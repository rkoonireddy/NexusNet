package ch.nexusnet.postmanager.controller;

import ch.nexusnet.postmanager.model.Post;
import ch.nexusnet.postmanager.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/likes")
public class LikeController {

    private final LikeService likeService;

    @Autowired
    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping("/post/{postId}")
    @Operation(summary = "Like a post")
    @ApiResponse(responseCode = "200", description = "Post liked successfully")
    public ResponseEntity<Void> likePost(@PathVariable String postId, @RequestParam String userId) {
        likeService.likePost(postId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/post/{postId}")
    @Operation(summary = "Unlike a post")
    @ApiResponse(responseCode = "200", description = "Post unliked successfully")
    public ResponseEntity<Void> unlikePost(@PathVariable String postId, @RequestParam String userId) {
        likeService.unlikePost(postId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/comment/{commentId}")
    @Operation(summary = "Like a comment")
    @ApiResponse(responseCode = "200", description = "Comment liked successfully")
    public ResponseEntity<Void> likeComment(@PathVariable String commentId, @RequestParam String userId) {
        likeService.likeComment(commentId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/comment/{commentId}")
    @Operation(summary = "Unlike a comment")
    @ApiResponse(responseCode = "200", description = "Comment unliked successfully")
    public ResponseEntity<Void> unlikeComment(@PathVariable String commentId, @RequestParam String userId) {
        likeService.unlikeComment(commentId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{userId}/posts")
    @Operation(summary = "Get liked posts by user")
    @ApiResponse(responseCode = "200", description = "List of liked posts", content = @Content)
    public ResponseEntity<List<Post>> getLikedPosts(@PathVariable String userId) {
        return ResponseEntity.ok(likeService.getLikedPostsByUserSortedByRecency(userId));
    }

    @GetMapping("/post/{postId}/hasLiked")
    @Operation(summary = "Check if user has liked a post")
    @ApiResponse(responseCode = "200", description = "Liked status returned", content = @Content)
    public ResponseEntity<Boolean> hasUserLikedPost(@PathVariable String postId, @RequestParam String userId) {
        boolean hasLiked = likeService.checkUserLikeStatusForPost(postId, userId);
        return ResponseEntity.ok(hasLiked);
    }

    @GetMapping("/comment/{commentId}/hasLiked")
    @Operation(summary = "Check if user has liked a comment")
    @ApiResponse(responseCode = "200", description = "Liked status returned", content = @Content)
    public ResponseEntity<Boolean> hasUserLikedComment(@PathVariable String commentId, @RequestParam String userId) {
        boolean hasLiked = likeService.checkUserLikeStatusForComment(commentId, userId);
        return ResponseEntity.ok(hasLiked);
    }
}
