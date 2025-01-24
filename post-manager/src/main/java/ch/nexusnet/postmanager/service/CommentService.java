package ch.nexusnet.postmanager.service;

import ch.nexusnet.postmanager.model.Comment;
import ch.nexusnet.postmanager.model.dto.CreateCommentDTO;
import ch.nexusnet.postmanager.model.dto.UpdateCommentDTO;

import java.util.List;

public interface CommentService {

    Comment createComment(CreateCommentDTO comment);

    List<Comment> findAllCommentsByPostId(String postId);

    List<Comment> findAllCommentsByAuthorId(String authorID);

    Comment updateComment(String commentId, UpdateCommentDTO comment);

    void deleteComment(String commentId);
}
