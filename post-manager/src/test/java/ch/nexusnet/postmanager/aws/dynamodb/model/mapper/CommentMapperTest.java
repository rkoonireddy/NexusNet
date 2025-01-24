package ch.nexusnet.postmanager.aws.dynamodb.model.mapper;

import ch.nexusnet.postmanager.aws.dynamodb.model.table.DynamoDBComment;
import ch.nexusnet.postmanager.model.Comment;
import ch.nexusnet.postmanager.model.dto.CreateCommentDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CommentMapperTest {

    private DynamoDBComment dynamoDBComment;
    private CreateCommentDTO createCommentDTO;

    @BeforeEach
    void setUp() {
        dynamoDBComment = new DynamoDBComment();
        dynamoDBComment.setId("1");
        dynamoDBComment.setPostId("post1");
        dynamoDBComment.setAuthorId("author1");
        dynamoDBComment.setContent("content");
        dynamoDBComment.setLikeNumber(10);
        dynamoDBComment.setCreatedDateTime("2022-01-01T00:00:00");

        createCommentDTO = new CreateCommentDTO();
        createCommentDTO.setPostId("post1");
        createCommentDTO.setAuthorId("author1");
        createCommentDTO.setContent("content");
    }

    @Test
    void convertDynamoDBCommentToCommentReturnsCorrectComment() {
        Comment comment = CommentMapper.convertDynamoDBCommentToComment(dynamoDBComment);
        assertEquals(dynamoDBComment.getId(), comment.getId());
        assertEquals(dynamoDBComment.getPostId(), comment.getPostId());
        assertEquals(dynamoDBComment.getAuthorId(), comment.getAuthorId());
        assertEquals(dynamoDBComment.getContent(), comment.getContent());
        assertEquals(dynamoDBComment.getLikeNumber(), comment.getLikeNumber());
        assertEquals(dynamoDBComment.getCreatedDateTime(), comment.getCreatedAt());
    }

    @Test
    void convertDynamoDBCommentToCommentWithNullInputReturnsNull() {
        assertNull(CommentMapper.convertDynamoDBCommentToComment(null));
    }

    @Test
    void convertCreateCommentDTOToDynamoDBCommentReturnsCorrectDynamoDBComment() {
        DynamoDBComment dynamoDBComment = CommentMapper.convertCreateCommentDTOToDynamoDBComment(createCommentDTO);
        assertEquals(createCommentDTO.getPostId(), dynamoDBComment.getPostId());
        assertEquals(createCommentDTO.getAuthorId(), dynamoDBComment.getAuthorId());
        assertEquals(createCommentDTO.getContent(), dynamoDBComment.getContent());
    }

    @Test
    void convertCreateCommentDTOToDynamoDBCommentWithNullInputReturnsNull() {
        assertNull(CommentMapper.convertCreateCommentDTOToDynamoDBComment(null));
    }
}