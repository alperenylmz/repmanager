package io.repsy.repmanager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class DownloadController {

    private final Path storageRoot = Paths.get("storage");

    @GetMapping("{packageName}/{version}/{fileName}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable String packageName,
            @PathVariable String version,
            @PathVariable String fileName
    ) throws Exception {
        Path filePath = storageRoot.resolve(packageName).resolve(version).resolve(fileName);
        if (!Files.exists(filePath)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new UrlResource(filePath.toUri());
        String contentType = Files.probeContentType(filePath);
        if (contentType == null) contentType = "application/octet-stream";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }
}
