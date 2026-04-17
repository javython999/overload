# com.errday.overloadworker 루트 패키지

애플리케이션 진입점과 공용 열거형을 포함하는 최상위 패키지.

## 클래스 목록

| 클래스 | 역할 |
|--------|------|
| `OverloadWorkerApplication` | Spring Boot 진입점 (`@SpringBootApplication`) |
| `LoadStatus` | 부하 테스트 상태 열거형 — `REGISTERED`, `RUNNING`, `COMPLETED`, `FAILED` |

## LoadStatus 위치에 대한 참고

`LoadStatus`는 registry 모듈의 `enums.LoadStatus`와 동일한 값을 가지지만 별도로 정의되어 있다. Worker는 Registry에 의존하지 않는 독립 모듈이므로 각자 열거형을 소유한다.