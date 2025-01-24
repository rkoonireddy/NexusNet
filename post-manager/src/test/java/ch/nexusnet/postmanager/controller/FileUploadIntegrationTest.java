package ch.nexusnet.postmanager.controller;

import ch.nexusnet.postmanager.aws.dynamodb.model.table.DynamoDBPost;
import ch.nexusnet.postmanager.aws.dynamodb.repositories.DynamoDBPostRepository;
import ch.nexusnet.postmanager.aws.s3.config.S3ClientConfiguration;
import ch.nexusnet.postmanager.model.PostStatus;
import ch.nexusnet.postmanager.model.PostType;
import ch.nexusnet.postmanager.util.IdGenerator;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.shaded.org.hamcrest.MatcherAssert.assertThat;
import static org.testcontainers.shaded.org.hamcrest.Matchers.containsString;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FileUploadIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DynamoDBPostRepository dynamoDBPostRepository;

    @Autowired
    private S3ClientConfiguration s3ClientConfig;

    @Value("${postmanager.aws.s3.bucket}")
    private String bucketName;

    private String postId;

    @BeforeEach
    void setUp() {
        DynamoDBPost dynamoDBPost = new DynamoDBPost();
        dynamoDBPost.setId(IdGenerator.generatePostId());
        dynamoDBPost.setAuthorId("123");
        dynamoDBPost.setLikeNumber(1);
        dynamoDBPost.setType(PostType.PROJECT.name());
        dynamoDBPost.setStatus(PostStatus.NEW.name());
        dynamoDBPost = dynamoDBPostRepository.save(dynamoDBPost);
        postId = dynamoDBPost.getId();
    }

    @AfterEach
    void tearDown() {
        dynamoDBPostRepository.deleteById(postId);
    }

    @Test
    void testFileUpload() throws Exception {
        byte[] content = Files.readAllBytes(Paths.get("src/test/java/ch/nexusnet/postmanager/files/478px-American_Beaver-1649739069.jpg"));
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", content);

        String fileKey = mockMvc.perform(multipart("/posts/{postId}/uploadFile", postId)
                        .file(file)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isCreated()).andReturn().getResponse().getHeader("Location");

        assertThat(fileKey, containsString(postId));

        s3ClientConfig.getS3client().deleteObject(new DeleteObjectRequest(bucketName, fileKey));

        DynamoDBPost dynamoDBPost = dynamoDBPostRepository.findById(postId).orElseThrow();
        assertEquals(1, dynamoDBPost.getFileUrls().size());
    }

    @Test
    void testDeleteFile() throws Exception {
        byte[] content = Files.readAllBytes(Paths.get("src/test/java/ch/nexusnet/postmanager/files/478px-American_Beaver-1649739069.jpg"));
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", content);
        String fileKey = "post-files/" + postId + "/test.jpg";

        s3ClientConfig.getS3client().putObject(bucketName, fileKey, file.getInputStream(), new ObjectMetadata());

        mockMvc.perform(delete("/posts/deleteFile/{fileKey}", fileKey))
                .andExpect(status().isOk());

        DynamoDBPost dynamoDBPost = dynamoDBPostRepository.findById(postId).orElseThrow();
        assertEquals(0, dynamoDBPost.getFileUrls().size());
    }

    @Test
    void testUploadAndDeleteFile() throws Exception {
        byte[] content = Files.readAllBytes(Paths.get("src/test/java/ch/nexusnet/postmanager/files/478px-American_Beaver-1649739069.jpg"));
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", content);

        String fileKey = mockMvc.perform(multipart("/posts/{postId}/uploadFile", postId)
                        .file(file)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isCreated()).andReturn().getResponse().getHeader("Location");

        DynamoDBPost dynamoDBPost = dynamoDBPostRepository.findById(postId).orElseThrow();
        assertEquals(1, dynamoDBPost.getFileUrls().size());

        mockMvc.perform(delete("/posts/deleteFile/{fileKey}", fileKey))
                .andExpect(status().isOk());

        dynamoDBPost = dynamoDBPostRepository.findById(postId).orElseThrow();
        assertEquals(0, dynamoDBPost.getFileUrls().size());
    }

}
