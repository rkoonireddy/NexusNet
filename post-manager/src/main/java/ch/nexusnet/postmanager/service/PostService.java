package ch.nexusnet.postmanager.service;

import ch.nexusnet.postmanager.model.Post;
import ch.nexusnet.postmanager.model.dto.CreatePostDTO;
import ch.nexusnet.postmanager.model.dto.UpdatePostDTO;

import java.util.List;

public interface PostService {
    Post createPost(CreatePostDTO post);

    List<Post> findAllPosts();

    Post findById(String id);

    List<Post> findByAuthorId(String authorId);

    Post updatePost(String id, UpdatePostDTO post);

    void deletePost(String id);
}
