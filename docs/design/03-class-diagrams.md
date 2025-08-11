## 클래스 다이어그램

## 브랜드-상품-상품 좋아요 클래스 다이어그램

```markdown
특징:
ProductLike 에서 사용자, 상품별로 좋아요를 관리하며, 좋아요 수는 ProductLikeCount 에서 관리합니다.
```

```mermaid
classDiagram
    class User {
        -Long id
        -String username
        -String name
        -Email email
        -Birthday birthday
        -GenderType Gender
    }
    class Brand {
        -Long id
        -String name
        -BrandStatus status
        +active() boolean
        +inactive() boolean
        +close() boolean
    }
    class Product {
        -Long id
        -String name
        -Brand brand
        -ProductStatus status
        -String description
        +isNotActive() boolean
    }
    class ProductLike {
        -Long id
        -Product product
        -User user
    }
    class ProductLikeCount {
        -Product product
        -Long productLikeCount
        +increaseProductLikeCount()
        +decreaseProductLikeCount()
    }
    Brand --> Product
    User --> ProductLike
    Product --> ProductLike
    Product --> ProductLikeCount 
```

## 상품-주문-결제 클래스 다이어그램 V1

```markdown
특징:
**Product 단위로 주문과 결제를 처리하는 구조**로, Product 에서 price, stock 등의 속성을 관리합니다.
Order 에서 사용자, 주문자 정보, 주문 상품 목록을 관리하며, 주문 상품 목록은 OrderItem 으로 관리합니다.
OrderItem 에서 상품명, 수량, 총 가격 등을 관리합니다.
추후 쿠폰/할인 개념 도입을 고려하여 Order, OrderItem 에서 totalPrice(총 가격), amount(결제 금액) 등의 속성을 추가했습니다.
Payment 에서 결제 방법, 상태, 결제 아이템 목록, 결제 내역을 관리합니다.
PaymentItem 에서 주문 상품과 결제 금액을 관리하며,
PaymentGatewayHistory 에서 결제 내역을 관리합니다. (ex: 결제/환불)
```

```mermaid
classDiagram
%% 상품
    class Product {
        -Long id
        -String name
        -Brand brand
        -ProductStatusType status
        -Stock stock
        -Price price
        -String description
        +isNotActive() boolean
    }
%% 재고
    class Stock {
        -Long productId
        -int quantity
        -version: Long
        +isQuantityLessThan(quantity: Int) boolean
        +deductQuantity(quantity: Int) void
    }
%% 사용자
    class User {
        -Long id
        -String username
        -String name
        -Email email
        -Birthday birthday
        -GenderType Gender
    }
%% 주문
    class Order {
        -Long id
        -User user
        -OrderCustomer orderCustomer
        -OrderStatusType orderStatus
        -Price totalPrice
        -Price amount
        -OrderItems orderItems
        +complete()
        +cancel()
        +addItems()
        +getTotalPrice()
        +getAmount()
    }
%% 주문 상품 목록
    class OrderItems {
        -List~OrderItem~ items
        +getTotalPrice() Price
        +getAmount() Price
        +size() Int
    }
%% 주문 상품
    class OrderItem {
        -Long productId
        -Order order
        -String productName
        -Price totalPrice
        -Price amount
    }
%% 결제
    class Payment {
        -Long id
        -Long orderId
        -PaymentMethodType method
        -PaymentStatusType status
        -PaymentItems paymentItems
        -Price totalAmount
        +complete() void
        +fail() void
        +cancel() void
        +addItems(paymentItem: List~PaymentItem~) void
    }
%% 결제 항목 목록
    class PaymentItems {
        -List~PaymentItem~ items
        +totalAmount() Price
        +complete() void
        +fail() void
        +isAllPending() boolean
        +isAllCompleted() boolean
        +isAllFailed() boolean
        +isAllCanceled() boolean
        +cancel() void
    }
%% 결제 항목
    class PaymentItem {
        -Long id
        -Payment payment
        -Long orderItemId
        -PaymentItemStatusType status
        -Price amount
        +complete() void
        +fail() void
        +cancel() void
        +isPending() boolean
        +isCompleted() boolean
        +isFailed() boolean
        +isCanceled() boolean
    }
%% 쿠폰
    class Coupon {
        -Long id
        -String name
        -CouponType type
        -Price discountAmount
        -PercentRate discountRate
        -CouponStatus status
        +issue(userId: Long) IssuedCoupon
        +issuable() boolean
        +calculateDiscountAmount(orderTotalPrice: Price) Price
    }
%% 사용자 쿠폰
    class IssuedCoupon {
        -Long id
        -User user
        -Coupon coupon
        -IssuedCouponStatus status
        -LocalDateTime issuedAt
        -LocalDateTime usedAt
        +isUsable() boolean
        +use() void
    }

    Product --> Stock
    Order --> OrderItems
    OrderItems --> OrderItem
    OrderItem --> Product
    Order --> User
    Order --> Payment
    Payment --> PaymentItems
    PaymentItems --> PaymentItem
    PaymentItem --> OrderItem
    Coupon --> IssuedCoupon
    IssuedCoupon --> User
    Order --> IssuedCoupon
```

