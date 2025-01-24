package ch.nexusnet.postmanager.service;

import ch.nexusnet.postmanager.aws.dynamodb.model.table.DynamoDBPost;
import ch.nexusnet.postmanager.aws.dynamodb.repositories.DynamoDBPostRepository;
import ch.nexusnet.postmanager.aws.s3.config.S3ClientConfiguration;
import ch.nexusnet.postmanager.exception.ResourceNotFoundException;
import ch.nexusnet.postmanager.util.FileValidationUtil;
import ch.nexusnet.postmanager.util.IdGenerator;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;

@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {

    private static final String POST_FILE_PATH = "post-files/";
    private final S3ClientConfiguration s3ClientConfiguration;
    private final DynamoDBPostRepository dynamoDBPostRepository;
    @Value("${postmanager.aws.s3.bucket}")
    private String bucketName;

    /**
     * Uploads a file to a post.
     *
     * @param file   the file to be uploaded
     * @param postId the id of the post to which the file will be uploaded
     * @return the URL of the uploaded file
     * @throws IOException if an I/O error occurs
     */
    @Override
    public String uploadFileToPost(MultipartFile file, String postId) throws IOException {
        DynamoDBPost post = findPostById(postId);
        FileValidationUtil.validateFileType(file);
        URL url = uploadFileToS3(postId, file);
        post.addFileUrl(url.toString());
        dynamoDBPostRepository.save(post);
        return url.toString();
    }

    /**
     * Deletes a file from the storage.
     *
     * @param fileKey the key of the file to be deleted
     */
    @Override
    public void deleteFile(String fileKey) {
        int postIndex = fileKey.indexOf("POST-");
        int slashIndex = fileKey.indexOf("/", postIndex);
        String postId = fileKey.substring(postIndex, slashIndex);

        DynamoDBPost post = findPostById(postId);
        post.removeFileUrl(fileKey);

        dynamoDBPostRepository.save(post);
        s3ClientConfiguration.getS3client().deleteObject(new DeleteObjectRequest(bucketName, fileKey));
    }

    private URL uploadFileToS3(String postId, MultipartFile multipartFile) throws IOException {
        AmazonS3 s3 = s3ClientConfiguration.getS3client();

        String keyName = POST_FILE_PATH + postId + "/" + IdGenerator.generateFileId();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(multipartFile.getContentType());
        metadata.setContentLength(multipartFile.getSize());

        // Upload the file
        s3.putObject(new PutObjectRequest(bucketName, keyName, multipartFile.getInputStream(), metadata));

        return s3.getUrl(bucketName, keyName);
    }

    private DynamoDBPost findPostById(String id) throws ResourceNotFoundException {
        return dynamoDBPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));
    }
}
