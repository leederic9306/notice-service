package com.rsupport.notice.application.dto;

import java.time.LocalDateTime;

public record NoticeListItemResponse(
        Long id,
        String title,
        boolean hasAttachments,
        LocalDateTime createdAt,
        long viewCount,
        String author
) {}
