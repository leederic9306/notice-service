# Notice

## Stack
- Java 21, Spring Boot 3.5.5
- JPA/Hibernate, H2 (test/dev)
- Redis (조회수 카운터, 캐시)
- TDD: 단위/통합 테스트

## 도메인 모델 (DDD)
- Aggregate: Notice
  - 필드: title, content, author, startAt, endAt, viewCount, attachments
  - 규칙: endAt ≥ startAt
  - 첨부파일은 Entity `Attachment`로 Notice에 종속

## API
- POST /api/notices (multipart)
  - parts: payload`(json), files(optional[])
- PUT /api/notices/{id} (multipart)
  - parts: payload(json: title/content/기간/삭제할 첨부 id 목록), files(추가 첨부)
- DELETE /api/notices/{id}
- GET /api/notices (목록+검색)
  - params: q, titleOnly, from, to, page, size, sort
- GET /api/notices/{id} (상세+조회수 증가)

## 대용량 트래픽 고려
- 상세 응답 캐시: @Cacheable("noticeDetail")
- JPA batch 옵션, 읽기전용 트랜잭션, 페이징 검색
