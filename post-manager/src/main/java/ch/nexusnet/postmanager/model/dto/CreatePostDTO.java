package ch.nexusnet.postmanager.model.dto;

import ch.nexusnet.postmanager.model.PostStatus;
import ch.nexusnet.postmanager.model.PostType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreatePostDTO {

    @NotNull(message = "Author ID cannot be null")
    private String authorId;

    @NotNull(message = "Post type cannot be null")
    private PostType type;

    @NotNull(message = "Post status cannot be null")
    private PostStatus status;

    @NotBlank(message = "Title cannot be blank")
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    private String title;

    private String image;

    @Size(max = 500, message = "Short description cannot exceed 500 characters")
    private String shortDescription;

    @NotBlank(message = "Description cannot be blank")
    private String description;

    private List<String> hashtags;
}
