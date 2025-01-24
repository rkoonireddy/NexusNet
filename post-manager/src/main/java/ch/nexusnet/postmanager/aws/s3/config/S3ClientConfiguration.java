package ch.nexusnet.postmanager.aws.s3.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class S3ClientConfiguration {

    private static final Regions region = Regions.EU_CENTRAL_2;
    @Value("${amazon.s3.endpoint}")
    private String serviceEndpoint;
    @Value("${amazon.aws.s3.accesskey}")
    private String accessKey;
    @Value("${amazon.aws.s3.secretkey}")
    private String secretKey;
    @Value("${postmanager.aws.s3.bucket}")
    @Getter
    private String bucketName;
    @Getter
    private AmazonS3 s3client;

    @PostConstruct
    private void init() {
        s3client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(
                                serviceEndpoint,
                                region.getName()
                        )
                )
                .withPathStyleAccessEnabled(true)
                .build();
    }
}