```mermaid
classDiagram
%% 주문 상태 enum 정의
    class OrderStatusType {
        <<enumeration>>
        PENDING
        COMPLETED
        CANCELED
    }
%% 주문자 정보
    class OrderCustomer {
        <<valueobject>>
        -Order order
        -String name
        -Email email
        -Mobile mobile
        -Address address
    }

%% 결제 방식 enum 정의
    class PaymentMethodType {
        <<enumeration>>
        POINT
    }
%% 결제 상태 enum 정의
    class PaymentStatusType {
        <<enumeration>>
        PENDING
        COMPLETED
        FAILED
        CANCELED
    }
%% 결제 항목 상태 enum 정의
    class PaymentStatusItemType {
        <<enumeration>>
        PENDING
        COMPLETED
        FAILED
        CANCELED
    }
%% 가격
    class Price {
        <<valueobject>>
        -value: Long,
    }
%% 이메일
    class Email {
        <<valueobject>>
        -String value
    }
%% 전화번호
    class Mobile {
        <<valueobject>>
        -String value
    }
%% 주소
    class Address {
        <<valueobject>>
        -String zipCode
        -String address
        -String addressDetail
    }
%% 생년월일
    class Birthday {
        <<valueobject>>
        -String value
    }
%% 수량
    class Quantity {
        <<valueobject>>
        -int value
    }
%% 쿠폰 상태
    class CouponStatus {
        <<enumeration>>
        ACTIVE
        INACTIVE
    }
%% 쿠폰 타입
    class CouponType {
        <<enumeration>>
        AMOUNT
        PERCENTAGE
    }
%% 쿠폰 발급 상태
    class IssuedCouponStatus {
        <<enumeration>>
        ACTIVE
        USED
    }
%% 퍼센트
    class PercentRate {
        <<valueobject>>
        -Double value
    }
```

## 상품-주문-결제 클래스 다이어그램 V2

```markdown
특징:
**ProductItem 단위로 주문과 결제를 처리하는 구조**로, ProductItem 에서 price, stock 등의 속성을 관리합니다.
=> ProductItem 이 SKU(Stock Keeping Unit)이며, SKU 의 조합을 관리하는 ProductItemAttribute 를 통해 해당 상품의 속성 조합을 관리합니다.
=> ProductItemAttribute 는 ProductItem 의 속성 조합을 관리합니다. (ex: 빨강 + S 조합)
ProductAttribute 는 상품 속성 타입별 속성 값들을 관리합니다. (속성: 색상, 속성값: 빨강, 파랑 등)
ProductAttributeType 은 상품 속성 타입을 관리합니다. (ex: 색상, 사이즈 등)
ProductAttributeValue 는 상품 속성 값 타입을 관리합니다. (ex: 빨강, 파랑, S, M, L 등)
```

```mermaid
classDiagram
%% 회원
    class User {
        -Long id
        -String userId
        -String name
        -String email
        -String birthday
        -GenderType Gender
    }
%% 상품
    class Product {
        -Long id
        -String name
        -String description
        -ProductStatus status
        -List~ProductAttribute~ attributes
        -List~ProductItem~ items
    }
%% 상품 속성 타입 (색상, 사이즈...)
    class ProductAttributeType {
        -Long id
        -String name
    }
%% 상품 속성 값 타입 (빨강, 파랑, S, M, L...)
    class ProductAttributeValue {
        -Long id
        -ProductAttributeType type
        -String name
        -int ordering
    }
%% 상품 속성 그룹
    class ProductAttribute {
        -Long id
        -Product product
        -ProductAttributeType type
        -List~ProductAttributeValue~ values
    }
%% 상품 아이템 - SKU
    class ProductItem {
        -Long id
        -String sku
        -Price price
        -Stock stock
        -ProductItemStatus status
        -Product product
        -List~ProductItemAttribute~ attributes
    }
%% 상품 아이템 속성 (SKU 조합)
    class ProductItemAttribute {
        -Long id
        -ProductItem item
        -ProductAttributeValue value
    }
%% 주문
    class Order {
        -Long id
        -User user
        -PurchaseMethod purchaseMethod
        -OrderCustomer orderCustomer
        -OrderStatus orderStatus
        -Price totalPrice
        -Price amount
        -List~OrderItem~ orderItems
        +getOrderTotalPrice()
        +getOrderAmount()
    }
%% 주문자 정보
    class OrderCustomer {
        -Long id
        -Order order
        -String name
        -Email email
        -Mobile mobile
        -String zipCode
        -String address
        -String addressDetail
    }
%% 주문 상품
    class OrderItem {
        -Long id
        -ProductItem productItem
        -String productName
        -int quantity
        -Price totalPrice
        -Price amount
        +getOrderTotalPrice()
        +getOrderTotalAmount()
    }
%% 결제
    class Payment {
        -Long id
        -Order order
        -PaymentMethod method
        -PaymentStatus status
        -List~PaymentItem~ paymentItems
        -List~PaymentGatewayHistory~ histories
        -Price totalAmount
    }
%% 결제 아이템
    class PaymentItem {
        -Long id
        -Payment payment
        -OrderItem orderItem
        -PaymentStatus status
        -Price amount
    }
%% 쿠폰
    class Coupon {
        -Long id
        -String name
        -CouponType type
        -Price discountAmount
        -PercentRate discountPercentage
        -CouponStatus status
        +issue(userId: Long) IssuedCoupon
        +issuable() boolean
        +calculateDiscountAmount(orderTotalPrice: Price) Price
    }
%% 사용자 쿠폰
    class IssuedCoupon {
        -Long id
        -User user
        -Coupon coupon
        -IssuedCouponStatus status
        -LocalDateTime issuedAt
        +isUsable() boolean
        +use() void
    }

    Product --> ProductAttribute
    ProductAttribute --> ProductAttributeType
    ProductAttribute --> ProductAttributeValue
    ProductAttributeValue --> ProductAttributeType
    Product --> ProductItem
    ProductItem --> ProductItemAttribute
    ProductItemAttribute --> ProductAttributeValue
    Order --> OrderItem
    OrderItem --> ProductItem
    Order --> User
    Order --> OrderCustomer
    Order --> Payment
    Payment --> PaymentItem
    PaymentItem --> OrderItem
    Coupon --> IssuedCoupon
    IssuedCoupon --> User
    Order --> IssuedCoupon
```
