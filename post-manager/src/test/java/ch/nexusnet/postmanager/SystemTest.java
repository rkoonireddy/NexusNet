package ch.nexusnet.postmanager;

import ch.nexusnet.postmanager.aws.dynamodb.repositories.DynamoDBCommentRepository;
import ch.nexusnet.postmanager.aws.dynamodb.repositories.DynamoDBLikeRepository;
import ch.nexusnet.postmanager.aws.dynamodb.repositories.DynamoDBPostRepository;
import ch.nexusnet.postmanager.model.Comment;
import ch.nexusnet.postmanager.model.PostStatus;
import ch.nexusnet.postmanager.model.PostType;
import ch.nexusnet.postmanager.model.dto.CreatePostDTO;
import ch.nexusnet.postmanager.model.dto.UpdatePostDTO;
import ch.nexusnet.postmanager.utils.TestDataUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SystemTest {

    @Autowired
    DynamoDBPostRepository dynamoDBPostRepository;
    @Autowired
    DynamoDBCommentRepository dynamoDBCommentRepository;
    @Autowired
    DynamoDBLikeRepository dynamoDBLikeRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private String toJson(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    private String createUrl(String type, String id, String userId) {
        return String.format("/likes/%s/%s?userId=%s", type, id, userId);
    }

    @AfterEach
    public void tearDown() {
        dynamoDBPostRepository.deleteAll();
        dynamoDBCommentRepository.deleteAll();
        dynamoDBLikeRepository.deleteAll();
    }

    /**
     * This is a system test that simulates normal usage for the users. The user first creates a post
     * Then all posts are viewed. Then users interact with the post by liking and commenting. Then the post is updated and finally deleted.
     */
    @Test
    void test() throws Exception {
        CreatePostDTO createPostDTO = TestDataUtils.createSampleCreatePostDTO();

        String responseBody = mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(createPostDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.authorId").value(TestDataUtils.DEFAULT_AUTHOR_ID))
                .andExpect(jsonPath("$.type").value(PostType.PROJECT.name()))
                .andExpect(jsonPath("$.status").value(PostStatus.NEW.name()))
                .andExpect(jsonPath("$.image").value(TestDataUtils.DEFAULT_IMAGE))
                .andExpect(jsonPath("$.shortDescription").value(TestDataUtils.DEFAULT_SHORT_DESCRIPTION))
                .andExpect(jsonPath("$.description").value(TestDataUtils.DEFAULT_DESCRIPTION))
                .andExpect(jsonPath("$.title").value(TestDataUtils.DEFAULT_TITLE))
                .andExpect(jsonPath("$.edited").value(false))
                .andExpect(jsonPath("$.editedDateTime").isEmpty())
                .andExpect(jsonPath("$.createdDateTime").exists())
                .andExpect(jsonPath("$.likeNumber").value(0))
                .andExpect(jsonPath("$.commentNumber").value(0))
                .andExpect(jsonPath("$.hashtags[0]").value(TestDataUtils.DEFAULT_HASHTAGS.get(0)))
                .andExpect(jsonPath("$.hashtags[1]").value(TestDataUtils.DEFAULT_HASHTAGS.get(1)))
                .andReturn().getResponse().getContentAsString();

        String createdPostId = JsonPath.parse(responseBody).read("$.id", String.class);

        mockMvc.perform(get("/posts/" + createdPostId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdPostId))
                .andExpect(jsonPath("$.authorId").value(TestDataUtils.DEFAULT_AUTHOR_ID))
                .andExpect(jsonPath("$.type").value(PostType.PROJECT.name()))
                .andExpect(jsonPath("$.status").value(PostStatus.NEW.name()))
                .andExpect(jsonPath("$.image").value(TestDataUtils.DEFAULT_IMAGE))
                .andExpect(jsonPath("$.shortDescription").value(TestDataUtils.DEFAULT_SHORT_DESCRIPTION))
                .andExpect(jsonPath("$.description").value(TestDataUtils.DEFAULT_DESCRIPTION))
                .andExpect(jsonPath("$.title").value(TestDataUtils.DEFAULT_TITLE))
                .andExpect(jsonPath("$.edited").value(false))
                .andExpect(jsonPath("$.editedDateTime").isEmpty())
                .andExpect(jsonPath("$.createdDateTime").exists())
                .andExpect(jsonPath("$.likeNumber").value(0))
                .andExpect(jsonPath("$.hashtags[0]").value(TestDataUtils.DEFAULT_HASHTAGS.get(0)))
                .andExpect(jsonPath("$.hashtags[1]").value(TestDataUtils.DEFAULT_HASHTAGS.get(1)));

        mockMvc.perform(get("/posts")).andExpect(status().isOk()).andExpect(jsonPath("$").isArray());
        mockMvc.perform(get("/posts/user/" + TestDataUtils.DEFAULT_AUTHOR_ID)).andExpect(status().isOk()).andExpect(jsonPath("$").isArray());

        Comment comment = TestDataUtils.createSampleComment(createdPostId);
        responseBody = mockMvc.perform(post("/comments/posts/" + createdPostId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(comment)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.postId").value(createdPostId))
                .andExpect(jsonPath("$.authorId").value(comment.getAuthorId()))
                .andExpect(jsonPath("$.content").value(comment.getContent()))
                .andReturn().getResponse().getContentAsString();
        String createdCommentId = JsonPath.parse(responseBody).read("$.id", String.class);

        mockMvc.perform(get("/posts/" + createdPostId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentNumber").value(1));

        mockMvc.perform(get("/comments/author/" + TestDataUtils.DEFAULT_AUTHOR_ID)).andExpect(status().isOk()).andExpect(jsonPath("$").isArray());
        mockMvc.perform(get("/comments/posts/" + createdPostId)).andExpect(status().isOk()).andExpect(jsonPath("$").isArray());

        mockMvc.perform(post(createUrl("post", createdPostId, TestDataUtils.DEFAULT_AUTHOR_ID))).andExpect(status().isOk());
        mockMvc.perform(post(createUrl("comment", createdCommentId, TestDataUtils.DEFAULT_AUTHOR_ID))).andExpect(status().isOk());

        mockMvc.perform(get("/likes/user/" + TestDataUtils.DEFAULT_AUTHOR_ID + "/posts")).andExpect(status().isOk()).
                andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());

        mockMvc.perform(get("/likes/post/" + createdPostId + "/hasLiked?userId=" + TestDataUtils.DEFAULT_AUTHOR_ID)).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("true"));

        mockMvc.perform(get("/likes/comment/" + createdCommentId + "/hasLiked?userId=" + TestDataUtils.DEFAULT_AUTHOR_ID)).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("true"));

        mockMvc.perform(delete(createUrl("post", createdPostId, TestDataUtils.DEFAULT_AUTHOR_ID))).andExpect(status().isOk());
        mockMvc.perform(delete(createUrl("comment", createdCommentId, TestDataUtils.DEFAULT_AUTHOR_ID))).andExpect(status().isOk());

        mockMvc.perform(get("/likes/post/" + createdPostId + "/hasLiked?userId=" + TestDataUtils.DEFAULT_AUTHOR_ID)).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("false"));

        mockMvc.perform(get("/likes/comment/" + createdCommentId + "/hasLiked?userId=" + TestDataUtils.DEFAULT_AUTHOR_ID)).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("false"));

        UpdatePostDTO updatePostDTO = TestDataUtils.createSampleUpdatePostDTO();
        mockMvc.perform(put("/posts/" + createdPostId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(updatePostDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdPostId))
                .andExpect(jsonPath("$.authorId").value(TestDataUtils.DEFAULT_AUTHOR_ID))
                .andExpect(jsonPath("$.type").value(PostType.POST.name()))
                .andExpect(jsonPath("$.status").value(PostStatus.IN_PROGRESS.name()))
                .andExpect(jsonPath("$.image").value(TestDataUtils.UPDATED_IMAGE))
                .andExpect(jsonPath("$.shortDescription").value(TestDataUtils.UPDATED_SHORT_DESCRIPTION))
                .andExpect(jsonPath("$.description").value(TestDataUtils.UPDATED_DESCRIPTION))
                .andExpect(jsonPath("$.title").value(TestDataUtils.UPDATED_TITLE))
                .andExpect(jsonPath("$.edited").value(true))
                .andExpect(jsonPath("$.editedDateTime").exists())
                .andExpect(jsonPath("$.createdDateTime").exists())
                .andExpect(jsonPath("$.likeNumber").value(0))
                .andExpect(jsonPath("$.commentNumber").value(1))
                .andExpect(jsonPath("$.hashtags[0]").value(TestDataUtils.UPDATED_HASHTAGS.get(0)))
                .andExpect(jsonPath("$.hashtags[1]").value(TestDataUtils.UPDATED_HASHTAGS.get(1)));

        mockMvc.perform(delete("/posts/" + createdPostId)).andExpect(status().isOk());

        mockMvc.perform(get("/posts/" + createdPostId)).andExpect(status().isNotFound());

        // check that users likes and comments also got deleted
        mockMvc.perform(get("/likes/user/" + TestDataUtils.DEFAULT_AUTHOR_ID + "/posts")).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("[]"));
        mockMvc.perform(get("/comments/posts/" + createdPostId)).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("[]"));
    }
}