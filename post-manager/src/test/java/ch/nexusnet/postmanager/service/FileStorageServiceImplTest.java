package ch.nexusnet.postmanager.service;

import ch.nexusnet.postmanager.aws.dynamodb.model.table.DynamoDBPost;
import ch.nexusnet.postmanager.aws.dynamodb.repositories.DynamoDBPostRepository;
import ch.nexusnet.postmanager.aws.s3.config.S3ClientConfiguration;
import ch.nexusnet.postmanager.exception.ResourceNotFoundException;
import ch.nexusnet.postmanager.exception.UnsupportedFileTypeException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileStorageServiceImplTest {
    @Mock
    private S3ClientConfiguration s3ClientConfiguration;
    @Mock
    private DynamoDBPostRepository dynamoDBPostRepository;

    @Mock
    private AmazonS3 s3Client;

    @InjectMocks
    private FileStorageServiceImpl fileStorageService;

    @Test
    void uploadFileToPostSuccessfully() throws IOException {
        byte[] content = Files.readAllBytes(Paths.get("src/test/java/ch/nexusnet/postmanager/files/478px-American_Beaver-1649739069.jpg"));
        MultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", content);

        DynamoDBPost post = new DynamoDBPost();
        post.setId("123");
        when(dynamoDBPostRepository.findById(anyString())).thenReturn(Optional.of(post));
        when(s3ClientConfiguration.getS3client()).thenReturn(s3Client);
        when(s3Client.putObject(any(PutObjectRequest.class))).thenReturn(null);
        when(s3Client.getUrl(any(), anyString())).thenReturn(new URL("http:/post-files/123/FILE-bfd8d402-50a3-4850-9e65-51cb04368340"));

        String url = fileStorageService.uploadFileToPost(file, "123");

        assertNotNull(url);
        verify(dynamoDBPostRepository, times(1)).save(any(DynamoDBPost.class));
    }

    @Test
    void uploadFileToPostThrowsResourceNotFoundException() throws IOException {
        byte[] content = Files.readAllBytes(Paths.get("src/test/java/ch/nexusnet/postmanager/files/478px-American_Beaver-1649739069.jpg"));
        MultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", content);

        when(dynamoDBPostRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> fileStorageService.uploadFileToPost(file, "123"));
    }

    @Test
    void uploadFileToPostThrowsUnsupportedFileTypeException() {
        MultipartFile file = new MockMultipartFile("file", "hello.txt", "text/plain", "Hello, World!".getBytes());
        DynamoDBPost post = new DynamoDBPost();
        post.setId("123");
        when(dynamoDBPostRepository.findById(anyString())).thenReturn(Optional.of(post));

        assertThrows(UnsupportedFileTypeException.class, () -> fileStorageService.uploadFileToPost(file, "123"));
    }
}