package ch.nexusnet.postmanager.service;

import ch.nexusnet.postmanager.aws.dynamodb.model.table.DynamoDBComment;
import ch.nexusnet.postmanager.aws.dynamodb.repositories.DynamoDBCommentRepository;
import ch.nexusnet.postmanager.aws.dynamodb.repositories.DynamoDBLikeRepository;
import ch.nexusnet.postmanager.exception.ResourceNotFoundException;
import ch.nexusnet.postmanager.model.Comment;
import ch.nexusnet.postmanager.model.dto.CreateCommentDTO;
import ch.nexusnet.postmanager.model.dto.UpdateCommentDTO;
import ch.nexusnet.postmanager.util.IdGenerator;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    private final static String POST_ID = IdGenerator.generatePostId();
    private final static String AUTHOR_ID = UUID.randomUUID().toString();
    private final static String COMMENT_ID = IdGenerator.generateCommentId();
    @Mock
    private DynamoDBCommentRepository dynamoDBCommentRepository;

    @Mock
    private DynamoDBLikeRepository dynamoDBLikeRepository;

    @Mock
    private AmazonDynamoDB amazonDynamoDB;
    private CommentServiceImpl commentService;

    @BeforeEach
    public void setUp() {
        commentService = new CommentServiceImpl(dynamoDBCommentRepository, dynamoDBLikeRepository, amazonDynamoDB);
    }

    @Test
    void createComment() {
        CreateCommentDTO commentDTO = new CreateCommentDTO();
        commentDTO.setContent("Test Comment");
        commentDTO.setPostId(POST_ID);
        commentDTO.setAuthorId(AUTHOR_ID);

        DynamoDBComment dynamoDBComment = new DynamoDBComment();
        dynamoDBComment.setContent("Test Comment");
        dynamoDBComment.setId(COMMENT_ID);
        dynamoDBComment.setPostId(POST_ID);
        dynamoDBComment.setAuthorId(AUTHOR_ID);

        Mockito.when(dynamoDBCommentRepository.save(Mockito.any(DynamoDBComment.class))).thenReturn(dynamoDBComment);

        Comment result = commentService.createComment(commentDTO);

        assertEquals(commentDTO.getContent(), result.getContent());
        assertEquals(commentDTO.getPostId(), result.getPostId());
        assertEquals(commentDTO.getAuthorId(), result.getAuthorId());
        Mockito.verify(dynamoDBCommentRepository).save(Mockito.any(DynamoDBComment.class));
        Mockito.verify(amazonDynamoDB).updateItem(Mockito.any(UpdateItemRequest.class));
    }

    @Test
    void shouldFindAllCommentsByPostId() {
        List<DynamoDBComment> comments = new ArrayList<>();
        comments.add(new DynamoDBComment());
        Mockito.when(dynamoDBCommentRepository.findByPostId(POST_ID)).thenReturn(comments);

        List<Comment> actualComments = commentService.findAllCommentsByPostId(POST_ID);

        assertEquals(1, actualComments.size());
        Mockito.verify(dynamoDBCommentRepository).findByPostId(anyString());
    }

    @Test
    void shouldReturnEmptyListWhenFindAllCommentsByPostIdForPostWithoutComments() {
        List<DynamoDBComment> comments = new ArrayList<>();
        Mockito.when(dynamoDBCommentRepository.findByPostId(POST_ID)).thenReturn(comments);

        List<Comment> actualComments = commentService.findAllCommentsByPostId(POST_ID);

        assertTrue(actualComments.isEmpty());
        Mockito.verify(dynamoDBCommentRepository).findByPostId(POST_ID);
    }

    @Test
    void shouldFindAllCommentsByAuthorId() {
        List<DynamoDBComment> comments = new ArrayList<>();
        comments.add(new DynamoDBComment());
        Mockito.when(dynamoDBCommentRepository.findByAuthorId(anyString())).thenReturn(comments);

        List<Comment> actualComments = commentService.findAllCommentsByAuthorId(AUTHOR_ID);

        assertEquals(1, actualComments.size());
        Mockito.verify(dynamoDBCommentRepository).findByAuthorId(anyString());
    }

    @Test
    void shouldReturnEmptyListWhenFindAllCommentsByAuthorIdForAuthorWithoutComments() {
        List<DynamoDBComment> comments = new ArrayList<>();
        Mockito.when(dynamoDBCommentRepository.findByAuthorId(anyString())).thenReturn(comments);

        List<Comment> actualComments = commentService.findAllCommentsByAuthorId(AUTHOR_ID);

        assertTrue(actualComments.isEmpty());
        Mockito.verify(dynamoDBCommentRepository).findByAuthorId(anyString());
    }

    @Test
    void testUpdateComment() {
        DynamoDBComment existingComment = new DynamoDBComment();
        existingComment.setId(COMMENT_ID);
        existingComment.setContent("Initial Content");

        DynamoDBComment updatedComment = new DynamoDBComment();
        updatedComment.setId(COMMENT_ID);
        updatedComment.setContent("Updated Content");

        UpdateCommentDTO updateCommentDto = new UpdateCommentDTO();
        updateCommentDto.setContent("Updated Content");

        Mockito.when(dynamoDBCommentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(existingComment));
        Mockito.when(dynamoDBCommentRepository.save(any(DynamoDBComment.class))).thenReturn(updatedComment);

        Comment result = commentService.updateComment(COMMENT_ID, updateCommentDto);

        assertEquals("Updated Content", result.getContent());
        assertEquals(COMMENT_ID, result.getId());
    }

    @Test
    void testUpdateCommentNotFound() {
        UpdateCommentDTO updateCommentDto = new UpdateCommentDTO();
        updateCommentDto.setContent("Updated Content");

        Mockito.when(dynamoDBCommentRepository.findById(COMMENT_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> commentService.updateComment(COMMENT_ID, updateCommentDto));
    }

    @Test
    void shouldDeleteComment() {
        DynamoDBComment dbComment = new DynamoDBComment();
        dbComment.setPostId(POST_ID);

        Mockito.when(dynamoDBCommentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(dbComment));

        commentService.deleteComment(COMMENT_ID);

        Mockito.verify(dynamoDBCommentRepository).deleteById(COMMENT_ID);
        Mockito.verify(amazonDynamoDB).updateItem(any(UpdateItemRequest.class));
    }

    @Test
    void shouldThrowExceptionWhenDeleteCommentDueNoComment() {
        Mockito.when(dynamoDBCommentRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> commentService.deleteComment(COMMENT_ID));
    }
}