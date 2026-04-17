# config 패키지

애플리케이션 레벨의 설정 빈을 모아두는 패키지.

## 클래스 목록

| 클래스 | 역할 |
|--------|------|
| `P6SpyFormatConfig` | P6Spy SQL 로그 포매터. DDL/DML 구분 후 Hibernate `FormatStyle`로 들여쓰기·하이라이트 처리 |
| `ThymeleafConfig` | `LayoutDialect` 빈 등록 — Thymeleaf Layout Dialect를 통한 레이아웃 상속 지원 |

## 주의사항

- `P6SpyFormatConfig`는 개발 편의용이다. 운영 환경에서는 P6Spy 자체를 비활성화하는 것을 권장한다.
- 설정 클래스를 추가할 때는 역할별로 파일을 분리한다(예: `KafkaConfig`, `SecurityConfig`).