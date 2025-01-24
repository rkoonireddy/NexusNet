package ch.nexusnet.postmanager.controller;

import ch.nexusnet.postmanager.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;

import java.io.IOException;
import java.net.URI;

@RestController
@RequestMapping("/posts")
public class FileUploadController {

    private final FileStorageService fileStorageService;

    public FileUploadController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @Operation(summary = "Upload a file to a post")
    @ApiResponse(responseCode = "201", description = "File created", content = @Content(schema = @Schema(implementation = String.class)))
    @PostMapping("/{postId}/uploadFile")
    public ResponseEntity<URI> uploadFileToPost(@PathVariable String postId, @RequestParam("file") MultipartFile file) throws IOException {
        String fileUrl = fileStorageService.uploadFileToPost(file, postId);
        return ResponseEntity.created(URI.create(fileUrl)).build();
    }

    @Operation(summary = "Delete a file from a post")
    @ApiResponse(responseCode = "200", description = "Successfully deleted file")
    @DeleteMapping("/deleteFile/**")
    public ResponseEntity<Void> deleteFile(HttpServletRequest request) {
        // We manually extract the file key from the request path, because the file key can contain slashes
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String fileKey = new AntPathMatcher().extractPathWithinPattern(bestMatchPattern, path);

        fileStorageService.deleteFile(fileKey);
        return ResponseEntity.ok().build();
    }
}
