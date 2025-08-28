package com.rsupport.notice.infrastructure.filestorage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LocalFileStorage implements FileStorage {

    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    @Override
    public StoredFile store(MultipartFile file) {
        try {
            String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
            String stored = UUID.randomUUID() + (ext != null ? "." + ext : "");
            Path dir = Paths.get(uploadDir);
            Files.createDirectories(dir);
            Path dest = dir.resolve(stored);
            Files.copy(file.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);
            String url = "/files/" + stored; // 운영에선 Static Resource or CDN 매핑
            return new StoredFile(file.getOriginalFilename(), stored, url, file.getContentType(), file.getSize());
        } catch (IOException e) {
            throw new RuntimeException("File store failed", e);
        }
    }
}
