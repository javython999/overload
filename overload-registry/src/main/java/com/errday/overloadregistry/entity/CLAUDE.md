# entity 패키지

JPA 엔티티 클래스를 모아두는 패키지. 모든 엔티티는 `@EntityListeners(AuditingEntityListener.class)`를 통해 생성·수정 시각을 자동 기록한다.

## 엔티티 목록

| 엔티티 | 테이블 | 설명 |
|--------|--------|------|
| `Load` | `loads` | 부하 테스트의 핵심 엔티티. `LoadStatus`, `AttacheFile` 목록을 소유 |
| `AttacheFile` | `attache_files` | `Load`에 첨부된 파일 메타데이터 (원본명, 저장명, 저장경로) |
| `Script` | `scripts` | `Load`에 1:1로 연결된 k6 스크립트 파일 메타데이터 |
| `Summary` | `summaries` | `Load`에 1:1로 연결된 k6 실행 결과 요약. `summaryData`에 JSON 문자열 저장 |
| `Client` | `clients` | Worker 클라이언트 IP 및 설명 정보 |

## 연관관계

```
Load (1) ─── (N) AttacheFile   // CascadeType.ALL, orphanRemoval=true
Load (1) ─── (1) Script        // Script 쪽에서 @JoinColumn
Load (1) ─── (1) Summary       // Summary 쪽에서 @JoinColumn
```

## 규칙

- 연관 엔티티 조회는 모두 `FetchType.LAZY`로 설정한다.
- `Load.addAttacheFile()`처럼 양방향 연관관계 편의 메서드는 소유 측(부모) 엔티티에 정의한다.
- `Summary.getSummaryDataAsJson()`처럼 저장된 JSON 문자열을 `JsonNode`로 변환하는 메서드는 엔티티 내부에 둔다.
- `Script.saveFullPath()`처럼 경로 조합 로직도 엔티티 내부에 캡슐화한다.
