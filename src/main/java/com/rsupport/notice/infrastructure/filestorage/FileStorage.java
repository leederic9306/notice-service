package com.rsupport.notice.infrastructure.filestorage;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorage {
    StoredFile store(MultipartFile file);
    record StoredFile(String originalFilename, String storedFilename, String url, String contentType, long size) {}
}
