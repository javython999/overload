# Project Guidelines

# Package
패키지는 Controller, Service, repository, dto, entity를 사용한다.
1. Controller는 web controller 클래스가 위치한다.
2. Service는 @Service 클래스들이 위치한다.
3. repository는 JPA Repository 인터페이스들이 위치한다.
4. dto는 Dto 클래스들이 위치한다.
5. entity는 JPA entity들이 위치한다.

---

# Entity
## Load
부하 테스트 정보가 담긴 entity이다.
Load는 1개의 script를 가지고 있다.

## Script
* k6 스크립트이다. 
* 파일로 업로드 되며 Script Entity는 파일의 저장 정보를 가지고 있다.
* overload-worker 모듈에서 다운 받아서 실행하게 된다.

## AttacheFile
* Load에 첨부된 파일이다.
* Load에 첨부된 Script를 수행하는데 필요한 파일들이다.
* overload-worker 모듈에서 다운 받아서 실행하게 된다.

## Summary
* Load의 결과이다.

---

# summary.json 예시
```json
{
  "overview": {
    "iterations": {
      "total": 21825,
      "rate": 336.2892897389768
    },
    "vus": {
      "min": 8,
      "max": 2974
    },
    "requests": {
      "total": 21825,
      "rate": 336.2892897389768
    },
    "data_received": {
      "total": 5296698,
      "rate": 81613.87438175756
    },
    "data_sent": {
      "total": 2553525,
      "rate": 39345.84689946028
    }
  },
  "trends": [
    {
      "metric": "http_req_blocked",
      "avg": 1.302520192439862,
      "min": 0,
      "med": 0,
      "max": 22.8618,
      "p90": 9.0536,
      "p95": 9.675080000000001
    },
    {
      "metric": "http_req_connecting",
      "avg": 1.2963188178694156,
      "min": 0,
      "med": 0,
      "max": 20.0002,
      "p90": 9.0173,
      "p95": 9.63864
    },
    {
      "metric": "http_req_duration",
      "avg": 9.438669030927814,
      "min": 6.5212,
      "med": 9.355,
      "max": 70.9324,
      "p90": 10.230480000000002,
      "p95": 10.518559999999999
    },
    {
      "metric": "http_req_receiving",
      "avg": 0.0963706254295531,
      "min": 0,
      "med": 0,
      "max": 9.404,
      "p90": 0.5171,
      "p95": 0.7003599999999999
    },
    {
      "metric": "http_req_sending",
      "avg": 0.008185223367697595,
      "min": 0,
      "med": 0,
      "max": 13.0942,
      "p90": 0,
      "p95": 0
    },
    {
      "metric": "http_req_tls_handshaking",
      "avg": 0,
      "min": 0,
      "med": 0,
      "max": 0,
      "p90": 0,
      "p95": 0
    },
    {
      "metric": "http_req_waiting",
      "avg": 9.334113182130563,
      "min": 6.5212,
      "med": 9.2465,
      "max": 70.9324,
      "p90": 10.11416,
      "p95": 10.391979999999998
    },
    {
      "metric": "iteration_duration",
      "avg": 3021.9389504283927,
      "min": 1009.1274,
      "med": 3020.5249,
      "max": 5018.9836,
      "p90": 4633.5470000000005,
      "p95": 4820.46876
    }
  ],
  "failed": {
    "rate": 0,
    "rate_percent": "0.00",
    "passes": 0,
    "fails": 21825
  }
}
```