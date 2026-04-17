# service 패키지

Worker의 핵심 실행 로직을 담당하는 패키지.

## 클래스 목록

| 클래스 | 역할 |
|--------|------|
| `LoadConsumerService` | `load-test` 토픽 소비 + k6 실행 전체 오케스트레이션 |
| `DownloadService` | Registry에서 ZIP 파일 다운로드 후 압축 해제 |
| `KafkaProducerService` | `load-status`, `load-result` 토픽으로 메시지 발행 |

## LoadConsumerService 실행 흐름

```
1. load-test 수신
2. sendStatus(RUNNING)
3. downloadAndUnzipLoadFiles(loadId)          ← DownloadService
4. wrapperScriptFile 동적 생성 (wrapper.js)
5. k6 run --out experimental-prometheus-rw <wrapper.js>   ← ProcessBuilder
6. summary.json 읽기 → sendResult(loadId, json)          ← KafkaProducerService
7. sendStatus(COMPLETED)
8. finally: loadDir 삭제, 예외 시 sendStatus(FAILED)
```

## wrapper.js 구조

k6 실행 전 동적으로 생성되는 래퍼 스크립트. 원본 스크립트를 import하여 다음을 추가한다.
- `htmlReport` 번들(`script.bundle-script`)을 통한 HTML 결과 리포트 생성
- `handleSummary` 함수: `summary.json`(Registry 전송용) + `summary.html` 생성
- `buildOverview`, `buildTrends`, `buildRates`로 메트릭 구조화

## 규칙

- Kafka 리스너 `concurrency = "1"` — 한 번에 하나의 테스트만 순차 처리한다.
- 메시지 처리 중 예외 발생 시 상태를 `FAILED`로 전송하고 예외를 삼킨다(컨슈머 중단 방지).
- k6 프로세스 stdout/stderr는 `{log-path}/load_{id}/load.log`에 리다이렉트된다.
- `DownloadService.unzip()`은 Zip Slip 취약점 방어 로직(`newFile` 메서드)을 포함한다.
- Windows 경로를 k6 ESM import용 `file://` URL로 변환할 때 `formatFileUrl()`을 사용한다 (백슬래시 → 슬래시, 드라이브 문자 앞에 `/` 추가).
- `KafkaProducerService.sendResult()`는 JSON 문자열을 직접 조립하므로 summaryJson이 유효한 JSON인지 호출 전 확인해야 한다.
