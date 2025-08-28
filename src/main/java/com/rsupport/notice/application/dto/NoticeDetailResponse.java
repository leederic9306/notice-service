package com.rsupport.notice.application.dto;

import java.time.LocalDateTime;
import java.util.List;

public record NoticeDetailResponse(
        Long id,
        String title,
        String content,
        LocalDateTime createdAt,
        long viewCount,
        String author,
        List<AttachmentResponse> attachments
) {
    public record AttachmentResponse(
            Long id, String originalFilename, String url, String contentType, long size
    ) {}
}
