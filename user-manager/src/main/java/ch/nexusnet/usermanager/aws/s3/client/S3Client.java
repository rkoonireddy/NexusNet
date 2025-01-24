package ch.nexusnet.usermanager.aws.s3.client;

import ch.nexusnet.usermanager.aws.s3.exceptions.UnsupportedFileTypeException;
import ch.nexusnet.usermanager.aws.s3.filetypes.S3FileType;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class S3Client {
    private static final String USER_FILE_PATH = "user-files/";
    private static final String PROFILE_PICTURE = "profile-picture.jpeg";
    private static final String RESUME = "resume.pdf";

    private final S3ClientConfiguration s3ClientConfiguration;
    @Value("${usermanager.aws.s3.bucket}")
    private String bucketName;


    /**
     * Uploads a file to S3. Takes a multipart file and a user id as input.
     * jpeg's and pdf's are supported.
     * @param userId The user id of the user who is uploading the file.
     * @param multipartFile The file to be uploaded.
     * @return The URL of the uploaded file.
     * @throws IOException If an I/O error occurs.
     * @throws UnsupportedFileTypeException If the file type is not supported.
     */
    public URL uploadFileToS3(String userId , MultipartFile multipartFile) throws IOException, UnsupportedFileTypeException {
        AmazonS3 s3 = getS3Client();

        String keyName;
        if (isFileAProfilePicture(multipartFile)) {
            keyName = USER_FILE_PATH + userId + PROFILE_PICTURE;
        } else {
            keyName = USER_FILE_PATH + userId + RESUME;
        }

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(multipartFile.getContentType());
        metadata.setContentLength(multipartFile.getSize());

        // Upload the file
        s3.putObject(new PutObjectRequest(bucketName, keyName, multipartFile.getInputStream(), metadata));

        // Return the file URL
        return s3.getUrl(bucketName, keyName);
    }

    /**
     * Gets the profile picture from S3.
     * @param userid The user id of the user whose profile picture is to be retrieved.
     * @return The URL of the profile picture.
     */
    public URL getProfilePictureFromS3(String userid) {
        AmazonS3 s3 = getS3Client();
        String keyName = USER_FILE_PATH + userid + PROFILE_PICTURE;
        if (!doesFileExist(s3, keyName)) {
            return null;
        }
        Date expiration = getExpirationDateForURL();
        return generatePresignedURL(keyName, expiration, s3);

    }

    /**
     * Gets the resume from S3.
     * @param userid The user id of the user whose resume is to be retrieved.
     * @return The URL of the resume.
     */
    public URL getResumeFromS3(String userid) {
        AmazonS3 s3 = getS3Client();
        Date expiration = getExpirationDateForURL();
        String keyName = USER_FILE_PATH + userid + RESUME;
        return generatePresignedURL(keyName, expiration, s3);
    }

    private AmazonS3 getS3Client() {
        return s3ClientConfiguration.getS3client();
    }

    private boolean isFileAProfilePicture(MultipartFile multipartFile) {
        S3FileType s3FileType = getSupportedFileType(multipartFile);
        return s3FileType.equals(S3FileType.PROFILE_PICTURE);
    }

    private S3FileType getSupportedFileType(MultipartFile multipartFile) throws UnsupportedFileTypeException {
        String mimeType = multipartFile.getContentType();
        S3FileType fileType = S3FileType.getByLabel(mimeType);
        log.info(mimeType);

        if (fileType != null) {
            return fileType;
        } else {
            throw new UnsupportedFileTypeException("File Not Supported. Filetype " + mimeType + " was found.");
        }
    }

    private Date getExpirationDateForURL() {
        // Set expiration time for the pre-signed URL
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 60; // 1 hour
        expiration.setTime(expTimeMillis);
        return expiration;
    }


    private URL generatePresignedURL(String keyName, Date expiration, AmazonS3 s3) {
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucketName, keyName)
                        .withMethod(HttpMethod.GET)
                        .withExpiration(expiration);
        return s3.generatePresignedUrl(generatePresignedUrlRequest);
    }

    private boolean doesFileExist(AmazonS3 s3, String keyName) {
        return s3.doesObjectExist(bucketName, keyName);
    }
}
