package ch.nexusnet.postmanager.controller;

import ch.nexusnet.postmanager.model.Post;
import ch.nexusnet.postmanager.model.dto.CreatePostDTO;
import ch.nexusnet.postmanager.model.dto.UpdatePostDTO;
import ch.nexusnet.postmanager.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    @Operation(summary = "Create a new post")
    @ApiResponse(responseCode = "201", description = "Post created", content = @Content(schema = @Schema(implementation = Post.class)))
    public ResponseEntity<Post> createPost(@RequestBody @Valid CreatePostDTO createPostDTO) {
        Post post = postService.createPost(createPostDTO);
        URI location = URI.create("/posts/" + post.getId());
        return ResponseEntity.created(location).body(post);
    }

    @GetMapping
    @Operation(summary = "Retrieve all posts")
    @ApiResponse(responseCode = "200", description = "List of all posts", content = @Content(schema = @Schema(implementation = List.class)))
    public ResponseEntity<List<Post>> getAllPosts() {
        return ResponseEntity.ok(postService.findAllPosts());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retrieve a post by its ID")
    @ApiResponse(responseCode = "200", description = "Post retrieved", content = @Content(schema = @Schema(implementation = Post.class)))
    public ResponseEntity<Post> getPostById(@PathVariable String id) {
        return ResponseEntity.ok(postService.findById(id));
    }

    @GetMapping("/user/{authorId}")
    @Operation(summary = "Retrieve posts by author ID")
    @ApiResponse(responseCode = "200", description = "List of posts by author", content = @Content(schema = @Schema(implementation = List.class)))
    public ResponseEntity<List<Post>> getPostsByAuthorId(@PathVariable String authorId) {
        List<Post> posts = postService.findByAuthorId(authorId);
        return ResponseEntity.ok(posts);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a post")
    @ApiResponse(responseCode = "200", description = "Post updated", content = @Content(schema = @Schema(implementation = Post.class)))
    public ResponseEntity<Post> updatePost(@PathVariable String id, @RequestBody UpdatePostDTO post) {
        return ResponseEntity.ok(postService.updatePost(id, post));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a post")
    @ApiResponse(responseCode = "200", description = "Post deleted")
    public ResponseEntity<Void> deletePost(@PathVariable String id) {
        postService.deletePost(id);
        return ResponseEntity.ok().build();
    }

}
