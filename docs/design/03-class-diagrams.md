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
        -String userId
        -String name
        -String email
        -String birthday
        -GenderType Gender
    }
    class Brand {
        -Long id
        -String name
        -BrandStatus status
    }
    class Product {
        -Long id
        -String name
        -Brand brand
        -ProductStatus status
        -String description
        -ProductLikeCount productLikeCount
        +getProductLikeCount()
    }
    class ProductLike {
        -Long id
        -Product product
        -User user
        +create(product, user)
        +delete(product, user)
    }
    class ProductLikeCount {
        -Product product
        -Long productLikeCount
        +increase()
        +decrease()
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
        -ProductStatus status
        -Stock stock
        -Price price
        -String description
        +availableOrder(quantity)
    }
%% 사용자
    class User {
        -Long id
        -String userId
        -String name
        -String email
        -String birthday
        -GenderType Gender
    }
%% 주문
    class Order {
        -Long id
        -User user
        -OrderCustomer orderCustomer
        -OrderStatus orderStatus
        -Price totalPrice
        -Price amount
        -List~OrderItem~ orderItems
        +getOrderTotalPrice()
        +getOrderAmount()
    }
%% 주문 상태 enum 정의
    class OrderStatus {
        <<enumeration>>
        ORDER
        PURCHASE
        CANCEL
    }
%% 주문자 정보
    class OrderCustomer {
        -Order order
        -String name
        -Email email
        -Mobile mobile
        -Address address
    }
%% 주문 상품
    class OrderItem {
        -Product product
        -String productName
        -int quantity
        -Price totalPrice
        -Price amount
        +getProductTotalPrice()
        +getProductTotalAmount()
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
%% 결제 방식 enum 정의
    class PaymentMethod {
        <<enumeration>>
        POINT
    }
%% 결제 상태 enum 정의
    class PaymentStatus {
        <<enumeration>>
        PURCHASE
        CANCEL
    }
%% 결제 아이템
    class PaymentItem {
        -Long id
        -Payment payment
        -OrderItem orderItem
        -PaymentStatus status
        -Price amount
    }
%% 결제 히스토리
    class PaymentGatewayHistory {
        -Long id
        -Payment payment
        -PaymentGatewayType type
        -String transactionId
        -String gatewayResponse
    }

    Order --> OrderItem
    OrderItem --> Product
    Order --> User
    Order --> OrderCustomer
    Order --> Payment
    Payment --> PaymentItem
    PaymentItem --> OrderItem
    Payment --> PaymentGatewayHistory
    Payment --> PaymentMethod
    Payment --> PaymentStatus
    Order --> OrderStatus
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
        -Integer ordering
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
        -String email
        -String phoneNumber
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
        +getProductTotalPrice()
        +getProductTotalAmount()
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
%% 결제 히스토리
    class PaymentGatewayHistory {
        -Long id
        -Payment payment
        -PaymentGatewayType type
        -String transactionId
        -String gatewayResponse
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
    Payment --> PaymentGatewayHistory
```
