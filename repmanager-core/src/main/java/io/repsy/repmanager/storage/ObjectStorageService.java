package io.repsy.repmanager.storage;

import io.minio.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@ConditionalOnProperty(name = "storage.strategy", havingValue = "object-storage")
@RequiredArgsConstructor
@Slf4j
public class ObjectStorageService implements StorageService {

    @Value("${storage.minio.endpoint}")
    private String endpoint;

    @Value("${storage.minio.accessKey}")
    private String accessKey;

    @Value("${storage.minio.secretKey}")
    private String secretKey;

    @Value("${storage.minio.bucket}")
    private String bucket;

    private MinioClient minioClient;

    @PostConstruct
    public void init() throws Exception {
        this.minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();

        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }
    }

    @Override
    public void save(String packageName, String version, MultipartFile file, String fileName) {
        try {
            String objectName = packageName + "/" + version + "/" + fileName;
            InputStream is = file.getInputStream();

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .stream(is, is.available(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            log.info("Saved object: {}", objectName);
        } catch (Exception e) {
            throw new RuntimeException("MinIO save error: " + fileName, e);
        }
    }

    @Override
    public Path load(String packageName, String version, String fileName) {
        try {
            String objectName = packageName + "/" + version + "/" + fileName;
            Path tempFile = Files.createTempFile("repmanager-", "-" + fileName);
            Files.deleteIfExists(tempFile); // önceden varsa sil

            minioClient.downloadObject(
                    DownloadObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .filename(tempFile.toString())
                            .build()
            );

            return tempFile;
        } catch (Exception e) {
            throw new RuntimeException("MinIO load error: " + fileName, e);
        }
    }
}
