package ch.nexusnet.postmanager.controller;

import ch.nexusnet.postmanager.aws.dynamodb.model.mapper.DynamoPostToPostMapper;
import ch.nexusnet.postmanager.aws.dynamodb.model.table.DynamoDBPost;
import ch.nexusnet.postmanager.aws.dynamodb.repositories.DynamoDBPostRepository;
import ch.nexusnet.postmanager.model.Post;
import ch.nexusnet.postmanager.model.PostStatus;
import ch.nexusnet.postmanager.model.PostType;
import ch.nexusnet.postmanager.model.dto.CreatePostDTO;
import ch.nexusnet.postmanager.model.dto.UpdatePostDTO;
import ch.nexusnet.postmanager.util.IdGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PostControllerIntegrationTests {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE_TIME;
    private static final String AUTHOR_ID = UUID.randomUUID().toString();
    private static final String TITLE = "Initial Post Title";
    private static final String SHORT_DESCRIPTION = "Initial Short Description";
    private static final String DESCRIPTION = "Initial Post Description. Lorem ipsum dolor sit amet.";
    private static final String IMAGE = "Image";
    private static final String CREATION_DATE = FORMATTER.format(LocalDateTime.now());

    private static final List<String> HASHTAGS = Arrays.asList("Project", "UZH");

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private DynamoDBPostRepository dynamoDBPostRepository;
    private Post savedPost;

    @BeforeEach
    public void setup() {
        DynamoDBPost dynamoDBPost = new DynamoDBPost();
        dynamoDBPost.setId(IdGenerator.generatePostId());
        dynamoDBPost.setAuthorId(AUTHOR_ID);
        dynamoDBPost.setType(PostType.PROJECT.name());
        dynamoDBPost.setStatus(PostStatus.NEW.name());
        dynamoDBPost.setTitle(TITLE);
        dynamoDBPost.setShortDescription(SHORT_DESCRIPTION);
        dynamoDBPost.setDescription(DESCRIPTION);
        dynamoDBPost.setCreatedDateTime(CREATION_DATE);
        dynamoDBPost.setImage(IMAGE);
        dynamoDBPost.setHashtags(HASHTAGS);

        savedPost = DynamoPostToPostMapper.map(dynamoDBPostRepository.save(dynamoDBPost));
    }

    @AfterEach
    public void tearDown() {
        if (dynamoDBPostRepository.findById(savedPost.getId()).isPresent()) {
            dynamoDBPostRepository.deleteById(savedPost.getId());
        }
    }

    private String toJson(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    @Test
    void testCreatePost() throws Exception {
        CreatePostDTO createPostDTO = new CreatePostDTO();
        createPostDTO.setAuthorId(AUTHOR_ID);
        createPostDTO.setType(PostType.PROJECT);
        createPostDTO.setStatus(PostStatus.NEW);
        createPostDTO.setImage(IMAGE);
        createPostDTO.setShortDescription(SHORT_DESCRIPTION);
        createPostDTO.setDescription(DESCRIPTION);
        createPostDTO.setTitle(TITLE);
        createPostDTO.setHashtags(HASHTAGS);

        String responseBody = mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(createPostDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.authorId").value(AUTHOR_ID))
                .andExpect(jsonPath("$.type").value(PostType.PROJECT.name()))
                .andExpect(jsonPath("$.status").value(PostStatus.NEW.name()))
                .andExpect(jsonPath("$.image").value(IMAGE))
                .andExpect(jsonPath("$.shortDescription").value(SHORT_DESCRIPTION))
                .andExpect(jsonPath("$.description").value(DESCRIPTION))
                .andExpect(jsonPath("$.title").value(TITLE))
                .andExpect(jsonPath("$.edited").value(false))
                .andExpect(jsonPath("$.editedDateTime").isEmpty())
                .andExpect(jsonPath("$.createdDateTime").exists())
                .andExpect(jsonPath("$.likeNumber").value(0))
                .andExpect(jsonPath("$.hashtags[0]").value(HASHTAGS.get(0)))
                .andExpect(jsonPath("$.hashtags[1]").value(HASHTAGS.get(1)))
                .andReturn().getResponse().getContentAsString();

        String createdPostId = JsonPath.parse(responseBody).read("$.id", String.class);
        dynamoDBPostRepository.deleteById(createdPostId);
    }

    @Test
    void testCreatePostWithMissingFields() throws Exception {
        CreatePostDTO createPostDTO = new CreatePostDTO();

        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(createPostDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.message").value("Validation failed for one or more fields."));
    }


    @Test
    void testGetAllPosts() throws Exception {
        mockMvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testGetPostById() throws Exception {
        String postId = savedPost.getId();
        mockMvc.perform(get("/posts/{id}", postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(postId));
    }

    @Test
    void testGetPostByNonExistentId() throws Exception {
        String nonExistentPostId = "nonexistent_id";
        mockMvc.perform(get("/posts/{id}", nonExistentPostId)).andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Post not found with id: " + nonExistentPostId))
                .andExpect(jsonPath("$.status").value(404));
    }


    @Test
    void testGetPostsByAuthorId() throws Exception {
        String authorId = savedPost.getAuthorId();
        mockMvc.perform(get("/posts/user/{authorId}", authorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testUpdatePost() throws Exception {
        String postId = savedPost.getId();
        UpdatePostDTO updatePostDTO = new UpdatePostDTO();
        updatePostDTO.setDescription("New Description");
        updatePostDTO.setShortDescription("New Short Description");
        updatePostDTO.setTitle("New Title");
        updatePostDTO.setImage("New Image");
        updatePostDTO.setType(PostType.POST);

        mockMvc.perform(put("/posts/{id}", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(updatePostDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(postId))
                .andExpect(jsonPath("$.description").value("New Description"))
                .andExpect(jsonPath("$.shortDescription").value("New Short Description"))
                .andExpect(jsonPath("$.title").value("New Title"))
                .andExpect(jsonPath("$.image").value("New Image"))
                .andExpect(jsonPath("$.type").value(PostType.POST.name()))
                .andExpect(jsonPath("$.status").value(PostStatus.NEW.name()))
                .andExpect(jsonPath("$.edited").value(true))
                .andExpect(jsonPath("$.editedDateTime").exists())
        ;
    }

    @Test
    void testUpdateNonExistentPost() throws Exception {
        UpdatePostDTO updatePostDTO = new UpdatePostDTO();
        updatePostDTO.setTitle("New Title");

        String nonExistentPostId = "nonexistent_id";
        mockMvc.perform(put("/posts/{id}", nonExistentPostId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(updatePostDTO)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Post not found with id: " + nonExistentPostId))
                .andExpect(jsonPath("$.status").value(404));
    }


    @Test
    void testDeletePost() throws Exception {
        String postId = savedPost.getId();
        mockMvc.perform(delete("/posts/{id}", postId))
                .andExpect(status().isOk());
    }

}