package com.rsupport.notice.presentation;

import com.rsupport.notice.application.dto.NoticeCreateRequest;
import com.rsupport.notice.application.dto.NoticeDetailResponse;
import com.rsupport.notice.application.dto.NoticeListItemResponse;
import com.rsupport.notice.application.dto.NoticeUpdateRequest;
import com.rsupport.notice.application.service.NoticeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notices")
public class NoticeController {

    private final NoticeService noticeService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Long create(@Valid @RequestPart("payload") NoticeCreateRequest payload,
                       @RequestPart(name="files", required=false) MultipartFile[] files) {
        return noticeService.create(payload, files == null ? null : java.util.Arrays.asList(files));
    }

    @PutMapping(value="/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void update(@PathVariable("id") Long id,
                       @RequestPart("payload") NoticeUpdateRequest payload,
                       @RequestPart(name="files", required=false) MultipartFile[] files) {
        noticeService.update(id, payload, files == null ? null : java.util.Arrays.asList(files));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        noticeService.delete(id);
    }

    @GetMapping
    public Page<NoticeListItemResponse> list(
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "titleOnly", defaultValue = "false") boolean titleOnly,
            @RequestParam(name = "from", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(name = "to", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    )  {
        return noticeService.list(q, titleOnly, from, to, pageable);
    }

    @GetMapping("/{id}")
    public NoticeDetailResponse detail(@PathVariable("id") Long id) {
        return noticeService.detail(id);
    }
}
