# config 패키지

애플리케이션 레벨의 설정 빈을 모아두는 패키지.

## 클래스 목록

| 클래스 | 역할 |
|--------|------|
| `KafkaConfig` | `@EnableKafka` 활성화 — Spring Kafka 어노테이션 기반 리스너 사용을 활성화 |

## 주의사항

- Kafka Producer/Consumer 세부 설정(bootstrap servers, serializer, `max.poll.interval.ms` 등)은 `application.yaml`에서 관리한다.
- 추가 설정 빈이 필요할 경우 역할별로 파일을 분리한다(예: `RestTemplateConfig`).