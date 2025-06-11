package com.clinicaregional.clinica.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {
    @Value("${AWS_ACCESS_KEY}")
    private String accessKey;

    @Value("${AWS_SECRET_KEY}")
    private String secretKey;

    @Value("${AWS_S3_REGION}")
    private String awsS3Region;

    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials awsBasicCreds = AwsBasicCredentials.create(accessKey, secretKey);
        return S3Client.builder().region(Region.of(awsS3Region)).credentialsProvider(StaticCredentialsProvider
                .create(awsBasicCreds)).build();
    }
}
