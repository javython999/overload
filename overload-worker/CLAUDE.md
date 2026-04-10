# overload-worker

부하 테스트 실행 워커 서버. Registry로부터 Kafka 메시지를 받아 k6 스크립트를 실행하고, 결과를 다시 Kafka로 전송하는 무상태(stateless) 실행 엔진이다.

## 기술 스택

- **Java 25**, Spring Boot 4.0.3
- **Spring Kafka** — Registry와의 비동기 메시지 통신
- **Spring Actuator + Micrometer (Prometheus)** — 메트릭 노출 (`/actuator/prometheus`)
- **Lombok** — 보일러플레이트 제거
- **k6** (외부 프로세스) — 실제 부하 테스트 실행 (`ProcessBuilder`)

## 주요 Kafka 토픽

| 방향 | 토픽 | 설명 |
|------|------|------|
| Consumer | `load-test` | Registry가 보내는 테스트 실행 요청 수신 |
| Producer | `load-status` | 테스트 상태(RUNNING/COMPLETED/FAILED) 전송 |
| Producer | `load-result` | k6 `summary.json` 결과 전송 |

## 테스트 실행 흐름

1. `load-test` 토픽 메시지 수신 (`KafkaConsumeDto`: loadId, scriptFileName)
2. Registry에서 스크립트 파일 ZIP 다운로드 후 압축 해제 (`DownloadService`)
3. `wrapper.js` 동적 생성 — htmlReport + handleSummary 포함
4. `k6 run --out experimental-prometheus-rw <wrapper.js>` 실행 (ProcessBuilder)
5. 실행 로그 → `script.log-path/load_{id}/load.log`
6. `summary.json` 읽어 `load-result` 토픽으로 전송
7. 실행 디렉토리 정리(삭제)

## 패키지 구조

```
com.errday.overloadworker
├── config/      — Kafka 설정
├── dto/         — KafkaConsumeDto, LoadStatusDto
├── service/
│   ├── LoadConsumerService   — Kafka 소비 + k6 실행 오케스트레이션
│   ├── DownloadService       — Registry에서 파일 다운로드 + 압축 해제
│   └── KafkaProducerService  — 상태/결과 메시지 발행
└── LoadStatus.java           — RUNNING / COMPLETED / FAILED 열거형
```

## 설정값 (`application.yaml`)

| 키 | 설명 |
|----|------|
| `script.download-path` | 다운로드된 스크립트 저장 경로 |
| `script.log-path` | k6 실행 로그 저장 경로 |
| `script.registry-url` | Registry 파일 다운로드 엔드포인트 |
| `script.bundle-script` | htmlReport 번들 JS 경로 |
| `prometheus.endpoints` | Prometheus Remote Write URL |

## 외부 의존성

- **Kafka** — `localhost:29092`
- **k6** — 실행 환경에 `k6` 바이너리가 PATH에 있어야 한다
- **Registry** — `script.registry-url`로 파일 다운로드 (기본: `http://localhost:8080/download`)
- **Prometheus** — Remote Write로 메트릭 푸시

## 빌드 및 실행

```bash
./gradlew bootRun

./gradlew test
```

## 주의사항

- `LoadConsumerService`의 Kafka 리스너 `concurrency = "1"`로 설정되어 있어 동시에 하나의 테스트만 처리한다. 병렬 처리가 필요하다면 concurrency 값과 k6 프로세스 관리를 함께 검토해야 한다.
- k6 실행 시 `--insecure-skip-tls-verify` 플래그가 기본 적용된다. 운영 환경에서는 인증서 검증 여부를 재검토할 것.
- `max.poll.interval.ms: 900000` (15분) — k6 실행 시간이 길어질 경우를 고려한 설정이다.
- DB 의존성 없음 — 순수 메시지 기반 무상태 서비스이다.
