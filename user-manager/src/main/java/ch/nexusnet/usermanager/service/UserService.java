package ch.nexusnet.usermanager.service;

import ch.nexusnet.usermanager.aws.dynamodb.model.mapper.UserInfoToUserMapper;
import ch.nexusnet.usermanager.aws.dynamodb.model.mapper.UserInfoToUserSummaryMapper;
import ch.nexusnet.usermanager.aws.dynamodb.model.mapper.UserToUserInfoMapper;
import ch.nexusnet.usermanager.aws.dynamodb.model.table.UserInfo;
import ch.nexusnet.usermanager.aws.dynamodb.repositories.UserInfoRepository;
import ch.nexusnet.usermanager.aws.s3.client.S3Client;
import ch.nexusnet.usermanager.aws.s3.exceptions.UnsupportedFileTypeException;
import ch.nexusnet.usermanager.service.exceptions.FileDoesNotExistException;
import ch.nexusnet.usermanager.service.exceptions.UserAlreadyExistsException;
import ch.nexusnet.usermanager.service.exceptions.UserNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.model.UpdateUser;
import org.openapitools.model.User;
import org.openapitools.model.UserSummary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {

    private final UserInfoRepository userInfoRepository;
    private final S3Client s3Client;

    /**
     * Returns a list of all users in the database.
     * @return List<UserSummary> A list of all users in the database.
     */
    public List<UserSummary> getUsers() {
        Iterable<UserInfo> userInfos = userInfoRepository.findAll();
        return getUserSummariesFromUserInfos(userInfos);
    }

    /**
     * Creates a new user.
     * @param newUser The user to be created.
     * @return User The created user.
     * @throws UserAlreadyExistsException if the user already exists.
     */
    public User createUser(User newUser) throws UserAlreadyExistsException {
        if (newUser.getId() == null) {
            newUser.setId(UUID.randomUUID());
        }

        Optional<UserInfo> userInfo = findUserByUsername(newUser.getUsername());
        if (userInfo.isEmpty()) {
            UserInfo createdUserInfo = saveUserToDB(newUser);
            return mapUserInfoToUser(createdUserInfo);
        }
        String userInformationMessage = getUserAlreadyExistsMessage(userInfo.get().getUsername());
        log.info(userInformationMessage);
        throw new UserAlreadyExistsException(userInformationMessage);
    }

    /**
     * Returns a user by user id.
     * @param userId The user id of the user to be returned.
     * @return User The user with the given user id.
     * @throws UserNotFoundException if the user does not exist.
     */
    public User getUserByUserId(String userId) throws UserNotFoundException {
        UserInfo userInfo = findUserById(userId);
        return mapUserInfoToUser(userInfo);
    }

    /**
     * Returns a user by username.
     * @param username The username of the user to be returned.
     * @return User The user with the given username.
     * @throws UserNotFoundException if the user does not exist.
     */
    public User getUserByUsername(String username) throws UserNotFoundException {
        Optional<UserInfo> userInfo = findUserByUsername(username);
        if (userInfo.isPresent()) {
            return mapUserInfoToUser(userInfo.get());
        }
        String userInformationMessage = getUserNotFoundByUserNameMessage(username);
        log.info(userInformationMessage);
        throw new UserNotFoundException(userInformationMessage);
    }

    /**
     * Updates information about a user.
     * @param userId The user id of the user to be updated.
     * @param updateUser The updated user information.
     * @throws UserNotFoundException if the user does not exist.
     */
    public void updateUser(String userId, UpdateUser updateUser) throws UserNotFoundException {
        UserInfo userInfo = findUserById(userId);
        updateUserInfo(updateUser, userInfo);
        saveUserToDB(mapUserInfoToUser(userInfo));
    }

    /**
     * Deletes a user by user id.
     * @param userId The user id of the user to be deleted.
     * @throws UserNotFoundException if the user does not exist.
     */
    public void deleteUser(String userId) throws UserNotFoundException {
        throwExceptionIfUserDoesNotExist(userId);
        userInfoRepository.deleteById(userId);
    }

    /**
     * Uploads a file to S3.
     * @param userId The user id of the user uploading the file.
     * @param multipartFile The file to be uploaded.
     * @return URL The URL of the uploaded file.
     * @throws IOException If an I/O error occurs.
     * @throws UnsupportedFileTypeException If the file type is not supported.
     * @throws UserNotFoundException If the user does not exist.
     */
    public URL uploadFile(String userId, MultipartFile  multipartFile) throws IOException, UnsupportedFileTypeException, UserNotFoundException {
        throwExceptionIfUserDoesNotExist(userId);
        return s3Client.uploadFileToS3(userId, multipartFile);
    }

    /**
     * Gets the profile picture of a user.
     * @param userId The user id of the user whose profile picture is to be returned.
     * @return URL The URL of the profile picture.
     * @throws UserNotFoundException If the user does not exist.
     * @throws FileDoesNotExistException If the file does not exist.
     */
    public URL getProfilePicture(String userId) throws UserNotFoundException, FileDoesNotExistException {
        throwExceptionIfUserDoesNotExist(userId);
        URL profilePicturePath = s3Client.getProfilePictureFromS3(userId);
        throwExceptionIfFileDoesNotExist(profilePicturePath);
        return profilePicturePath;
    }

    /**
     * Gets the resume of a user.
     * @param userId The user id of the user whose resume is to be returned.
     * @return URL The URL of the resume.
     * @throws UserNotFoundException If the user does not exist.
     * @throws FileDoesNotExistException If the file does not exist.
     */
    public URL getResume(String userId) throws UserNotFoundException, FileDoesNotExistException {
        throwExceptionIfUserDoesNotExist(userId);
        URL resumePath = s3Client.getResumeFromS3(userId);
        throwExceptionIfFileDoesNotExist(resumePath);
        return resumePath;
    }

    private List<UserSummary> getUserSummariesFromUserInfos(Iterable<UserInfo> userInfos) {
        List<UserSummary> allUsers = new ArrayList<>();

        userInfos.forEach(element -> {
            UserSummary userSummary = UserInfoToUserSummaryMapper.map(element);
            try {
                URL url = getProfilePicture(element.getId());
                userSummary.setProfilePicture(url.toString());
            } catch (UserNotFoundException e) {
                log.info(getUserNotFoundByIdMessage(element.getId()));
            } catch (FileDoesNotExistException e) {
                log.info(getFileWasNotFoundMessage());
            }
            allUsers.add(userSummary);
        });

        return allUsers;
    }

    private UserInfo findUserById(String userId) {
        return userInfoRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(getUserNotFoundByIdMessage(userId)));
    }

    private Optional<UserInfo> findUserByUsername(String username) {
        List<UserInfo> userInfos = userInfoRepository.findUserInfoByUsername(username);
        log.info("found following users " + userInfos.toString());
        return userInfos.stream().findAny();
    }

    private UserInfo saveUserToDB(User user) {
        return userInfoRepository.save(mapUserToUserInfo(user));
    }

    private UserInfo mapUserToUserInfo(User user) {
        return UserToUserInfoMapper.map(user);
    }

    private User mapUserInfoToUser(UserInfo userInfo) {
        return UserInfoToUserMapper.map(userInfo);
    }

    private String getUserNotFoundByIdMessage(String userId) {
        return "User with user id " + userId + " was not found.";
    }

    private String getUserNotFoundByUserNameMessage(String username) {
        return "User with username " + username + " was not found.";
    }

    private String getUserAlreadyExistsMessage(String username) {
        return "User with username " + username + " already exists.";
    }

    private void throwExceptionIfUserDoesNotExist(String userId) throws UserNotFoundException {
        if (! userInfoRepository.existsById(userId)) {
            String userInformationMessage = getUserNotFoundByIdMessage(userId);
            log.info(userInformationMessage);
            throw new UserNotFoundException(userInformationMessage);
        }
    }

    private void throwExceptionIfFileDoesNotExist(URL filePath) throws FileDoesNotExistException {
        if (filePath == null) {
            String fileInformationMessage = getFileWasNotFoundMessage();
            log.info(fileInformationMessage);
            throw new FileDoesNotExistException(fileInformationMessage);
        }
    }

    private static String getFileWasNotFoundMessage() {
        return "File was not found.";
    }

    private void updateUserInfo(UpdateUser updateUser, UserInfo userInfo) {
        applyUpdate(updateUser.getFirstName(), userInfo::setFirstName);
        applyUpdate(updateUser.getLastName(), userInfo::setLastName);
        applyUpdate(updateUser.getUsername(), userInfo::setUsername);
        applyUpdate(updateUser.getEmail(), userInfo::setEmail);
        applyUpdate(updateUser.getMotto(), userInfo::setMotto);
        applyUpdate(updateUser.getUniversity(), userInfo::setUniversity);
        applyUpdate(updateUser.getBio(), userInfo::setBio);
        applyUpdate(updateUser.getDegreeProgram(), userInfo::setDegreeProgram);
        applyUpdate(updateUser.getBirthday(), birthday -> userInfo.setBirthday(birthday.toString()));

        if (updateUser.getPrivateProfile() != null) {
            userInfo.setPrivateProfile(updateUser.getPrivateProfile());
        }
    }

    private <T> void applyUpdate(T value, Consumer<T> updateFunction) {
        if (value != null) {
            updateFunction.accept(value);
        }
    }


}
