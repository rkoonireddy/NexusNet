package ch.nexusnet.usermanager.aws.s3.client;

import ch.nexusnet.usermanager.aws.s3.exceptions.UnsupportedFileTypeException;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class S3ClientTest {

    @InjectMocks
    private S3Client s3Client;

    @Mock
    private S3ClientConfiguration s3ClientConfigurationMock;
    
    private final String FILE_NAME = "test-file-name";

    private final String TEST_URL = "http://test.com";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @ParameterizedTest
    @ValueSource(strings = {"image/jpeg", "application/pdf"})
    void testUploadProfilePictureToS3_expectSuccess(String contentType) throws IOException {
        // arrange
        String userId = UUID.randomUUID().toString();
        MultipartFile multipartFile = new MockMultipartFile(FILE_NAME, FILE_NAME, contentType, new byte[0]);
        AmazonS3 s3Mock = mock(AmazonS3.class);
        setUploadStubs(s3Mock);

        // act
        URL url = s3Client.uploadFileToS3(userId, multipartFile);

        // assert
        assertNotNull(url);
    }

    @ParameterizedTest
    @ValueSource(strings = {"image/png", "application/json", "text/plain", "application/octet-stream}"})
    void testUploadProfilePictureToS3WithNotSupportedMimeTypes_expectException(String contentType) throws IOException {
        // arrange
        String userId = UUID.randomUUID().toString();
        MultipartFile multipartFile = new MockMultipartFile(FILE_NAME, FILE_NAME, contentType, new byte[0]);
        AmazonS3 s3Mock = mock(AmazonS3.class);
        setUploadStubs(s3Mock);

        // act
        // assert
        assertThrows(UnsupportedFileTypeException.class, () -> s3Client.uploadFileToS3(userId, multipartFile));
    }

    @ParameterizedTest
    @ValueSource(strings = {"profile-picture.jpeg", "resume.pdf"})
    void testGetProfilePictureFromS3_expectSuccess(String keyName) throws MalformedURLException {
        // arrange
        String userId = UUID.randomUUID().toString();
        String fileKey = "user-files/" + userId + keyName;
        AmazonS3 s3Mock = mock(AmazonS3.class);

        when(s3ClientConfigurationMock.getS3client()).thenReturn(s3Mock);
        when(s3Mock.doesObjectExist(any(), any())).thenReturn(true);
        when(s3Mock.generatePresignedUrl(any())).thenReturn(new URL(TEST_URL));

        GeneratePresignedUrlRequest expectedGeneratePresignedUrlRequest = new GeneratePresignedUrlRequest(
                "nexus-net-user-info-test-bucket", fileKey)
                .withMethod(HttpMethod.GET)
                .withExpiration(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)));
        ArgumentCaptor<GeneratePresignedUrlRequest> captor = ArgumentCaptor.forClass(GeneratePresignedUrlRequest.class);

        // act
        if (keyName.equals("profile-picture.jpeg")) {
            s3Client.getProfilePictureFromS3(userId);
        }
        else {
            s3Client.getResumeFromS3(userId);
        }

        // assert
        verify(s3Mock).generatePresignedUrl(captor.capture());
        assertEquals(expectedGeneratePresignedUrlRequest.getKey(), captor.getValue().getKey());
        assertEquals(expectedGeneratePresignedUrlRequest.getMethod(), captor.getValue().getMethod());
    }

    private void setUploadStubs(AmazonS3 s3Mock) throws MalformedURLException {
        when(s3ClientConfigurationMock.getS3client()).thenReturn(s3Mock);
        when(s3Mock.putObject(any(PutObjectRequest.class))).thenReturn(null);
        when(s3Mock.getUrl(any(), any())).thenReturn(new URL(TEST_URL));
    }
}