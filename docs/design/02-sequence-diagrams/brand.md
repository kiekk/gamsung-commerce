## 브랜드 시퀀스 다이어그램

### 브랜드 목록 조회

```mermaid
sequenceDiagram
    participant User
    participant BrandController
    participant UserService
    participant BrandService
    User ->> BrandController: 브랜드 목록 조회 요청
    alt 인증 헤더(`X-USER-ID`)가 없는 경우
        BrandController -->> User: 인증 예외 (401 Unauthorized)
    end
    BrandController ->> UserService: 사용자 정보 조회
    alt 사용자 정보가 존재하지 않는 경우
        UserService -->> BrandController: 인증 예외 (401 Unauthorized)
    else 사용자 정보가 존재하는 경우
        BrandController ->> BrandService: 브랜드 목록 조회<br/>(페이징, 정렬, 필터링)
        alt 잘못된 정렬, 페이징 조건인 경우
            BrandService -->> BrandController: 잘못된 요청 예외 (400 Bad Request)
        else 올바른 정렬, 페이징 조건인 경우
            BrandService -->> BrandController: 브랜드 목록 응답 (200 OK)
        end
    end
```

### 브랜드 상세 조회

```mermaid
sequenceDiagram
    participant User
    participant BrandController
    participant UserService
    participant BrandService
    User ->> BrandController: 브랜드 상세 조회 요청
    alt 인증 헤더(`X-USER-ID`)가 없는 경우
        BrandController -->> User: 인증 예외 (401 Unauthorized)
    end
    BrandController ->> UserService: 사용자 정보 조회
    alt 사용자 정보가 존재하지 않는 경우
        UserService -->> BrandController: 인증 예외 (401 Unauthorized)
    else 사용자 정보가 존재하는 경우
        BrandController ->> BrandService: 브랜드 상세 조회

        alt 브랜드 정보가 존재하지 않는 경우
            BrandService -->> BrandController: 브랜드 없음 예외 (404 Not Found)
        else 브랜드 정보가 존재하는 경우
            BrandService -->> BrandController: 브랜드 정보 응답 (200 OK)
        end
    end
```

### 브랜드 등록

```mermaid
sequenceDiagram
    participant User
    participant BrandController
    participant UserService
    participant BrandService
    User ->> BrandController: 브랜드 등록 요청
    alt 인증 헤더(`X-USER-ID`)가 없는 경우
        BrandController -->> User: 인증 예외 (401 Unauthorized)
    end
    BrandController ->> UserService: 사용자 정보 조회
    alt 사용자 정보가 존재하지 않는 경우
        UserService -->> BrandController: 인증 예외 (401 Unauthorized)
    else 사용자 정보가 존재하는 경우
        BrandController ->> BrandService: 브랜드 등록 요청<br/>(필수 정보 포함)
        alt 필수 정보가 누락된 경우
            BrandService -->> BrandController: 잘못된 요청 예외 (400 Bad Request)
        else 필수 정보가 모두 제공된 경우
            BrandService -->> BrandController: 브랜드 등록 성공 응답 (201 Created, 생성된 브랜드 ID)
        end
    end
```
