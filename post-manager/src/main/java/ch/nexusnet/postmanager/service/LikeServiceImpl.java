package ch.nexusnet.postmanager.service;

import ch.nexusnet.postmanager.aws.dynamodb.model.mapper.DynamoPostToPostMapper;
import ch.nexusnet.postmanager.aws.dynamodb.model.table.DynamoDBLike;
import ch.nexusnet.postmanager.aws.dynamodb.model.table.DynamoDBPost;
import ch.nexusnet.postmanager.aws.dynamodb.repositories.DynamoDBLikeRepository;
import ch.nexusnet.postmanager.aws.dynamodb.repositories.DynamoDBPostRepository;
import ch.nexusnet.postmanager.model.LikeTargetType;
import ch.nexusnet.postmanager.model.Post;
import ch.nexusnet.postmanager.util.IdGenerator;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeAction;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class LikeServiceImpl implements LikeService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE_TIME;
    private final DynamoDBLikeRepository dynamoDBLikeRepository;

    private final DynamoDBPostRepository dynamoDBPostRepository;

    private final AmazonDynamoDB amazonDynamoDB;
    private final ZoneId appZoneId;

    public LikeServiceImpl(DynamoDBLikeRepository dynamoDBLikeRepository, DynamoDBPostRepository dynamoDBPostRepository, AmazonDynamoDB amazonDynamoDB, @Value("${app.timezone:CET}") ZoneId appZoneId) {
        this.dynamoDBLikeRepository = dynamoDBLikeRepository;
        this.dynamoDBPostRepository = dynamoDBPostRepository;
        this.amazonDynamoDB = amazonDynamoDB;
        this.appZoneId = appZoneId;
    }

    /**
     * Increases the like number of a post by 1 and creates and saves a like record for the user.
     *
     * @param postId the ID of the post
     * @param userId the ID of the user
     */
    @Override
    public void likePost(String postId, String userId) {
        changeLikeNumber(postId, "1");
        createAndSaveLike(postId, LikeTargetType.POST, userId);
    }

    /**
     * Decreases the like number of a post by 1 and deletes the like record of the user.
     *
     * @param postId the ID of the post
     * @param userId the ID of the user
     */
    @Override
    public void unlikePost(String postId, String userId) {
        changeLikeNumber(postId, "-1");
        dynamoDBLikeRepository.deleteByTargetIdAndTargetTypeAndUserId(postId, LikeTargetType.POST.name(), userId);
    }

    /**
     * Increases the like number of a comment by 1 and creates and saves a like record for the user.
     *
     * @param commentId the ID of the comment
     * @param userId    the ID of the user
     */
    @Override
    public void likeComment(String commentId, String userId) {
        changeLikeNumber(commentId, "1");
        createAndSaveLike(commentId, LikeTargetType.COMMENT, userId);
    }

    /**
     * Decreases the like number of a comment by 1 and deletes the like record of the user.
     *
     * @param commentId the ID of the comment
     * @param userId    the ID of the user
     */
    @Override
    public void unlikeComment(String commentId, String userId) {
        changeLikeNumber(commentId, "-1");
        dynamoDBLikeRepository.deleteByTargetIdAndTargetTypeAndUserId(commentId, LikeTargetType.COMMENT.name(), userId);
    }

    /**
     * Returns a list of liked posts for a given user, sorted by recency.
     *
     * @param userId the ID of the user
     * @return a list of Post objects representing liked posts, sorted by recency
     */
    @Override
    public List<Post> getLikedPostsByUserSortedByRecency(String userId) {
        List<DynamoDBLike> dynamoDBLikes = dynamoDBLikeRepository.findAllByTargetTypeAndUserId(LikeTargetType.POST.name(), userId);

        List<String> postIds = dynamoDBLikes.stream()
                .map(DynamoDBLike::getTargetId)
                .toList();

        List<DynamoDBPost> posts = new ArrayList<>();
        if (!postIds.isEmpty()) {
            posts = (List<DynamoDBPost>) dynamoDBPostRepository.findAllById(postIds);
        }

        Map<String, DynamoDBPost> postMap = posts.stream()
                .collect(Collectors.toMap(DynamoDBPost::getId, Function.identity()));

        return dynamoDBLikes.stream()
                .sorted(Comparator.comparing((DynamoDBLike like) -> LocalDateTime.parse(like.getTimestamp(), DateTimeFormatter.ISO_DATE_TIME)).reversed())
                .map(like -> postMap.get(like.getTargetId()))
                .filter(Objects::nonNull)
                .map(DynamoPostToPostMapper::map)
                .toList();
    }

    /**
     * Checks whether a user has liked a post or not.
     *
     * @param postId the ID of the post
     * @param userId the ID of the user
     * @return true if the user has liked the post, false otherwise
     */
    @Override
    public boolean checkUserLikeStatusForPost(String postId, String userId) {
        return dynamoDBLikeRepository.findByTargetIdAndTargetTypeAndUserId(postId, LikeTargetType.POST.name(), userId).isPresent();
    }

    /**
     * Checks whether a user has liked a comment or not.
     *
     * @param commentId the ID of the comment
     * @param userId    the ID of the user
     * @return true if the user has liked the comment, false otherwise
     */
    @Override
    public boolean checkUserLikeStatusForComment(String commentId, String userId) {
        return dynamoDBLikeRepository.findByTargetIdAndTargetTypeAndUserId(commentId, LikeTargetType.COMMENT.name(), userId).isPresent();
    }

    private void createAndSaveLike(String postId, LikeTargetType post, String userId) {
        DynamoDBLike dynamoDBLike = new DynamoDBLike();
        dynamoDBLike.setId(IdGenerator.generateLikeId());
        dynamoDBLike.setTargetId(postId);
        dynamoDBLike.setTargetType(post.name());
        dynamoDBLike.setUserId(userId);
        dynamoDBLike.setTimestamp(FORMATTER.format(LocalDateTime.now(appZoneId)));
        dynamoDBLikeRepository.save(dynamoDBLike);
    }

    /**
     * Increases or decreases the like number of a target (post or comment) by the specified value.
     * Uses a direct query on the DynamoDB to reduce read and write operations (such as when using repositories)
     *
     * @param targetId       the ID of the target (post or comment)
     * @param changeValue    the value by which to change the like number (positive for increase, negative for decrease)
     */
    private void changeLikeNumber(String targetId, String changeValue) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("id", new AttributeValue().withS(targetId));

        Map<String, AttributeValueUpdate> attributeUpdates = new HashMap<>();

        attributeUpdates.put("likeNumber",
                new AttributeValueUpdate().withAction(AttributeAction.ADD)
                        .withValue(new AttributeValue().withN(changeValue)));

        UpdateItemRequest updateItemRequest = new UpdateItemRequest()
                .withTableName("Posts")
                .withKey(key)
                .withAttributeUpdates(attributeUpdates);

        amazonDynamoDB.updateItem(updateItemRequest);
    }

}
