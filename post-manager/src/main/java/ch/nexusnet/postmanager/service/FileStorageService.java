package ch.nexusnet.postmanager.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStorageService {
    String uploadFileToPost(MultipartFile file, String postId) throws IOException;

    void deleteFile(String fileKey);
}
