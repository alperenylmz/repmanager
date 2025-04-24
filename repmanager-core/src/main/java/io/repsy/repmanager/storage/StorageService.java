package io.repsy.repmanager.storage;

import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface StorageService {
    void save(String packageName, String version, MultipartFile file, String fileName);
    Path load(String packageName, String version, String fileName);
}
