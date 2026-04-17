# dto 패키지

요청/응답 데이터 전달 객체(DTO)를 모아두는 패키지. 하위 패키지로 용도별로 분리된다.

## 하위 패키지 구조

```
dto/
├── SliceResponse.java          — 커서 기반 페이징 공통 응답 래퍼
├── kafka/                      — Kafka 메시지 송신용 DTO
│   └── KafkaProduceDto         — load-test 토픽에 발행하는 메시지 구조체
└── load/                       — 부하 테스트 관련 DTO
    ├── LoadRegisterRequest     — 테스트 등록 요청 (multipart form)
    ├── LoadListResponse        — 목록 조회 응답
    ├── LoadDetailResponse      — 상세 조회 응답
    ├── LoadStatusResponse      — 상태 업데이트 수신 DTO (Kafka consume)
    ├── attachefile/            — 첨부 파일 관련 응답 DTO
    ├── script/                 — 스크립트 파일 관련 응답 DTO
    └── summary/
        └── SummarySaveRequestDto — load-result 토픽에서 수신하는 요약 저장 요청 DTO
```

## 규칙

- 불변 DTO는 Java `record`로 선언한다.
- Kafka 수신용 DTO는 Jackson 역직렬화를 위해 기본 생성자와 Setter가 필요하다(`@Getter @Setter` 사용).
- `SliceResponse`는 `hasNext` 플래그만 가지며 전체 카운트를 포함하지 않는다.
- `KafkaProduceDto`는 `Load` + `Script` 엔티티를 받아 스스로 필드를 채우는 생성자를 제공한다.
