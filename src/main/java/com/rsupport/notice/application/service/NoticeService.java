package com.rsupport.notice.application.service;


import com.rsupport.notice.application.dto.*;
import com.rsupport.notice.application.query.NoticeSpecification;
import com.rsupport.notice.domain.notice.*;
import com.rsupport.notice.infrastructure.filestorage.FileStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final FileStorage fileStorage;

    @Transactional
    public Long create(NoticeCreateRequest req, List<MultipartFile> files) {
        Notice n = Notice.builder()
                .title(req.title()).content(req.content()).author(req.author())
                .startAt(req.startAt()).endAt(req.endAt()).viewCount(0).build();
        n.validatePeriod();

        if (files != null) {
            for (MultipartFile f : files) {
                if (f.isEmpty()) continue;
                var stored = fileStorage.store(f);
                n.addAttachment(Attachment.builder()
                        .originalFilename(stored.originalFilename())
                        .storedFilename(stored.storedFilename())
                        .contentType(stored.contentType())
                        .size(stored.size())
                        .url(stored.url())
                        .build());
            }
        }
        return noticeRepository.save(n).getId();
    }

    @Transactional
    public void update(Long id, NoticeUpdateRequest req, List<MultipartFile> newFiles) {
        Notice n = noticeRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Notice not found"));
        n.update(req.title(), req.content(), req.startAt(), req.endAt());

        if (req.removeAttachmentIds() != null && !req.removeAttachmentIds().isEmpty()) {
            n.removeAttachmentsByIds(req.removeAttachmentIds());
        }
        if (newFiles != null) {
            for (MultipartFile f : newFiles) {
                if (f.isEmpty()) continue;
                var stored = fileStorage.store(f);
                n.addAttachment(Attachment.builder()
                        .originalFilename(stored.originalFilename())
                        .storedFilename(stored.storedFilename())
                        .contentType(stored.contentType())
                        .size(stored.size())
                        .url(stored.url())
                        .build());
            }
        }
    }

    @Transactional
    public void delete(Long id) {
        noticeRepository.deleteById(id);
    }

    public Page<NoticeListItemResponse> list(String q, boolean titleOnly,
                                             java.time.LocalDate from, java.time.LocalDate to,
                                             Pageable pageable) {
        Specification<Notice> spec = Specification.not(null); // 빈 스펙으로 시작

        if (q != null && !q.isBlank()) {
            spec = spec.and(NoticeSpecification.keyword(q, titleOnly));
        }
        if (from != null || to != null) {
            spec = spec.and(NoticeSpecification.createdBetween(from, to));
        }
        Page<Notice> page = noticeRepository.findAll(spec, pageable);
        return page.map(n -> new NoticeListItemResponse(
                n.getId(), n.getTitle(), n.hasAttachments(), n.getCreatedAt(), n.getViewCount(), n.getAuthor()
        ));
    }

    public NoticeDetailResponse detail(Long id) {
        Notice n = noticeRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Notice not found"));

        var att = n.getAttachments().stream()
                .map(a -> new NoticeDetailResponse.AttachmentResponse(
                        a.getId(), a.getOriginalFilename(), a.getUrl(), a.getContentType(), a.getSize()
                )).toList();

        return new NoticeDetailResponse(n.getId(), n.getTitle(), n.getContent(),
                n.getCreatedAt(), n.getViewCount(), n.getAuthor(), att);
    }
}
