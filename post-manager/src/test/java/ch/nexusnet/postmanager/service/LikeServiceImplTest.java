package ch.nexusnet.postmanager.service;

import ch.nexusnet.postmanager.aws.dynamodb.model.table.DynamoDBLike;
import ch.nexusnet.postmanager.aws.dynamodb.model.table.DynamoDBPost;
import ch.nexusnet.postmanager.aws.dynamodb.repositories.DynamoDBCommentRepository;
import ch.nexusnet.postmanager.aws.dynamodb.repositories.DynamoDBLikeRepository;
import ch.nexusnet.postmanager.aws.dynamodb.repositories.DynamoDBPostRepository;
import ch.nexusnet.postmanager.model.LikeTargetType;
import ch.nexusnet.postmanager.model.Post;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LikeServiceImplTest {

    private static final String USER_ID = "user123";
    private static final String POST_ID = "post123";
    private static final String COMMENT_ID = "comment123";

    @Mock
    private DynamoDBLikeRepository dynamoDBLikeRepository;
    @Mock
    private DynamoDBPostRepository dynamoDBPostRepository;
    @Mock
    private DynamoDBCommentRepository dynamoDBCommentRepository;

    @Mock
    private AmazonDynamoDB amazonDynamoDB;

    private LikeServiceImpl likeService;

    @BeforeEach
    void setUp() {
        likeService = new LikeServiceImpl(dynamoDBLikeRepository, dynamoDBPostRepository, amazonDynamoDB, ZoneId.of("CET"));
    }

    @Test
    void shouldLikeExistingPost() {
        likeService.likePost(POST_ID, USER_ID);

        Mockito.verify(amazonDynamoDB).updateItem(any(UpdateItemRequest.class));
        verify(dynamoDBLikeRepository).save(any(DynamoDBLike.class));
    }

    @Test
    void shouldUnlikeExistingPost() {
        likeService.unlikePost(POST_ID, USER_ID);

        Mockito.verify(amazonDynamoDB).updateItem(any(UpdateItemRequest.class));
        verify(dynamoDBLikeRepository).deleteByTargetIdAndTargetTypeAndUserId(POST_ID, LikeTargetType.POST.name(), USER_ID);
    }

    @Test
    void shouldLikeExistingComment() {
        likeService.likeComment(COMMENT_ID, USER_ID);

        Mockito.verify(amazonDynamoDB).updateItem(any(UpdateItemRequest.class));
        verify(dynamoDBLikeRepository).save(any(DynamoDBLike.class));
    }

    @Test
    void shouldUnlikeExistingComment() {
        likeService.unlikeComment(COMMENT_ID, USER_ID);

        Mockito.verify(amazonDynamoDB).updateItem(any(UpdateItemRequest.class));
        verify(dynamoDBLikeRepository).deleteByTargetIdAndTargetTypeAndUserId(COMMENT_ID, LikeTargetType.COMMENT.name(), USER_ID);
    }

    @Test
    void shouldReturnLikedPostsSortedByRecency() {
        DynamoDBLike like1 = new DynamoDBLike();
        like1.setTargetId("post1");
        like1.setTimestamp(LocalDateTime.now().minusDays(1).toString());
        like1.setTargetType(LikeTargetType.POST.name());
        like1.setUserId(USER_ID);

        DynamoDBLike like2 = new DynamoDBLike();
        like2.setTargetId("post2");
        like2.setTimestamp(LocalDateTime.now().minusDays(2).toString());
        like2.setTargetType(LikeTargetType.POST.name());
        like2.setUserId(USER_ID);

        List<DynamoDBLike> likes = Arrays.asList(
                like1, like2
        );

        DynamoDBPost post1 = new DynamoDBPost();
        post1.setId("post1");
        DynamoDBPost post2 = new DynamoDBPost();
        post2.setId("post2");

        List<DynamoDBPost> posts = Arrays.asList(
                post1, post2
        );

        when(dynamoDBLikeRepository.findAllByTargetTypeAndUserId(LikeTargetType.POST.name(), USER_ID)).thenReturn(likes);
        when(dynamoDBPostRepository.findAllById(any())).thenReturn(posts);

        List<Post> resultPosts = likeService.getLikedPostsByUserSortedByRecency(USER_ID);

        assertThat(resultPosts).hasSize(2);
        assertThat(resultPosts.get(0).getId()).isEqualTo("post1");
        assertThat(resultPosts.get(1).getId()).isEqualTo("post2");
    }

    @Test
    void shouldReturnEmptyListWhenUserHasNotLikedAnything() {
        when(dynamoDBLikeRepository.findAllByTargetTypeAndUserId(LikeTargetType.POST.name(), USER_ID)).thenReturn(Collections.emptyList());

        List<Post> resultPosts = likeService.getLikedPostsByUserSortedByRecency(USER_ID);

        assertThat(resultPosts).isEmpty();
    }

    @Test
    void shouldReturnEmptyListDueToDeletedPost() {
        DynamoDBLike like = new DynamoDBLike();
        like.setTargetId("post1");
        like.setTimestamp(LocalDateTime.now().minusDays(1).toString());
        like.setTargetType(LikeTargetType.POST.name());
        like.setUserId(USER_ID);

        List<DynamoDBLike> likes = Collections.singletonList(
                like
        );

        when(dynamoDBLikeRepository.findAllByTargetTypeAndUserId(LikeTargetType.POST.name(), USER_ID)).thenReturn(likes);
        when(dynamoDBPostRepository.findAllById(any())).thenReturn(Collections.emptyList());

        List<Post> resultPosts = likeService.getLikedPostsByUserSortedByRecency(USER_ID);

        assertThat(resultPosts).isEmpty();
    }

    @Test
    void shouldReturnTrueWhenUserHasLikedPost() {
        when(dynamoDBLikeRepository.findByTargetIdAndTargetTypeAndUserId(POST_ID, LikeTargetType.POST.name(), USER_ID))
                .thenReturn(Optional.of(new DynamoDBLike()));

        assertTrue(likeService.checkUserLikeStatusForPost(POST_ID, USER_ID));
    }

    @Test
    void shouldReturnFalseWhenUserHasNotLikedPost() {
        when(dynamoDBLikeRepository.findByTargetIdAndTargetTypeAndUserId(POST_ID, LikeTargetType.POST.name(), USER_ID))
                .thenReturn(Optional.empty());

        assertFalse(likeService.checkUserLikeStatusForPost(POST_ID, USER_ID));
    }
}
