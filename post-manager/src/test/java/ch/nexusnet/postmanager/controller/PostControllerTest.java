package ch.nexusnet.postmanager.controller;

import ch.nexusnet.postmanager.model.Post;
import ch.nexusnet.postmanager.model.dto.CreatePostDTO;
import ch.nexusnet.postmanager.model.dto.UpdatePostDTO;
import ch.nexusnet.postmanager.service.PostService;
import ch.nexusnet.postmanager.utils.TestDataUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createPostTest() throws Exception {
        CreatePostDTO createPostDTO = TestDataUtils.createSampleCreatePostDTO();
        Post post = TestDataUtils.createSamplePost();

        given(postService.createPost(any(CreatePostDTO.class))).willReturn(post);

        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPostDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/posts/" + post.getId()));
    }

    @Test
    void getAllPostsTest() throws Exception {
        List<Post> allPosts = Arrays.asList(TestDataUtils.createSamplePost(), TestDataUtils.createSamplePost());
        given(postService.findAllPosts()).willReturn(allPosts);

        mockMvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getPostByIdTest() throws Exception {
        Post post = TestDataUtils.createSamplePost();
        given(postService.findById(post.getId())).willReturn(post);

        mockMvc.perform(get("/posts/" + post.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(post.getId())));
    }

    @Test
    void getPostsByAuthorIdTest() throws Exception {
        List<Post> posts = Arrays.asList(TestDataUtils.createSamplePost(), TestDataUtils.createSamplePost());
        given(postService.findByAuthorId(TestDataUtils.DEFAULT_AUTHOR_ID)).willReturn(posts);

        mockMvc.perform(get("/posts/user/" + TestDataUtils.DEFAULT_AUTHOR_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void updatePostTest() throws Exception {
        UpdatePostDTO updatePostDTO = TestDataUtils.createSampleUpdatePostDTO();
        Post updatedPost = TestDataUtils.createSampleUpdatedPost();
        given(postService.updatePost(updatedPost.getId(), updatePostDTO)).willReturn(updatedPost);

        mockMvc.perform(put("/posts/" + updatedPost.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePostDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void deletePostTest() throws Exception {
        Post post = TestDataUtils.createSamplePost();
        willDoNothing().given(postService).deletePost(post.getId());

        mockMvc.perform(delete("/posts/123" + post.getId()))
                .andExpect(status().isOk());
    }
}