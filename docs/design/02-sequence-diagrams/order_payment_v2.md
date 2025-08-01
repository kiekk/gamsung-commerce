## 주문 시퀀스 다이어그램 V1

```markdown
특징:
주문 생성, 결제 처리, 주문 후처리 단계로 시퀀스 다이어그램을 분리해봤습니다.
이렇게 작성하게 될 경우 각 단계의 흐름을 명확하게 파악할 수 있어 이해하기 쉽다고 생각합니다.
```

### 주문 생성 시퀀스 다이어그램

```mermaid
sequenceDiagram
    participant User
    participant OrderController
    participant UserService
    participant OrderService
    activate User
    User ->> OrderController: 주문 요청 (배송지 정보, 주문 상품 목록 및 수량)
    activate OrderController
    alt 인증 헤더(`X-USER-ID`)가 없는 경우
        OrderController -->> User: 인증 예외 (401 Unauthorized)
        deactivate User
    end
    OrderController ->> UserService: 사용자 정보 조회
    activate UserService
    alt 사용자 정보가 존재하지 않는 경우
        UserService -->> OrderController: 인증 예외 (401 Unauthorized)
        deactivate UserService
    end
    activate OrderService
    alt 주문 상품 목록이 비어있는 경우
        OrderService -->> OrderController: 잘못된 요청 예외 (400 Bad Request)
    end
    alt 주문 불가능한 상품이 하나 이상인 경우(비활성 상태, 재고 부족 등)
        OrderService -->> OrderController: 잘못된 요청 예외 (409 Conflict)
    end
    activate OrderController
    OrderController ->> OrderService: 주문 생성 요청 (사용자 ID, 배송지 정보, 주문 상품 목록)
    deactivate OrderController
    alt 주문 생성 실패
        OrderService -->> OrderController: 주문 처리 실패 예외 (500 Internal Server Error)
    end
    OrderService ->> OrderController: 주문 생성 성공 응답 (200 OK)
    deactivate OrderService
    deactivate OrderController
```

### 결제 처리 시퀀스 다이어그램

```mermaid
sequenceDiagram
    participant User
    participant OrderController
    participant UserService
    participant PaymentService
    participant PaymentGateway
    participant ProductService
    activate User
    User ->> OrderController: 결제 요청 (주문 ID)
    activate OrderController
    alt 인증 헤더(`X-USER-ID`)가 없는 경우
        OrderController -->> User: 인증 예외 (401 Unauthorized)
        deactivate User
    end
    OrderController ->> UserService: 사용자 정보 조회
    activate UserService
    alt 사용자 정보가 존재하지 않는 경우
        UserService -->> OrderController: 인증 예외 (401 Unauthorized)
        deactivate UserService
    end
    OrderController ->> PaymentService: 결제 요청 (주문 ID, 금액)
    activate PaymentService
    PaymentService ->> PaymentGateway: 결제 처리 요청 (포인트 차감)
    activate PaymentGateway
    alt 결제 실패
        PaymentGateway -->> PaymentService: 결제 실패 응답
        PaymentService -->> OrderController: 결제 실패 응답 (오류 메시지 등)
    end
    alt 결제 성공
        PaymentGateway -->> PaymentService: 결제 승인 응답
        deactivate PaymentGateway
        PaymentService -->> OrderController: 결제 승인 응답 (결제 상태 등)
        deactivate PaymentService
        activate OrderController
        OrderController -->> ProductService: 재고 차감 요청
        deactivate OrderController
    end
    activate ProductService
    alt 재고 차감 실패
        ProductService -->> OrderController: 재고 차감 실패 예외 (500 Internal Server Error)
    end
    alt 재고 차감 성공
        ProductService -->> OrderController: 결제 성공 성공 응답 (200 OK)
        deactivate ProductService
    end
    deactivate OrderController
```

### 주문 완료 처리 시퀀스 다이어그램

```mermaid
sequenceDiagram
    participant User
    participant OrderController
    participant UserService
    participant OrderService
    activate User
    User ->> OrderController: 주문 완료 요청 (주문 ID)
    activate OrderController
    alt 인증 헤더(`X-USER-ID`)가 없는 경우
        OrderController -->> User: 인증 예외 (401 Unauthorized)
        deactivate User
    end
    OrderController ->> UserService: 사용자 정보 조회
    activate UserService
    alt 사용자 정보가 존재하지 않는 경우
        UserService -->> OrderController: 인증 예외 (401 Unauthorized)
        deactivate UserService
    end
    OrderController ->> OrderService: 주문 정보 조회 요청 (주문 ID)
    activate OrderService
    alt 주문 정보가 존재하지 않는 경우
        OrderService -->> OrderController: 주문 없음 예외 (404 Not Found)
    end
    OrderController ->> OrderService: 주문 완료 처리 요청 (주문 ID)
    alt 주문 완료 처리에 실패한 경우
        OrderService -->> OrderController: 주문 처리 실패 예외 (500 Internal Server Error)
    end
    OrderService -->> OrderController: 주문 완료 처리 성공 응답 (200 OK)
    deactivate OrderService
    deactivate OrderController
```

### 주문 실패 처리 시퀀스 다이어그램

```mermaid
sequenceDiagram
    participant User
    participant OrderController
    participant UserService
    participant OrderService
    activate User
    User ->> OrderController: 주문 실패 요청 (주문 ID)
    activate OrderController
    alt 인증 헤더(`X-USER-ID`)가 없는 경우
        OrderController -->> User: 인증 예외 (401 Unauthorized)
        deactivate User
    end
    OrderController ->> UserService: 사용자 정보 조회
    activate UserService
    alt 사용자 정보가 존재하지 않는 경우
        UserService -->> OrderController: 인증 예외 (401 Unauthorized)
        deactivate UserService
    end
    OrderController ->> OrderService: 주문 정보 조회 요청 (주문 ID)
    activate OrderService
    alt 주문 정보가 존재하지 않는 경우
        OrderService -->> OrderController: 주문 없음 예외 (404 Not Found)
    end
    OrderController ->> OrderService: 주문 실패 처리 요청 (주문 ID) activate OrderService
    alt 주문 실패 처리에 실패한 경우
        OrderService -->> OrderController: 주문 처리 실패 예외 (500 Internal Server Error)
    end
    OrderService -->> OrderController: 주문 실패 처리 성공 응답 (200 OK)
    deactivate OrderService
    deactivate OrderController
```
