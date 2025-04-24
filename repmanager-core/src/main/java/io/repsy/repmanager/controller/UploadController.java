package io.repsy.repmanager.controller;

import io.repsy.repmanager.service.PackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class UploadController {

    private final PackageService packageService;

    @PostMapping("{packageName}/{version}")
    public ResponseEntity<String> uploadPackage(
            @PathVariable String packageName,
            @PathVariable String version,
            @RequestParam("package") MultipartFile packageFile,
            @RequestParam("meta") MultipartFile metaFile
    ) {
        packageService.uploadPackage(packageName, version, packageFile, metaFile);
        return ResponseEntity.ok("Package uploaded successfully.");
    }
}
