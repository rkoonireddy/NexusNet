package ch.nexusnet.usermanager.service;

import ch.nexusnet.usermanager.aws.dynamodb.model.mapper.UserInfoToUserSummaryMapper;
import ch.nexusnet.usermanager.aws.dynamodb.model.mapper.UserToUserInfoMapper;
import ch.nexusnet.usermanager.aws.dynamodb.model.table.UserInfo;
import ch.nexusnet.usermanager.aws.dynamodb.repositories.UserInfoRepository;
import ch.nexusnet.usermanager.aws.s3.client.S3Client;
import ch.nexusnet.usermanager.service.exceptions.UserAlreadyExistsException;
import ch.nexusnet.usermanager.service.exceptions.UserNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openapitools.model.UpdateUser;
import org.openapitools.model.User;
import org.openapitools.model.UserSummary;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @InjectMocks
    UserService userService;

    @Mock
    UserInfoRepository userInfoRepositoryMock;

    @Mock
    S3Client s3ClientMock;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @ParameterizedTest
    @MethodSource("getUsers")
    void createNewUser_expectSuccess(User testUser) {
        // arrange
        when(userInfoRepositoryMock.findUserInfoByUsername(any(String.class))).thenReturn(new ArrayList<>());
        when(userInfoRepositoryMock.save(any(UserInfo.class))).thenReturn(UserToUserInfoMapper.map(getUserWithId()));

        // act
        User createdUser = userService.createUser(testUser);

        // assert
        assertNotNull(createdUser.getId());
    }

    @ParameterizedTest
    @MethodSource("getUsers")
    void getUsers(User testUser) {
        // arrange
        when(userInfoRepositoryMock.findAll()).thenReturn(List.of(UserToUserInfoMapper.map(testUser)));

        // act
        List<UserSummary> users = userService.getUsers();

        // assert
        assertEquals(1, users.size());
        assertEquals(UserInfoToUserSummaryMapper.map(UserToUserInfoMapper.map(testUser)).getId(), users.get(0).getId());
    }


    @ParameterizedTest
    @MethodSource("getUsers")
    void createNewUser_expectFailure(User testUser) {
        // arrange
        when(userInfoRepositoryMock.findUserInfoByUsername(any(String.class))).thenReturn(List.of((UserToUserInfoMapper.map(testUser))));

        // act & assert
        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(testUser));
    }

    @Test
    void getUserByUserId_expectSuccess() {
        // arrange
        User testUser = getUserWithId();
        when(userInfoRepositoryMock.findById(any(String.class))).thenReturn(Optional.of(UserToUserInfoMapper.map(testUser)));

        // act
        User foundUser = userService.getUserByUserId(testUser.getId().toString());

        // assert
        assertEquals(testUser.getId(), foundUser.getId());
    }

    @Test
    void getUserByUserId_expectFailure() {
        // arrange
        String userId = UUID.randomUUID().toString();
        when(userInfoRepositoryMock.findById(any(String.class))).thenReturn(Optional.empty());

        // act & assert
        assertThrows(UserNotFoundException.class, () -> userService.getUserByUserId(userId));
    }

    @Test
    void getUserByUsername_expectSuccess() {
        // arrange
        User testUser = getUserWithId();
        when(userInfoRepositoryMock.findUserInfoByUsername(any(String.class))).thenReturn(List.of((UserToUserInfoMapper.map(testUser))));

        // act
        User foundUser = userService.getUserByUsername(testUser.getUsername());

        // assert
        assertEquals(testUser.getId(), foundUser.getId());
    }

    @Test
    void getUserByUsername_expectFailure() {
        // arrange
        // when(userInfoRepositoryMock.findUserInfoByUsername(any(String.class))).thenReturn(Optional.empty());

        // act & assert
        assertThrows(UserNotFoundException.class, () -> userService.getUserByUsername("nonexistent"));
    }

    @ParameterizedTest
    @MethodSource("getUpdateUsers")
    void updateUser_expectSuccess(UpdateUser updateUser) {
        // arrange
        User testUser = getUserWithId();

        UserInfo expectedUpdatedUser;
        if (updateUser.getFirstName() != null) {
            expectedUpdatedUser = getExpectedUserInfo(updateUser);
        } else {
            expectedUpdatedUser = new UserInfo();
            expectedUpdatedUser.setFirstName(testUser.getFirstName());
            expectedUpdatedUser.setLastName(testUser.getLastName());
            expectedUpdatedUser.setUsername(testUser.getUsername());
        }

        when(userInfoRepositoryMock.findById(any(String.class))).thenReturn(Optional.of(UserToUserInfoMapper.map(testUser)));

        ArgumentCaptor<UserInfo> captor = ArgumentCaptor.forClass(UserInfo.class);

        // act
        userService.updateUser(testUser.getId().toString(), updateUser);

        // assert
        verify(userInfoRepositoryMock).save(captor.capture());
        assertEquals(expectedUpdatedUser.getFirstName(), captor.getValue().getFirstName());
        assertEquals(expectedUpdatedUser.getLastName(), captor.getValue().getLastName());
        assertEquals(expectedUpdatedUser.getUsername(), captor.getValue().getUsername());
        assertEquals(expectedUpdatedUser.getUniversity(), captor.getValue().getUniversity());
        assertEquals(expectedUpdatedUser.getBio(), captor.getValue().getBio());
        assertEquals(expectedUpdatedUser.getDegreeProgram(), captor.getValue().getDegreeProgram());
        assertEquals(expectedUpdatedUser.getBirthday(), captor.getValue().getBirthday());
        assertEquals(expectedUpdatedUser.getPrivateProfile(), captor.getValue().getPrivateProfile());
    }

    @Test
    void updateNonExistentUser_expectFailure() {
        // arrange
        UpdateUser updateUser = getUpdateUser();
        when(userInfoRepositoryMock.findById(any(String.class))).thenReturn(Optional.empty());

        // act & assert
        assertThrows(UserNotFoundException.class, () -> userService.updateUser("nonexistent", updateUser));
    }

    @Test
    void deleteUser_expectSuccess() {
        // arrange
        User testUser = getUserWithId();
        when(userInfoRepositoryMock.existsById(any(String.class))).thenReturn(true);

        // act
        userService.deleteUser(testUser.getId().toString());

        // assert
        verify(userInfoRepositoryMock, times(1)).deleteById(testUser.getId().toString());
    }

    @Test
    void deleteNonExistentUser_expectFailure() {
        // arrange
        // arrange
        User testUser = getUserWithId();
        when(userInfoRepositoryMock.existsById(any(String.class))).thenReturn(false);

        // act & assert
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser("nonexistent"));
    }

    @Test
    void uploadFile_expectSuccess() throws IOException, URISyntaxException {
        // arrange
        MultipartFile file = mock(MultipartFile.class);
        User testUser = getUserWithId();
        when(userInfoRepositoryMock.existsById(testUser.getId().toString())).thenReturn(true);
        when(s3ClientMock.uploadFileToS3(testUser.getId().toString(), file)).thenReturn(new URI("http://example.com").toURL());

        // act
        userService.uploadFile(testUser.getId().toString(), file);

        // assert
        verify(userInfoRepositoryMock, times(1)).existsById(testUser.getId().toString());
    }

    @Test
    void uploadFileWithNonExistentUser_expectFailure() {
        // arrange
        MultipartFile file = mock(MultipartFile.class);
        User testUser = getUserWithId();
        String userId = testUser.getId().toString();
        when(userInfoRepositoryMock.existsById(testUser.getId().toString())).thenReturn(false);

        // act & assert
        assertThrows(UserNotFoundException.class, () -> userService.uploadFile(userId, file));
    }

    @Test
    void getProfilePicture_expectSuccess() throws URISyntaxException, MalformedURLException {
        // arrange
        User testUser = getUserWithId();
        when(userInfoRepositoryMock.existsById(testUser.getId().toString())).thenReturn(true);
        when(s3ClientMock.getProfilePictureFromS3(testUser.getId().toString())).thenReturn(new URI("http://example.com").toURL());

        // act
        userService.getProfilePicture(testUser.getId().toString());

        // assert
        verify(userInfoRepositoryMock, times(1)).existsById(testUser.getId().toString());
    }

    @Test
    void getProfilePictureWithNonExistentUser_expectFailure() {
        // arrange
        User testUser = getUserWithId();
        String userId = testUser.getId().toString();
        when(userInfoRepositoryMock.existsById(testUser.getId().toString())).thenReturn(false);

        // act & assert
        assertThrows(UserNotFoundException.class, () -> userService.getProfilePicture(userId));
    }

    @Test
    void getResume_expectSuccess() throws URISyntaxException, MalformedURLException {
        // arrange
        User testUser = getUserWithId();
        when(userInfoRepositoryMock.existsById(testUser.getId().toString())).thenReturn(true);
        when(s3ClientMock.getResumeFromS3(testUser.getId().toString())).thenReturn(new URI("http://example.com").toURL());

        // act
        userService.getResume(testUser.getId().toString());

        // assert
        verify(userInfoRepositoryMock, times(1)).existsById(testUser.getId().toString());
    }

    @Test
    void getResumeWithNonExistentUser_expectFailure() {
        // arrange
        User testUser = getUserWithId();
        String userId = testUser.getId().toString();
        when(userInfoRepositoryMock.existsById(testUser.getId().toString())).thenReturn(false);

        // act & assert
        assertThrows(UserNotFoundException.class, () -> userService.getResume(userId));
    }

    private static Stream<Arguments> getUsers() {
        return Stream.of(
            Arguments.of(getUserWithId()),
            Arguments.of(getUserWithoutId())
        );
    }

    public static User getUserWithoutId() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("jhnd");
        return user;
    }

    private static User getUserWithId() {
        User user = getUserWithoutId();
        user.setId(UUID.randomUUID());
        return user;
    }

    private static Stream<Arguments> getUpdateUsers() {
        return Stream.of(
                Arguments.of(getUpdateUser()),
                Arguments.of(new UpdateUser())
        );
    }

    private static UpdateUser getUpdateUser() {
        UpdateUser updateUser = new UpdateUser();
        updateUser.setFirstName("Max");
        updateUser.setLastName("Muserman");
        updateUser.setUsername("mamu");
        updateUser.setUniversity("ETH");
        updateUser.setBio("Master Student");
        updateUser.setDegreeProgram("AI");
        updateUser.setBirthday(LocalDate.of(2024, 3, 28));
        updateUser.privateProfile(true);
        return updateUser;
    }

    @NotNull
    private static UserInfo getExpectedUserInfo(UpdateUser updateUser) {
        UserInfo expectedUpdatedUser = new UserInfo();
        expectedUpdatedUser.setFirstName(updateUser.getFirstName());
        expectedUpdatedUser.setLastName(updateUser.getLastName());
        expectedUpdatedUser.setUsername(updateUser.getUsername());
        expectedUpdatedUser.setUniversity(updateUser.getUniversity());
        expectedUpdatedUser.setBio(updateUser.getBio());
        expectedUpdatedUser.setDegreeProgram(updateUser.getDegreeProgram());
        expectedUpdatedUser.setBirthday(updateUser.getBirthday().toString());
        expectedUpdatedUser.setPrivateProfile(updateUser.getPrivateProfile());
        return expectedUpdatedUser;
    }
}