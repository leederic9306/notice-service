package com.rsupport.notice.application.query;

import com.rsupport.notice.domain.notice.Notice;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class NoticeSpecification {
    public static Specification<Notice> keyword(String q, boolean titleOnly) {
        if (q == null || q.isBlank()) return (r, cq, cb) -> cb.conjunction();
        String like = "%" + q.trim().toLowerCase() + "%";
        return (root, cq, cb) -> titleOnly
                ? cb.like(cb.lower(root.get("title")), like)
                : cb.or(
                cb.like(cb.lower(root.get("title")), like),
                cb.like(cb.lower(cb.toString(root.get("content"))), like)
        );
    }

    public static Specification<Notice> createdBetween(LocalDate from, LocalDate to) {
        if (from == null && to == null) return (r, cq, cb) -> cb.conjunction();
        LocalDateTime start = (from != null ? from : LocalDate.MIN).atStartOfDay();
        LocalDateTime end = (to != null ? to.plusDays(1) : LocalDate.MAX).atStartOfDay();
        return (root, cq, cb) -> cb.between(root.get("createdAt"), start, end);
    }
}
