# service 패키지

비즈니스 로직과 유스케이스 오케스트레이션을 담당하는 패키지.

## 클래스 분류

### 단위 서비스 — 단일 도메인 책임

| 클래스 | 역할 |
|--------|------|
| `LoadService` | `Load` 엔티티 CRUD, 상태 업데이트, 페이징 조회 |
| `ScriptService` | 스크립트 파일 저장 및 `Script` 엔티티 관리 |
| `AttacheFileService` | 첨부 파일 저장 및 `AttacheFile` 엔티티 관리 |
| `SummaryService` | `Summary` 엔티티 저장 및 조회 |
| `FileService` | 실제 파일 I/O (디스크 저장, 읽기, ZIP 생성) |
| `KafkaProducerService` | `load-test` 토픽으로 메시지 발행 |
| `LoadConsumerService` | `load-status`, `load-result` 토픽 소비 후 상태 갱신 및 요약 저장 |

### Orchestration 서비스 — 복수 서비스 조합

| 클래스 | 역할 |
|--------|------|
| `LoadRegisterOrchestration` | 테스트 등록 플로우: DB 저장 → 파일 저장 → Kafka 발행 |
| `LoadDetailOrchestration` | 상세 조회 조합: Load + Script + AttacheFile + Summary 조회 |
| `SummarySaveOrchestration` | 요약 저장 플로우: Load 조회 → Summary 저장 → 상태 업데이트 |
| `DownloadOrchestration` | 파일 다운로드: 원본파일명 조회 + 리소스 반환, ZIP 생성 |
| `LoadRetryOrchestration` | 실패 테스트 재시도: 상태 초기화 → Kafka 재발행 |

## 규칙

- 단위 서비스는 자신의 도메인 엔티티와 레포지토리만 의존한다.
- 여러 단위 서비스를 조합해야 하는 유스케이스는 반드시 `*Orchestration` 클래스로 분리한다.
- 쓰기 트랜잭션은 `@Transactional`, 읽기 전용은 `@Transactional(readOnly = true)`를 명시한다.
- Kafka Consumer(`LoadConsumerService`)는 메시지 처리 실패 시 예외를 삼키고 로그만 남긴다 (컨슈머 중단 방지).
