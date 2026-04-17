# dto 패키지

Kafka 메시지 수신/송신에 사용하는 데이터 전달 객체를 모아두는 패키지.

## 클래스 목록

| 클래스 | 방향 | 토픽 | 설명 |
|--------|------|------|------|
| `KafkaConsumeDto` | Consume | `load-test` | Registry가 보내는 테스트 실행 요청 수신 구조체 (loadId, loadName, scriptFileName, attacheFileNames) |
| `LoadStatusDto` | Produce | `load-status` | Worker가 Registry로 전송하는 테스트 상태 메시지 (loadId, LoadStatus) |

## 규칙

- Kafka 메시지는 JSON 직렬화/역직렬화를 사용하므로 기본 생성자와 Getter/Setter가 필요하다.
- `KafkaConsumeDto`의 `attacheFileNames`는 첨부 파일이 없을 경우 `null`이 될 수 있다. 사용 전 null 체크를 수행한다.
- `load-result` 토픽 메시지는 별도 DTO 없이 `KafkaProducerService`에서 직접 JSON 문자열로 조립한다.