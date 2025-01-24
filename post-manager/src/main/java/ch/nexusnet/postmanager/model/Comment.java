package ch.nexusnet.postmanager.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Comment {

    private String id;

    private String postId;

    private String authorId;

    private String content;

    private int likeNumber;

    private String createdAt;

}
