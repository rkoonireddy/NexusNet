package ch.nexusnet.postmanager.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCommentDTO {

    @NotBlank(message = "Content cannot be blank")
    private String content;

}