package ch.nexusnet.postmanager.service;

import ch.nexusnet.postmanager.model.Post;

import java.util.List;

public interface LikeService {
    void likePost(String postId, String userId);

    void unlikePost(String postId, String userId);

    void likeComment(String commentId, String userId);

    void unlikeComment(String commentId, String userId);

    List<Post> getLikedPostsByUserSortedByRecency(String userId);

    boolean checkUserLikeStatusForPost(String postId, String userId);

    boolean checkUserLikeStatusForComment(String commentId, String userId);
}
