package ch.nexusnet.postmanager.utils;

import ch.nexusnet.postmanager.aws.dynamodb.model.table.DynamoDBPost;
import ch.nexusnet.postmanager.model.Comment;
import ch.nexusnet.postmanager.model.Post;
import ch.nexusnet.postmanager.model.PostStatus;
import ch.nexusnet.postmanager.model.PostType;
import ch.nexusnet.postmanager.model.dto.CreatePostDTO;
import ch.nexusnet.postmanager.model.dto.UpdatePostDTO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class TestDataUtils {
    public static final String DEFAULT_AUTHOR_ID = UUID.randomUUID().toString();
    public static final PostType DEFAULT_POST_TYPE = PostType.PROJECT;
    public static final PostStatus DEFAULT_POST_STATUS = PostStatus.NEW;
    public static final String DEFAULT_TITLE = "Sample Title";
    public static final String DEFAULT_IMAGE = "https://example.com/image.jpg";
    public static final String DEFAULT_SHORT_DESCRIPTION = "This is a short description.";
    public static final String DEFAULT_DESCRIPTION = "This is a long detailed description.";
    public static final int DEFAULT_LIKE_NUMBER = 0;
    public static final List<String> DEFAULT_HASHTAGS = Arrays.asList("#example", "#test");
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE_TIME;
    public static final String DEFAULT_CREATED_DATE_TIME = FORMATTER.format(LocalDateTime.now());
    public static final boolean DEFAULT_EDITED = false;
    public static final String DEFAULT_EDITED_DATE_TIME = null;

    public static final PostType UPDATED_POST_TYPE = PostType.POST;
    public static final PostStatus UPDATED_POST_STATUS = PostStatus.IN_PROGRESS;
    public static final String UPDATED_TITLE = "Updated Title";
    public static final String UPDATED_IMAGE = "https://example.com/updated_image.jpg";
    public static final String UPDATED_SHORT_DESCRIPTION = "This is an updated short description.";
    public static final String UPDATED_DESCRIPTION = "This is an updated long detailed description.";
    public static final List<String> UPDATED_HASHTAGS = Arrays.asList("#updatedExample", "#updatedTest");

    public static final boolean UPDATED_EDITED = true;

    public static final String UPDATED_EDITED_DATE_TIME = FORMATTER.format(LocalDateTime.now());

    public static String generateUniqueId() {
        return UUID.randomUUID().toString();
    }

    public static CreatePostDTO createSampleCreatePostDTO() {
        CreatePostDTO dto = new CreatePostDTO();
        dto.setAuthorId(DEFAULT_AUTHOR_ID);
        dto.setType(DEFAULT_POST_TYPE);
        dto.setStatus(DEFAULT_POST_STATUS);
        dto.setTitle(DEFAULT_TITLE);
        dto.setImage(DEFAULT_IMAGE);
        dto.setShortDescription(DEFAULT_SHORT_DESCRIPTION);
        dto.setDescription(DEFAULT_DESCRIPTION);
        dto.setHashtags(DEFAULT_HASHTAGS);
        return dto;
    }

    public static DynamoDBPost createSampleDynamoDBPost() {
        DynamoDBPost post = new DynamoDBPost();
        post.setId(generateUniqueId());
        post.setAuthorId(DEFAULT_AUTHOR_ID);
        post.setType(DEFAULT_POST_TYPE.name());
        post.setStatus(DEFAULT_POST_STATUS.name());
        post.setTitle(DEFAULT_TITLE);
        post.setImage(DEFAULT_IMAGE);
        post.setShortDescription(DEFAULT_SHORT_DESCRIPTION);
        post.setDescription(DEFAULT_DESCRIPTION);
        post.setLikeNumber(DEFAULT_LIKE_NUMBER);
        post.setHashtags(DEFAULT_HASHTAGS);
        post.setCreatedDateTime(DEFAULT_CREATED_DATE_TIME);
        post.setEdited(DEFAULT_EDITED);
        post.setEditedDateTime(DEFAULT_EDITED_DATE_TIME);
        return post;
    }

    public static UpdatePostDTO createSampleUpdatePostDTO() {
        UpdatePostDTO dto = new UpdatePostDTO();
        dto.setType(UPDATED_POST_TYPE);
        dto.setStatus(UPDATED_POST_STATUS);
        dto.setTitle(UPDATED_TITLE);
        dto.setImage(UPDATED_IMAGE);
        dto.setShortDescription(UPDATED_SHORT_DESCRIPTION);
        dto.setDescription(UPDATED_DESCRIPTION);
        dto.setHashtags(UPDATED_HASHTAGS);
        return dto;
    }

    public static Post createSamplePost() {
        Post post = new Post();
        post.setId(generateUniqueId());
        post.setAuthorId(DEFAULT_AUTHOR_ID);
        post.setType(DEFAULT_POST_TYPE);
        post.setStatus(DEFAULT_POST_STATUS);
        post.setTitle(DEFAULT_TITLE);
        post.setImage(DEFAULT_IMAGE);
        post.setShortDescription(DEFAULT_SHORT_DESCRIPTION);
        post.setDescription(DEFAULT_DESCRIPTION);
        post.setLikeNumber(DEFAULT_LIKE_NUMBER);
        post.setHashtags(DEFAULT_HASHTAGS);
        post.setCreatedDateTime(DEFAULT_CREATED_DATE_TIME);
        post.setEdited(DEFAULT_EDITED);
        post.setEditedDateTime(null);
        return post;
    }

    public static Post createSampleUpdatedPost() {
        Post post = new Post();
        post.setId(generateUniqueId());
        post.setAuthorId(DEFAULT_AUTHOR_ID);
        post.setType(UPDATED_POST_TYPE);
        post.setStatus(UPDATED_POST_STATUS);
        post.setTitle(UPDATED_TITLE);
        post.setImage(UPDATED_IMAGE);
        post.setShortDescription(UPDATED_SHORT_DESCRIPTION);
        post.setDescription(UPDATED_DESCRIPTION);
        post.setLikeNumber(DEFAULT_LIKE_NUMBER);
        post.setHashtags(UPDATED_HASHTAGS);
        post.setCreatedDateTime(DEFAULT_CREATED_DATE_TIME);
        post.setEdited(UPDATED_EDITED);
        post.setEditedDateTime(UPDATED_EDITED_DATE_TIME);
        return post;
    }

    public static Comment createSampleComment(String postId) {
        Comment comment = new Comment();
        comment.setId(generateUniqueId());
        comment.setAuthorId(DEFAULT_AUTHOR_ID);
        comment.setPostId(postId);
        comment.setContent("This is a comment.");
        comment.setCreatedAt(DEFAULT_CREATED_DATE_TIME);
        return comment;
    }

}
