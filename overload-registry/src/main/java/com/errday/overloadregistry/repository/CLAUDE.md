# repository 패키지

Spring Data JPA 레포지토리 인터페이스를 모아두는 패키지.

## 레포지토리 목록

| 인터페이스 | 엔티티 | 비고 |
|-----------|--------|------|
| `LoadRepository` | `Load` | `JpaRepository` 기본 메서드만 사용 |
| `AttacheFileRepository` | `AttacheFile` | — |
| `ScriptRepository` | `Script` | — |
| `SummaryRepository` | `Summary` | — |
| `ClientRepository` | `Client` | — |

## 규칙

- 기본 CRUD는 `JpaRepository`에서 제공하는 메서드를 그대로 사용한다.
- 커스텀 쿼리가 필요할 경우 메서드 네이밍 쿼리를 우선 사용하고, 복잡한 쿼리는 `@Query`(JPQL)를 사용한다.
- Native SQL은 마이그레이션 등 불가피한 경우에만 사용한다.
- 목록 페이징은 `Slice`를 반환한다(`Page`의 카운트 쿼리를 피하기 위해).
