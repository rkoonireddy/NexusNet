package ch.nexusnet.postmanager.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCommentDTO {
    @NotBlank(message = "Conent cannot be blank")
    private String content;
    @NotNull(message = "Post id cannot be null")
    private String postId;
    @NotNull(message = "Author id cannot be null")
    private String authorId;

}
