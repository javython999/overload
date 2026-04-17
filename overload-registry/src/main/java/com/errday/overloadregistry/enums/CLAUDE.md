# enums 패키지

도메인에서 사용하는 열거형을 모아두는 패키지.

## 열거형 목록

### `LoadStatus` — 부하 테스트 상태

| 값 | 한글 | UI 클래스 (Tailwind) |
|----|------|----------------------|
| `REGISTERED` | 등록 | `bg-blue-100 text-blue-700` |
| `RUNNING` | 진행 | `bg-purple-100 text-purple-700` |
| `COMPLETED` | 완료 | `bg-green-100 text-green-700` |
| `FAILED` | 실패 | `bg-red-100 text-red-700` |

- `text`: Thymeleaf 템플릿에서 한글 레이블 표시에 사용
- `className`: Thymeleaf 템플릿에서 Tailwind CSS 배지 클래스에 사용

## 상태 전이

```
REGISTERED → RUNNING → COMPLETED
                     ↘ FAILED
```

`LoadConsumerService`가 `load-status` 토픽을 소비하여 상태를 갱신한다.
`Load.isCompleted()`로 완료 여부를 확인할 수 있다.
