package io.repsy.repmanager.storage;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@Service
@ConditionalOnProperty(name = "storage.strategy", havingValue = "file-system", matchIfMissing = true)
public class FileSystemStorageService implements StorageService {

    private final Path storageRoot = Paths.get("storage");

    @PostConstruct
    public void init() throws IOException {
        if (!Files.exists(storageRoot)) {
            Files.createDirectories(storageRoot);
        }
    }

    @Override
    public void save(String packageName, String version, MultipartFile file, String fileName) {
        try {
            Path dir = storageRoot.resolve(packageName).resolve(version);
            Files.createDirectories(dir);
            Path filePath = dir.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Dosya kaydedilemedi: " + fileName, e);
        }
    }

    @Override
    public Path load(String packageName, String version, String fileName) {
        return storageRoot.resolve(packageName).resolve(version).resolve(fileName);
    }
}
