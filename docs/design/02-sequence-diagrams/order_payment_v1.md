## 주문 시퀀스 다이어그램 V1

```markdown
특징:
주문과 결제는 별도의 개념이기 때문에 시퀀스 다이어그램도 별도로 작성하는 것이 좋다고 생각하지만,
학습 및 비교의 목적으로 주문 생성과 결제 처리의 흐름을 하나의 시퀀스 다이어그램으로 표현해봤습니다.

이렇게 작성하게 될 경우 주문과 결제의 흐름이 한 눈에 보이는 부분은 괜찮다고 생각하지만,
이 시퀀스 다이어그램으로 비추어 보면 주문과 결제가 하나처럼 보일 수 있기 때문에 이렇게 표현하는 방식은 혼동을 줄 수 있다고 생각합니다.
```

### 주문 생성 & 결제 처리 시퀀스 다이어그램

```mermaid
sequenceDiagram
    participant User
    participant OrderController
    participant UserService
    participant OrderService
    participant CouponService
    participant PaymentService
    participant PaymentGateway
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
    alt 쿠폰 사용 요청이 있는 경우
        OrderController ->> CouponService: 쿠폰 정보 조)
        activate CouponService
        alt 쿠폰 정보가 없는 경우
            CouponService -->> OrderController: 쿠폰 정보 없음 예외 (404 Not Found)
        end
        alt 쿠폰 사용 불가 상태인 경우
            CouponService -->> OrderController: 쿠폰 사용 불가 예외 (409 Conflict)
        end
        alt 쿠폰 사용 가능 상태인 경우
            CouponService -->> OrderController: 쿠폰 정보 응답
        end
        deactivate CouponService
    end
    OrderController ->> OrderService: 주문 생성 요청 (사용자 ID, 쿠폰 정보, 배송지 정보, 주문 상품 목록)
    alt 주문 생성 실패
        OrderService -->> OrderController: 주문 처리 실패 예외 (500 Internal Server Error)
    end
    OrderService ->> OrderController: 주문 생성 응답
    OrderController ->> PaymentService: 결제 요청 (주문 ID, 금액)
    activate PaymentService
    PaymentService ->> PaymentGateway: 결제 처리 요청
    activate PaymentGateway
    alt 결제 실패
        PaymentGateway -->> PaymentService: 결제 실패 응답
        PaymentService -->> OrderController: 결제 실패 응답 (오류 메시지 등)
        activate OrderController
        OrderController ->> OrderService: 주문 실패 처리 요청
        deactivate OrderController
        alt 주문 실패 처리 실패
            OrderService -->> OrderController: 주문 실패 처리 실패 응답 (500 Internal Server Error)\
        end
        alt 주문 실패 처리 성공
            OrderService -->> OrderController: 주문 실패 처리 성공 응답 (200 OK)
            deactivate OrderService
        end
    end
    alt 결제 성공
        PaymentGateway -->> PaymentService: 결제 승인 응답
        deactivate PaymentGateway
        PaymentService -->> OrderController: 결제 승인 응답 (결제 상태 등)
        deactivate PaymentService
        activate OrderController
        OrderController ->> ProductService: 재고 차감 요청
        deactivate OrderController
        activate ProductService
        alt 재고 차감 실패
            ProductService -->> OrderController: 재고 차감 실패 예외 (500 Internal Server Error)
        end
        alt 재고 차감 성공
            ProductService -->> OrderController: 주문 및 결제 성공 응답 (200 OK)
        end
    end
    deactivate ProductService
    deactivate OrderController
```
