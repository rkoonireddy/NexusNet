package ch.nexusnet.postmanager.service;

import ch.nexusnet.postmanager.aws.dynamodb.model.mapper.CommentMapper;
import ch.nexusnet.postmanager.aws.dynamodb.model.table.DynamoDBComment;
import ch.nexusnet.postmanager.aws.dynamodb.repositories.DynamoDBCommentRepository;
import ch.nexusnet.postmanager.aws.dynamodb.repositories.DynamoDBLikeRepository;
import ch.nexusnet.postmanager.exception.ResourceNotFoundException;
import ch.nexusnet.postmanager.model.Comment;
import ch.nexusnet.postmanager.model.dto.CreateCommentDTO;
import ch.nexusnet.postmanager.model.dto.UpdateCommentDTO;
import ch.nexusnet.postmanager.util.IdGenerator;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeAction;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentServiceImpl implements CommentService {
    private final DynamoDBCommentRepository dynamoDBCommentRepository;

    private final DynamoDBLikeRepository dynamoDBLikeRepository;
    private final AmazonDynamoDB amazonDynamoDB;

    public CommentServiceImpl(DynamoDBCommentRepository dynamoDBCommentRepository, DynamoDBLikeRepository dynamoDBLikeRepository, AmazonDynamoDB amazonDynamoDB) {
        this.dynamoDBCommentRepository = dynamoDBCommentRepository;
        this.dynamoDBLikeRepository = dynamoDBLikeRepository;
        this.amazonDynamoDB = amazonDynamoDB;
    }

    /**
     * Creates a comment based on the specified CreateCommentDTO object.
     *
     * @param createCommentDTO the data transfer object containing the comment details
     * @return the created Comment object
     * @throws ResourceNotFoundException if the post with the specified postId is not found
     */
    @Override
    public Comment createComment(CreateCommentDTO createCommentDTO) {
        DynamoDBComment dynamoDBComment = CommentMapper.convertCreateCommentDTOToDynamoDBComment(createCommentDTO);
        dynamoDBComment.setId(IdGenerator.generateCommentId());

        DynamoDBComment savedComment = dynamoDBCommentRepository.save(dynamoDBComment);

        changePostsCommentNumber(createCommentDTO.getPostId(), 1);
        return CommentMapper.convertDynamoDBCommentToComment(savedComment);
    }

    /**
     * Finds all comments associated with a specific post ID.
     *
     * @param postId the ID of the post to retrieve comments for
     * @return a list of Comment objects associated with the specified post ID
     */
    @Override
    public List<Comment> findAllCommentsByPostId(String postId) {
        return mapDynamoDBCommentsToComments(dynamoDBCommentRepository.findByPostId(postId));
    }

    /**
     * Finds all comments associated with a specific author ID.
     *
     * @param authorID the ID of the author to retrieve comments for
     * @return a list of Comment objects associated with the specified author ID
     */
    @Override
    public List<Comment> findAllCommentsByAuthorId(String authorID) {
        return mapDynamoDBCommentsToComments(dynamoDBCommentRepository.findByAuthorId(authorID));
    }

    /**
     * Updates the content of a comment with the specified commentId.
     *
     * @param commentId the ID of the comment to update
     * @param comment   the updated content for the comment
     * @return the updated Comment object
     */
    @Override
    public Comment updateComment(String commentId, UpdateCommentDTO comment) {
        DynamoDBComment existingComment = findDynamoDBCommentById(commentId);
        existingComment.setContent(comment.getContent());
        return CommentMapper.convertDynamoDBCommentToComment(dynamoDBCommentRepository.save(existingComment));
    }

    /**
     * Deletes a comment with the specified commentId.
     *
     * @param commentId the ID of the comment to delete
     */
    @Override
    public void deleteComment(String commentId) {
        DynamoDBComment dynamoDBComment = findDynamoDBCommentById(commentId);
        dynamoDBCommentRepository.deleteById(commentId);
        changePostsCommentNumber(dynamoDBComment.getPostId(), -1);
        dynamoDBLikeRepository.deleteAllByTargetId(commentId);
    }

    private List<Comment> mapDynamoDBCommentsToComments(List<DynamoDBComment> dynamoDBComments) {
        return dynamoDBComments.stream()
                .map(CommentMapper::convertDynamoDBCommentToComment)
                .toList();
    }

    private DynamoDBComment findDynamoDBCommentById(String id) {
        return dynamoDBCommentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment with id " + id + " not found"));
    }


    /**
     * Updates the comment number of a post in the Posts table in DynamoDB.
     *
     * @param postId      the ID of the post to update the comment number for
     * @param changeValue the value by which to change the comment number (positive or negative)
     */
    private void changePostsCommentNumber(String postId, int changeValue) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("id", new AttributeValue().withS(postId));

        Map<String, AttributeValueUpdate> attributeUpdates = new HashMap<>();

        attributeUpdates.put("commentNumber",
                new AttributeValueUpdate().withAction(AttributeAction.ADD)
                        .withValue(new AttributeValue().withN(String.valueOf(changeValue))));

        UpdateItemRequest updateItemRequest = new UpdateItemRequest()
                .withTableName("Posts")
                .withKey(key)
                .withAttributeUpdates(attributeUpdates);

        amazonDynamoDB.updateItem(updateItemRequest);
    }
}
