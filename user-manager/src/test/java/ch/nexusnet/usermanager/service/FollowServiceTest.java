package ch.nexusnet.usermanager.service;

import ch.nexusnet.usermanager.aws.dynamodb.model.table.Follow;
import ch.nexusnet.usermanager.aws.dynamodb.repositories.FollowRepository;
import ch.nexusnet.usermanager.aws.dynamodb.repositories.UserInfoRepository;
import ch.nexusnet.usermanager.service.exceptions.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FollowServiceTest {

    FollowService followService;

    FollowRepository followRepositoryMock;
    UserInfoRepository userInfoRepositoryMock;
    UserService userServiceMock;

    @BeforeEach
    void setUp() {
        followRepositoryMock = mock(FollowRepository.class);
        userInfoRepositoryMock = mock(UserInfoRepository.class);
        userServiceMock = mock(UserService.class);
        followService = new FollowService(followRepositoryMock, userInfoRepositoryMock, userServiceMock);
    }

    @Test
    void followUser_expectSuccess() {
        // arrange
        Follow follow = new Follow();
        String userId = UUID.randomUUID().toString();
        String followUserId = UUID.randomUUID().toString();
        follow.setUserId(userId);
        follow.setFollowsUserId(followUserId);
        when(followRepositoryMock.save(any())).thenReturn(follow);
        when(userInfoRepositoryMock.existsById(userId)).thenReturn(true);
        when(userInfoRepositoryMock.existsById(followUserId)).thenReturn(true);

        // assert
        assertDoesNotThrow(() -> followService.followUser(userId, followUserId));
        verify(followRepositoryMock, times(1)).save(any());
        verify(userInfoRepositoryMock, times(1)).existsById(userId);
        verify(userInfoRepositoryMock, times(1)).existsById(followUserId);
    }

    @Test
    void whenFollowUserCalledWithNonExistentUserId_throwException() {
        // arrange
        String userId = "userId";
        String followUserId = "followUserId";

        // act & assert
        verify(followRepositoryMock, times(0)).save(any());
        assertThrows(UserNotFoundException.class, () -> followService.followUser(userId, followUserId));
    }
}