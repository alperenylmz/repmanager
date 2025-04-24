package io.repsy.repmanager.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.repsy.repmanager.model.PackageEntity;
import io.repsy.repmanager.repository.PackageRepository;
import io.repsy.repmanager.storage.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PackageService {

    private final PackageRepository packageRepository;
    private final StorageService storageService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void uploadPackage(String name, String version, MultipartFile packageFile, MultipartFile metaFile) {
        try {
            // meta.json içeriğini kontrol et
            Map<String, Object> metaData = objectMapper.readValue(metaFile.getInputStream(), Map.class);

            if (!metaData.get("name").equals(name) || !metaData.get("version").equals(version)) {
                throw new IllegalArgumentException("Meta.json içeriği URL ile uyuşmuyor.");
            }

            // Dosyaları kaydet
            storageService.save(name, version, packageFile, "package.rep");
            storageService.save(name, version, metaFile, "meta.json");

            // Path'leri al
            Path repPath = storageService.load(name, version, "package.rep");
            Path metaPath = storageService.load(name, version, "meta.json");

            // Veritabanına kaydet
            PackageEntity entity = PackageEntity.builder()
                    .name(name)
                    .version(version)
                    .author((String) metaData.get("author"))
                    .repFilePath(repPath.toString())
                    .metaFilePath(metaPath.toString())
                    .createdAt(LocalDateTime.now())
                    .build();

            packageRepository.save(entity);

            log.info("Package {}:{} uploaded.", name, version);

        } catch (IOException e) {
            throw new RuntimeException("Dosya işlenirken hata oluştu", e);
        }
    }
}
