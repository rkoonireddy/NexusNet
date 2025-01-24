package ch.nexusnet.postmanager.controller;

import ch.nexusnet.postmanager.aws.dynamodb.model.mapper.CommentMapper;
import ch.nexusnet.postmanager.aws.dynamodb.model.table.DynamoDBComment;
import ch.nexusnet.postmanager.aws.dynamodb.repositories.DynamoDBCommentRepository;
import ch.nexusnet.postmanager.model.Comment;
import ch.nexusnet.postmanager.model.dto.CreateCommentDTO;
import ch.nexusnet.postmanager.model.dto.UpdateCommentDTO;
import ch.nexusnet.postmanager.util.IdGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CommentControllerIntegrationTests {

    private static final String CONTENT = "Initial Comment Content";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private DynamoDBCommentRepository dynamoDBCommentRepository;
    private String postId;
    private String authorId;

    private Comment savedComment;

    @BeforeEach
    public void setup() {
        postId = IdGenerator.generatePostId();
        authorId = UUID.randomUUID().toString();

        DynamoDBComment dynamoDBComment = new DynamoDBComment();
        dynamoDBComment.setPostId(postId);
        dynamoDBComment.setId(IdGenerator.generateCommentId());
        dynamoDBComment.setAuthorId(authorId);
        dynamoDBComment.setContent(CONTENT);

        dynamoDBComment = dynamoDBCommentRepository.save(dynamoDBComment);
        savedComment = CommentMapper.convertDynamoDBCommentToComment(dynamoDBComment);
    }

    @AfterEach
    public void tearDown() {
        dynamoDBCommentRepository.deleteAll();
    }

    private String toJson(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    @Test
    void testAddComment() throws Exception {
        CreateCommentDTO createCommentDTO = new CreateCommentDTO();
        createCommentDTO.setPostId(postId);
        createCommentDTO.setAuthorId(authorId);
        createCommentDTO.setContent("This is a test comment.");

        mockMvc.perform(post("/comments/posts/{postId}", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(createCommentDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.postId").value(postId))
                .andExpect(jsonPath("$.authorId").value(authorId))
                .andExpect(jsonPath("$.content").value("This is a test comment."));
    }

    @Test
    void testGetAllCommentsByPostId() throws Exception {
        mockMvc.perform(get("/comments/posts/{postId}", postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testGetAllCommentsByAuthorId() throws Exception {
        mockMvc.perform(get("/comments/author/{authorId}", authorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testUpdateComment() throws Exception {
        UpdateCommentDTO updateCommentDTO = new UpdateCommentDTO();
        updateCommentDTO.setContent("Updated content for the comment.");

        String commentId = savedComment.getId();

        mockMvc.perform(put("/comments/{commentId}", commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(updateCommentDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentId))
                .andExpect(jsonPath("$.content").value("Updated content for the comment."));
    }

    @Test
    void testDeleteComment() throws Exception {
        String commentId = savedComment.getId();

        mockMvc.perform(delete("/comments/{commentId}", commentId))
                .andExpect(status().isOk());
    }
}
