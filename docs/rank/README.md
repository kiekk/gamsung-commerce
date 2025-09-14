## 상품 랭킹 시뮬레이션 환경 구성

### 1. 사전 데이터 설정
- `ddl-auto: none`으로 설정되어 있어 `commerce-api` 실행 후 ddl, dml 스크립트 실행
    - [ddl.sql](../../data/ddl.sql)
    - [brand.sql](../../data/brand.sql)
    - [member.sql](../../data/member.sql)
    - [product.sql](../../data/product.sql)
    - [product_like_count.sql](../../data/product_like_count.sql)

### 2. 구동 및 접속 경로
- `commerce-api` 실행
- `commerce-streamer` 실행
    - 실행 시 `profiles=local,rank-simulation` 추가 후 실행
- 접속 경로
    - 일별 랭킹: `http://localhost:8080/rankings/daily`
    - 시간별 랭킹: `http://localhost:8080/rankings/hourly`
