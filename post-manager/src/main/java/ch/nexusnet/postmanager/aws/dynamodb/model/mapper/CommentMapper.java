package ch.nexusnet.postmanager.aws.dynamodb.model.mapper;

import ch.nexusnet.postmanager.aws.dynamodb.model.table.DynamoDBComment;
import ch.nexusnet.postmanager.model.Comment;
import ch.nexusnet.postmanager.model.dto.CreateCommentDTO;

public class CommentMapper {

    private CommentMapper() {
    }

    public static Comment convertDynamoDBCommentToComment(DynamoDBComment dynamoDBComment) {
        if (dynamoDBComment == null) {
            return null;
        }
        Comment comment = new Comment();
        comment.setId(dynamoDBComment.getId());
        comment.setPostId(dynamoDBComment.getPostId());
        comment.setAuthorId(dynamoDBComment.getAuthorId());
        comment.setContent(dynamoDBComment.getContent());
        comment.setLikeNumber(dynamoDBComment.getLikeNumber());
        comment.setCreatedAt(dynamoDBComment.getCreatedDateTime());
        return comment;
    }

    public static DynamoDBComment convertCreateCommentDTOToDynamoDBComment(CreateCommentDTO createCommentDTO) {
        if (createCommentDTO == null) {
            return null;
        }
        DynamoDBComment dynamoDBComment = new DynamoDBComment();
        dynamoDBComment.setPostId(createCommentDTO.getPostId());
        dynamoDBComment.setAuthorId(createCommentDTO.getAuthorId());
        dynamoDBComment.setContent(createCommentDTO.getContent());
        return dynamoDBComment;
    }
}


