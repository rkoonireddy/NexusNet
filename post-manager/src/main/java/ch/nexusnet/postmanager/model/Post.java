package ch.nexusnet.postmanager.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    private String authorId;

    private PostType type;

    private PostStatus status;

    private String title;

    private String image;

    private String shortDescription;

    private String description;

    private int likeNumber;

    private int commentNumber;

    private List<String> hashtags;

    private String createdDateTime;

    private boolean edited = false;

    private String editedDateTime;

    private List<String> fileUrls;
}
