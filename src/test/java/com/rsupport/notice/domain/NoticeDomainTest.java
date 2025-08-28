package com.rsupport.notice.domain;

import com.rsupport.notice.domain.notice.Attachment;
import com.rsupport.notice.domain.notice.Notice;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

public class NoticeDomainTest {
    @Test
    @DisplayName("종료일이 시작일보다 빠르면 예외 발생")
    void period_validation_throws() {
        Notice n = Notice.builder()
                .title("t").content("c").author("a")
                .startAt(LocalDateTime.now())
                .endAt(LocalDateTime.now().minusDays(1))
                .viewCount(0)
                .build();

        assertThrows(IllegalArgumentException.class, n::validatePeriod);
    }

    @Test
    @DisplayName("부분 갱신 및 기간 재검증을 수행")
    void update_changes_fields_and_validates() {
        Notice n = Notice.builder()
                .title("t").content("c").author("a").viewCount(0)
                .build();

        n.update("t2", "c2", null, null);

        assertAll(
                () -> assertEquals("t2", n.getTitle()),
                () -> assertEquals("c2", n.getContent())
        );
    }

    @Test
    @DisplayName("첨부파일 추가/선택 삭제, 도메인 규칙 검증")
    void add_and_remove_attachments_by_id() {
        Notice n = Notice.builder().title("t").content("c").author("a").viewCount(0).build();

        Attachment a1 = Attachment.builder()
                .id(1L).originalFilename("a.txt").storedFilename("s1").contentType("text/plain").size(1L).url("/f/s1")
                .build();
        Attachment a2 = Attachment.builder()
                .id(2L).originalFilename("b.txt").storedFilename("s2").contentType("text/plain").size(1L).url("/f/s2")
                .build();

        n.addAttachment(a1);
        n.addAttachment(a2);
        assertThat(n.getAttachments()).hasSize(2);

        n.removeAttachmentsByIds(Set.of(1L));
        assertThat(n.getAttachments()).extracting(Attachment::getId).containsExactly(2L);
    }

    @Test
    @DisplayName("첨부 파일 유무 판단")
    void has_attachments_flag() {
        Notice n = Notice.builder().title("t").content("c").author("a").viewCount(0).build();
        assertFalse(n.hasAttachments());

        n.addAttachment(Attachment.builder().id(10L).originalFilename("x").storedFilename("x").contentType("t").size(1).url("/f/x").build());
        assertTrue(n.hasAttachments());
    }
}
