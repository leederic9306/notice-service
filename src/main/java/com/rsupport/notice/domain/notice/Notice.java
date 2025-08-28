package com.rsupport.notice.domain.notice;

import com.rsupport.notice.domain.common.AbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "notices")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Notice extends AbstractAggregateRoot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=300)
    private String title;

    @Lob @Column(nullable=false)
    private String content;

    @Column(nullable=false)
    private String author;

    private LocalDateTime startAt;
    private LocalDateTime endAt;

    @Column(nullable=false)
    private long viewCount;

    @OneToMany(mappedBy="notice", cascade=CascadeType.ALL, orphanRemoval=true)
    @Builder.Default
    private List<Attachment> attachments = new ArrayList<>();

    public void validatePeriod() {
        if (startAt != null && endAt != null && endAt.isBefore(startAt)) {
            throw new IllegalArgumentException("endAt must be after startAt");
        }
    }

    public void update(String title, String content, LocalDateTime startAt, LocalDateTime endAt) {
        if (title != null) this.title = title;
        if (content != null) this.content = content;
        this.startAt = startAt;
        this.endAt = endAt;
        validatePeriod();
    }

    public void addAttachment(Attachment a) { a.attachTo(this); attachments.add(a); }
    public void removeAttachmentsByIds(Collection<Long> ids) { attachments.removeIf(a -> ids.contains(a.getId())); }
    public boolean hasAttachments() { return !attachments.isEmpty(); }
}
