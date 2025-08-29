package com.rsupport.notice.repository;

import com.rsupport.notice.application.query.NoticeSpecification;
import com.rsupport.notice.domain.notice.Notice;
import com.rsupport.notice.domain.notice.NoticeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@EnableJpaAuditing
class NoticeRepositoryTest {

    @Autowired NoticeRepository repo;

    @Test
    @DisplayName("공지 정보 저장/조회")
    void save_and_find() {
        Notice saved = repo.save(Notice.builder()
                .title("hello spring").content("content").author("me").viewCount(0)
                .startAt(LocalDateTime.now().minusDays(1)).endAt(LocalDateTime.now().plusDays(1))
                .build());

        Notice found = repo.findById(saved.getId()).orElseThrow();
        assertThat(found.getTitle()).isEqualTo("hello spring");
    }

    @Test
    @DisplayName("제목, 내용(titleOnly=false)을 통한 검색")
    void search_keyword_title_or_content() {
        repo.save(Notice.builder().title("hello spring").content("content").author("me").viewCount(0).build());
        repo.save(Notice.builder().title("java world").content("spring tips").author("me").viewCount(0).build());
        repo.save(Notice.builder().title("kotlin memo").content("none").author("me").viewCount(0).build());

        Specification<Notice> spec = Specification
                .where(NoticeSpecification.keyword("spring", false));

        List<Notice> found = repo.findAll(spec);
        assertThat(found).extracting(Notice::getTitle)
                .containsExactlyInAnyOrder("hello spring", "java world");
    }

    @Test
    @DisplayName("제목(titleOnly=true)을 통한 검색")
    void search_keyword_title_only() {
        repo.save(Notice.builder().title("hello spring").content("xxx").author("me").viewCount(0).build());
        repo.save(Notice.builder().title("java world").content("spring tips").author("me").viewCount(0).build());

        Specification<Notice> spec = Specification
                .where(NoticeSpecification.keyword("spring", true));

        List<Notice> found = repo.findAll(spec);
        assertThat(found).extracting(Notice::getTitle)
                .containsExactly("hello spring"); // content에만 spring인 건 제외
    }

    @Test
    @DisplayName("등록일 기간 검색(createdBetween)")
    void search_created_between() {
        // createdAt은 Auditing이 채우므로, 테스트에서 대략 시간차만 이용
        repo.save(Notice.builder().title("old").content("c").author("a").viewCount(0).build());
        repo.save(Notice.builder().title("new").content("c").author("a").viewCount(0).build());

        // 간단히 페이징 정렬로 최신/오래된 데이터 순서 확인
        Page<Notice> page = repo.findAll(PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt")));
        assertThat(page.getContent()).extracting(Notice::getTitle).contains("new", "old");

        // 기간 필터: 오늘 ~ 내일(포함)
        Specification<Notice> spec = Specification.not(null); // 빈 스펙으로 시작
        spec = spec.and(NoticeSpecification.createdBetween(LocalDate.now(), LocalDate.now()));

        List<Notice> found = repo.findAll(spec);
        assertThat(found).hasSizeGreaterThanOrEqualTo(2); // 둘 다 오늘 생성됨(H2/in-memory)
    }

    @Test
    @DisplayName("페이징, 정렬")
    void paging_and_sorting() {
        for (int i = 0; i < 30; i++) {
            repo.save(Notice.builder().title("t"+i).content("c").author("a").viewCount(i).build());
        }
        Page<Notice> page = repo.findAll(PageRequest.of(1, 10, Sort.by(Sort.Direction.DESC, "createdAt")));
        assertThat(page.getSize()).isEqualTo(10);
        assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(30);
    }
}