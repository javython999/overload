# overload-registry

부하 테스트 등록 및 관리 서버. 사용자가 부하 테스트를 등록하고, Worker에게 실행을 지시하며, 결과를 수집·조회하는 중앙 허브 역할을 한다.

## 기술 스택

- **Java 25**, Spring Boot 4.0.3
- **Spring MVC + Thymeleaf** — 서버사이드 렌더링 UI
- **Spring Data JPA + MySQL** — 테스트 메타데이터 영속화
- **P6Spy** (`p6spy-spring-boot-starter`) — SQL 쿼리 로깅
- **Spring Kafka** — Worker와의 비동기 메시지 통신
- **Lombok** — 보일러플레이트 제거
- **Apache Commons IO / Groovy** — 파일 처리 유틸리티
- **Thymeleaf Layout Dialect** — 레이아웃 템플릿 구성

## 주요 Kafka 토픽

| 방향 | 토픽 | 설명 |
|------|------|------|
| Producer | `load-test` | Worker에 부하 테스트 실행 요청 전송 |
| Consumer | `load-status` | Worker가 보내는 테스트 상태(RUNNING/COMPLETED/FAILED) 수신 |
| Consumer | `load-result` | Worker가 보내는 k6 요약(summary.json) 수신 후 DB 저장 |

## 패키지 구조

```
com.errday.overloadregistry
├── config/          — P6Spy, Thymeleaf 설정
├── controller/      — MVC 컨트롤러 (UI) + REST 컨트롤러
├── dto/             — 요청/응답 DTO (load, kafka, summary 등)
├── entity/          — JPA 엔티티 (Load, Script, AttacheFile, Summary, Client)
├── enums/           — LoadStatus 열거형
├── repository/      — Spring Data JPA 레포지토리
└── service/         — 비즈니스 서비스 + Orchestration 서비스
```

### Orchestration 패턴

복수의 서비스를 조합하는 복잡한 유스케이스는 `*Orchestration` 클래스로 분리한다.

- `LoadRegisterOrchestration` — 테스트 등록 (파일 저장 + DB + Kafka 발행)
- `LoadDetailOrchestration` — 상세 조회 조합
- `SummarySaveOrchestration` — 결과 요약 저장
- `DownloadOrchestration` — 파일 다운로드 처리
- `LoadRetryOrchestration` — 실패 테스트 재시도

## 외부 의존성

- **MySQL** — `localhost:3306/overload`
- **Kafka** — `localhost:29092`
- **파일 업로드 경로** — `upload.path` (기본: `D:/workspace4_upload/test`)

## 빌드 및 실행

```bash
# 모듈 루트에서 실행
./gradlew bootRun

# 테스트
./gradlew test
```

## 주의사항

- `application.yaml`에 DB 비밀번호가 평문으로 존재한다. 운영 환경에서는 외부 설정(환경변수, Vault 등)으로 대체해야 한다.
- Thymeleaf 캐시는 개발 중 `false`로 설정되어 있다. 운영 배포 시 `true`로 변경할 것.
- P6Spy는 개발용이므로 운영 프로파일에서는 비활성화를 권장한다.
