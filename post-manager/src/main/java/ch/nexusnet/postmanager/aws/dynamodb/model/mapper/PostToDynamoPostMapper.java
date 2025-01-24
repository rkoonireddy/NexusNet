package ch.nexusnet.postmanager.aws.dynamodb.model.mapper;

import ch.nexusnet.postmanager.aws.dynamodb.model.table.DynamoDBPost;
import ch.nexusnet.postmanager.model.dto.CreatePostDTO;

public class PostToDynamoPostMapper {

    private PostToDynamoPostMapper() {
    }

    public static DynamoDBPost createPostMap(CreatePostDTO post) {
        DynamoDBPost dynamoDBPost = new DynamoDBPost();

        dynamoDBPost.setAuthorId(post.getAuthorId());
        dynamoDBPost.setType(post.getType().name());
        dynamoDBPost.setStatus(post.getStatus().name());
        dynamoDBPost.setTitle(post.getTitle());
        dynamoDBPost.setImage(post.getImage());
        dynamoDBPost.setShortDescription(post.getShortDescription());
        dynamoDBPost.setDescription(post.getDescription());
        dynamoDBPost.setHashtags(post.getHashtags());

        return dynamoDBPost;
    }
}

