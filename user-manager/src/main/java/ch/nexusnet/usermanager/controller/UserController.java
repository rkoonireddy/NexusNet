package ch.nexusnet.usermanager.controller;

import ch.nexusnet.usermanager.aws.dynamodb.model.table.Follow;
import ch.nexusnet.usermanager.service.FollowService;
import ch.nexusnet.usermanager.service.UserService;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.openapitools.api.UsersApi;
import org.openapitools.model.UpdateUser;
import org.openapitools.model.User;
import org.openapitools.model.UserSummary;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;


@Controller
@AllArgsConstructor
public class UserController implements UsersApi {

    private final UserService userService;
    private final FollowService followService;
    private static final String SERVICE_NOT_AVAILABLE = "Service not available.";

    @Override
    public ResponseEntity<List<UserSummary>> getUsers() {
        List<UserSummary> allUsers = userService.getUsers();
        return ResponseEntity.ok(allUsers);
    }

    @Override
    public ResponseEntity<User> createUser(User newUser) {
        User user;
        user = userService.createUser(newUser);
        URI location = URI.create("/users/" + user.getId());
        return ResponseEntity.created(location).body(user);
    }

    @Override
    public ResponseEntity<Void> deleteUser(String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok().build();
    }


    @Override
    public ResponseEntity<User> getUserById(String userId) {
        User user;
        user = userService.getUserByUserId(userId);
        return ResponseEntity.ok().body(user);
    }

    @Override
    public ResponseEntity<Void> updateUser(String userId, UpdateUser updateUser) {
        userService.updateUser(userId, updateUser);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<String> uploadProfilePicture(String userId, MultipartFile profilePicture) {
        return uploadFileAndRetrieveFileLocation(userId, profilePicture);
    }

    @Override
    public ResponseEntity<String> getProfilePicture(String userId) {
        try {
            URL location = userService.getProfilePicture(userId);
            return ResponseEntity.ok().body(location.toURI().toString());
        } catch (URISyntaxException e) {
            return ResponseEntity.internalServerError().body(SERVICE_NOT_AVAILABLE);
        }
    }

    @Override
    public ResponseEntity<String> uploadResume(String userId, MultipartFile resume) {
        return uploadFileAndRetrieveFileLocation(userId, resume);
    }

    @Override
    public ResponseEntity<String> getResume(String userId) {
        try {
            URL location = userService.getResume(userId);
            return ResponseEntity.ok().body(location.toURI().toString());

        } catch (URISyntaxException e) {
            return ResponseEntity.internalServerError().body(SERVICE_NOT_AVAILABLE);
        }
    }

    @Override
    public ResponseEntity<User> getUserByUsername(String username) {
        User user;
        user = userService.getUserByUsername(username);
        return ResponseEntity.ok().body(user);
    }

    @NotNull
    private ResponseEntity<String> uploadFileAndRetrieveFileLocation(String userId, MultipartFile resume) {
        try {
            URL location = userService.uploadFile(userId, resume);
            return ResponseEntity.created(location.toURI()).build();
        } catch (IOException | URISyntaxException e) {
            return ResponseEntity.internalServerError().body(SERVICE_NOT_AVAILABLE);
        }
    }

    @Override
    public ResponseEntity<String> followUser(String userId, String userToFollowId) {
        Follow follow = followService.followUser(userId, userToFollowId);
        URI location = URI.create("/users/" + follow.getUserId() + "follows/" + follow.getFollowsUserId());
        return ResponseEntity.created(location).build();
    }

    @Override
    public ResponseEntity<Void> unfollowUser(String userId, String userToFollowId) {
        followService.unfollowUser(userId, userToFollowId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<UserSummary>> getFollows(String userId) {
        List<UserSummary> follows = followService.getFollows(userId);
        return ResponseEntity.ok(follows);
    }

    @Override
    public ResponseEntity<List<UserSummary>> getFollowers(String userId) {
        List<UserSummary> follows = followService.getFollowers(userId);
        return ResponseEntity.ok(follows);
    }

    @Override
    public ResponseEntity<List<List<UserSummary>>> getAllFollows() {
        List<List<UserSummary>> allFollows = followService.getAllFollows();
        return ResponseEntity.ok(allFollows);
    }

}
