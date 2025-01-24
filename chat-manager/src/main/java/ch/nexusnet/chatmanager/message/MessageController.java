package ch.nexusnet.chatmanager.message;

import ch.nexusnet.chatmanager.chat.Chat;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    @Operation(summary = "Send a message to a chat")
    @ApiResponse(responseCode = "201", description = "Message Sent", content = @Content(schema = @Schema(implementation = Chat.class)))
    public ResponseEntity<Chat> sendMessage(@RequestBody @Valid Message message) {
        Chat chat = messageService.sendMessage(message);
        URI location = URI.create(String.format("/chats/get?participant1=%s&participant2=%s", message.getSender(), message.getReceiver()));
        return ResponseEntity.created(location).body(chat);
    }
}
