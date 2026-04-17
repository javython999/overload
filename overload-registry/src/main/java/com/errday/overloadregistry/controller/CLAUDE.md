# controller 패키지

HTTP 요청을 받아 서비스·오케스트레이션 레이어로 위임하는 계층.

## 클래스 목록

| 클래스 | 타입 | 경로 | 역할 |
|--------|------|------|------|
| `IndexController` | `@Controller` | `/` | 루트(인덱스) 페이지 반환 |
| `LoadController` | `@Controller` | `/loads` | 부하 테스트 등록(POST) 및 상세 뷰(GET) 렌더링 |
| `LoadRestController` | `@RestController` | `/api/loads` | 부하 테스트 목록 페이징, 상세 조회, 재시도 API |
| `DownloadController` | `@RestController` | `/download` | 개별 파일 다운로드 및 Load 전체 파일 ZIP 다운로드 |
| `WorkerMonitorController` | `@Controller` | `/worker-monitor` | Worker 모니터링 UI 페이지 반환 |

## 규칙

- MVC `@Controller`는 Thymeleaf 뷰 이름을 반환하고, REST `@RestController`는 JSON을 반환한다.
- 비즈니스 로직은 서비스나 `*Orchestration` 클래스에 위임한다. 컨트롤러는 얇게 유지한다.
- 파일 다운로드 응답은 `Content-Disposition` 헤더에 UTF-8 인코딩된 파일명을 포함해야 한다.
- 목록 조회 API는 `SliceResponse`(커서 기반)를 사용한다. `Page`(카운트 쿼리 있음)는 사용하지 않는다.