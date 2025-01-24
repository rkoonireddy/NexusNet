package ch.nexusnet.chatmanager.message;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class Message {

    private String id;

    @NotBlank
    private String content;

    @NotBlank
    private String sender;

    @NotBlank
    private String receiver;

    private String timestamp;
}
