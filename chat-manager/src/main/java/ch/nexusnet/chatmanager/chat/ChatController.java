package ch.nexusnet.chatmanager.chat;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/chats")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/{participant1}/{participant2}")
    @Operation(summary = "Get chat between two participants")
    @ApiResponse(responseCode = "200", description = "Chat found", content = @Content(schema = @Schema(implementation = Chat.class)))
    public ResponseEntity<Chat> getChat(@PathVariable("participant1") @NotNull String participant1, @PathVariable("participant2") @NotNull String participant2) {
        return ResponseEntity.ok(chatService.getChat(participant1, participant2));
    }

    @GetMapping("/{participant1}")
    @Operation(summary = "Get chats for participant")
    @ApiResponse(responseCode = "200", description = "Chats found", content = @Content(schema = @Schema(implementation = Chat.class)))
    public ResponseEntity<List<Chat>> getChatsForParticipant(@PathVariable("participant1") @NotNull String participant1) {
        return ResponseEntity.ok(chatService.getChatsForParticipant(participant1));
    }

}
