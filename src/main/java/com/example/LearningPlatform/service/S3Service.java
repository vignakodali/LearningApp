package com.example.LearningPlatform.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class S3Service {

    private static final Logger logger = LoggerFactory.getLogger(S3Service.class);

    @Value("${aws.s3.bucket}")
    private String bucketName;

    private final S3Client s3Client;
    private final S3Presigner presigner;

    public S3Service(S3Client s3Client, S3Presigner presigner) {
        this.s3Client = s3Client;
        this.presigner = presigner;
    }
    public String uploadFile(String keyPrefix, MultipartFile file, String contentType) {
        String key = keyPrefix + "/" + file.getOriginalFilename();
        try {
            s3Client.putObject(
                PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(contentType)
                    .build(),
                RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file to S3", e);
        }
        return getFileUrl(key);
    }
    public String getFileUrl(String key) {
        return "https://" + bucketName + ".s3.amazonaws.com/" + key;
    }
    public URL generatePresignedUrl(String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .responseContentDisposition("inline")
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(getObjectRequest)
                .build();
        
        return presigner.presignGetObject(presignRequest).url();
    }
    public List<URL> listMaterialsWithPresignedUrls(String keyPrefix) {
        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(keyPrefix)
                .build();

        ListObjectsV2Response response = s3Client.listObjectsV2(request);
        return response.contents().stream()
                .map(s3Object -> generatePresignedUrl(s3Object.key()))
                .collect(Collectors.toList());
    }
public void deleteFile(String materialKey) {
    try {
        logger.info("Attempting to delete from S3 with key: {}", materialKey);

        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(materialKey)
                .build());

        logger.info("Successfully deleted from S3: {}", materialKey);
    } catch (S3Exception e) {
        logger.error("Failed to delete from S3: {}", materialKey, e);
        throw new RuntimeException("Error deleting file from S3", e);
    }
}

}
