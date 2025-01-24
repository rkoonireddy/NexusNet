package ch.nexusnet.postmanager.controller;

import ch.nexusnet.postmanager.aws.dynamodb.model.table.DynamoDBComment;
import ch.nexusnet.postmanager.aws.dynamodb.model.table.DynamoDBLike;
import ch.nexusnet.postmanager.aws.dynamodb.model.table.DynamoDBPost;
import ch.nexusnet.postmanager.aws.dynamodb.repositories.DynamoDBCommentRepository;
import ch.nexusnet.postmanager.aws.dynamodb.repositories.DynamoDBLikeRepository;
import ch.nexusnet.postmanager.aws.dynamodb.repositories.DynamoDBPostRepository;
import ch.nexusnet.postmanager.model.LikeTargetType;
import ch.nexusnet.postmanager.model.PostStatus;
import ch.nexusnet.postmanager.model.PostType;
import ch.nexusnet.postmanager.util.IdGenerator;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LikeControllerIntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DynamoDBCommentRepository dynamoDBCommentRepository;

    @Autowired
    private DynamoDBLikeRepository dynamoDBLikeRepository;

    @Autowired
    private DynamoDBPostRepository dynamoDBPostRepository;

    private String userId;
    private String postId;

    private String commentId;

    private String createUrl(String type, String id, String userId) {
        return String.format("/likes/%s/%s?userId=%s", type, id, userId);
    }

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID().toString();

        DynamoDBPost dynamoDBPost = new DynamoDBPost();
        dynamoDBPost.setId(IdGenerator.generatePostId());
        dynamoDBPost.setAuthorId(userId);
        dynamoDBPost.setLikeNumber(1);
        dynamoDBPost.setType(PostType.PROJECT.name());
        dynamoDBPost.setStatus(PostStatus.NEW.name());
        dynamoDBPost = dynamoDBPostRepository.save(dynamoDBPost);
        postId = dynamoDBPost.getId();

        DynamoDBComment dynamoDBComment = new DynamoDBComment();
        dynamoDBComment.setId(IdGenerator.generateCommentId());
        dynamoDBComment.setAuthorId(userId);
        dynamoDBComment.setPostId(postId);
        dynamoDBComment = dynamoDBCommentRepository.save(dynamoDBComment);
        commentId = dynamoDBComment.getId();

        DynamoDBLike dynamoDBLike = new DynamoDBLike();
        dynamoDBLike.setId(IdGenerator.generateLikeId());
        dynamoDBLike.setUserId(userId);
        dynamoDBLike.setTargetType(LikeTargetType.POST.name());
        dynamoDBLike.setTargetId(postId);
        dynamoDBLike.setTimestamp(LocalDateTime.now().toString());
        dynamoDBLikeRepository.save(dynamoDBLike);
    }

    @AfterEach
    void tearDown() {
        dynamoDBLikeRepository.deleteAll();
        dynamoDBPostRepository.deleteAll();
        dynamoDBCommentRepository.deleteAll();
    }

    @Test
    void likePost() throws Exception {
        mockMvc.perform(post(createUrl("post", postId, userId)))
                .andExpect(status().isOk());

        DynamoDBPost likedPost = dynamoDBPostRepository.findById(postId).orElse(null);
        assertNotNull(likedPost);
        assertEquals(2, likedPost.getLikeNumber());

    }

    @Test
    void unlikePost() throws Exception {
        mockMvc.perform(delete(createUrl("post", postId, userId)))
                .andExpect(status().isOk());

        DynamoDBPost likedPost = dynamoDBPostRepository.findById(postId).orElse(null);
        assertNotNull(likedPost);
        assertEquals(0, likedPost.getLikeNumber());
    }

    @Test
    void likeComment() throws Exception {
        mockMvc.perform(post(createUrl("comment", commentId, userId)))
                .andExpect(status().isOk());
    }

    @Test
    void unlikeComment() throws Exception {
        mockMvc.perform(delete(createUrl("comment", commentId, userId)))
                .andExpect(status().isOk());
    }

    @Test
    void getLikedPosts() throws Exception {
        mockMvc.perform(get("/likes/user/" + userId + "/posts")).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON));

    }

    @Test
    void hasUserLikedPostTrue() throws Exception {
        mockMvc.perform(get("/likes/post/" + postId + "/hasLiked?userId=" + userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("true"));
    }

    @Test
    void hasUserLikedPostFalse() throws Exception {
        String notLikedPostId = "123";
        mockMvc.perform(get("/likes/post/" + notLikedPostId + "/hasLiked?userId=" + userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("false"));
    }

    @Test
    void hasUserLikedComment() throws Exception {
        String notLikedCommentId = "123";
        mockMvc.perform(get("/likes/comment/" + notLikedCommentId + "/hasLiked?userId=" + userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("false"));
    }
}