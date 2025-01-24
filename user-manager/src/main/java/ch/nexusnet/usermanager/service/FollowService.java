package ch.nexusnet.usermanager.service;

import ch.nexusnet.usermanager.aws.dynamodb.model.mapper.UserInfoToUserSummaryMapper;
import ch.nexusnet.usermanager.aws.dynamodb.model.table.Follow;
import ch.nexusnet.usermanager.aws.dynamodb.model.table.UserInfo;
import ch.nexusnet.usermanager.aws.dynamodb.repositories.FollowRepository;
import ch.nexusnet.usermanager.aws.dynamodb.repositories.UserInfoRepository;
import ch.nexusnet.usermanager.service.exceptions.FileDoesNotExistException;
import ch.nexusnet.usermanager.service.exceptions.UserNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.model.UserSummary;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
@Slf4j
public class FollowService {

    private final FollowRepository followRepository;
    private final UserInfoRepository userInfoRepository;
    private final UserService userService;

    /**
     * Returns a list of all follows in the database.
     * @return A list of all follows in the database.
     */
    public List<List<UserSummary>> getAllFollows() {
        List<List<UserSummary>> userSummaries = new ArrayList<>();

        Iterable<Follow> followsIterable = followRepository.findAll();
        followsIterable.forEach(element -> {
            List<UserSummary> currentFollowConnection = new ArrayList<>();
            currentFollowConnection.add(getUserSummaryFromFollow(element.getUserId()));
            currentFollowConnection.add(getUserSummaryFromFollow(element.getFollowsUserId()));
            userSummaries.add(currentFollowConnection);
        });

        return userSummaries;
    }

    /**
     * Follows a user.
     * @param userId The user ID of the user who wants to follow another user.
     * @param followsUserId The user ID of the user who will be followed.
     * @return Follow The follow object that was created.
     * @throws UserNotFoundException If the user does not exist.
     */
    public Follow followUser(String userId, String followsUserId) throws UserNotFoundException {
        throwExceptionIfUserDoesNotExist(userId);
        throwExceptionIfUserDoesNotExist(followsUserId);
        Follow follow = new Follow();
        follow.setId(UUID.randomUUID().toString());
        follow.setUserId(userId);
        follow.setFollowsUserId(followsUserId);
        return followRepository.save(follow);
    }

    /**
     * Unfollows a user.
     * @param userId The user ID of the user who wants to unfollow another user.
     * @param followsUserId The user ID of the user who will be unfollowed.
     */
    public void unfollowUser(String userId, String followsUserId) {
        Follow follow = followRepository.findByUserIdAndFollowsUserId(userId, followsUserId);
        if (follow != null) {
            followRepository.delete(follow);
        }
    }

    /**
     * Returns a list of all users that a user follows.
     * @param userId The user ID of the user.
     * @return List<UserSummary> A list of all users that a user follows.
     */
    public List<UserSummary> getFollows(String userId) {
        List<Follow> followIds = followRepository.findAllByUserId(userId);
        return getUserSummariesForFollows(followIds);
    }

    /**
     * Returns a list of all users that follow a user.
     * @param userId The user ID of the user.
     * @return List<UserSummary> A list of all users that follow a user.
     */
    public List<UserSummary> getFollowers(String userId) {
        List<Follow> followIds = followRepository.findAllByFollowsUserId(userId);
        return getUserSummariesForFollowers(followIds);
    }

    private List<UserSummary> getUserSummariesForFollows(List<Follow> follows) {
        // Extract user IDs from follows and fetch UserInfo objects in bulk
        return getUserSummaries(follows.stream()
                .map(Follow::getFollowsUserId));
    }

    private List<UserSummary> getUserSummariesForFollowers(List<Follow> follows) {
        // Extract user IDs from follows and fetch UserInfo objects in bulk
        return getUserSummaries(follows.stream()
                .map(Follow::getUserId));
    }

    @NotNull
    private List<UserSummary> getUserSummaries(Stream<String> stringStream) {
        List<String> userIds;
        userIds = stringStream.toList();
        Iterable<UserInfo> usersInfo = userInfoRepository.findAllById(userIds);

        List<UserSummary> userSummaries = new ArrayList<>();
        usersInfo.forEach(element -> userSummaries.add(createUserSummary(element)));
        return userSummaries;
    }

    private UserSummary getUserSummaryFromFollow(String userId) {
        Optional<UserInfo> userInfo = userInfoRepository.findById(userId);
        if (userInfo.isEmpty()) {
            throwUserNotFoundException(userId);
        }
        return createUserSummary(userInfo.get());
    }

    private UserSummary createUserSummary(UserInfo userInfo) {
        UserSummary userSummary = UserInfoToUserSummaryMapper.map(userInfo);
        try {
            URL url = userService.getProfilePicture(userInfo.getId());
            userSummary.setProfilePicture(url.toString());
        } catch (UserNotFoundException e) {
            log.info(getUserNotFoundByIdMessage(userInfo.getId()));
        } catch (FileDoesNotExistException e) {
            log.info(getFileNotFoundByIdMessage(userInfo.getId()));
        }
        return userSummary;
    }

    private void throwExceptionIfUserDoesNotExist(String userId) throws UserNotFoundException {
        if (!userInfoRepository.existsById(userId)) {
            throwUserNotFoundException(userId);
        }
    }

    private void throwUserNotFoundException(String userId) {
        String userInformationMessage = getUserNotFoundByIdMessage(userId);
        log.info(userInformationMessage);
        throw new UserNotFoundException(userInformationMessage);
    }

    private String getUserNotFoundByIdMessage(String userId) {
        return "User with user id " + userId + " was not found.";
    }

    private String getFileNotFoundByIdMessage(String userId) {
        return "File for user with id " + userId + " was not found.";
    }
}