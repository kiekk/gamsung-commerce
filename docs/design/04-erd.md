## 전체 테이블 구조 및 관계 정리

```mermaid
erDiagram
    BRAND ||--o{ PRODUCT : "1:N"
    MEMBER ||--|| ORDER : "1:1"
    ORDER ||--o{ ORDER_ITEM : "1:N"
    PRODUCT ||--|| ORDER_ITEM : "1:1"
    PRODUCT ||--o{ PRODUCT_LIKE: "1:N"
    PRODUCT ||--|| PRODUCT_LIKE_COUNT: "1:1"
    ORDER ||--|| PAYMENT : "1:1"
    PAYMENT ||--o{ PAYMENT_ITEM : "1:N"
    PAYMENT ||--o{ PAYMENT_GATEWAY_HISTORY : "1:N"
    ORDER_ITEM ||--|| PAYMENT_ITEM : "1:1"

    BRAND {
        BIGINT id PK
        VARCHAR name
        VARCHAR status
        DATETIME created_at
        DATETIME updated_at
        DATETIME deleted_at
    }
    
    MEMBER {
        BIGINT id PK
        VARCHAR user_id UK
        VARCHAR name
        VARCHAR email UK
        VARCHAR birthday
        VARCHAR gender
        DATETIME created_at
        DATETIME updated_at
        DATETIME deleted_at
    }

    PRODUCT {
        BIGINT id PK
        BIGINT brand_id FK
        VARCHAR name
        VARCHAR status
        INT stock
        INT price
        VARCHAR description
        DATETIME created_at
        DATETIME updated_at
        DATETIME deleted_at
    }

    ORDER {
        BIGINT id PK
        BIGINT member_id FK
        VARCHAR purchase_method
        VARCHAR status
        VARCHAR name
        VARCHAR email
        VARCHAR mobile
        VARCHAR zip_code
        VARCHAR address
        VARCHAR address_detail
        INT total_price
        INT amount
        DATETIME created_at
        DATETIME updated_at
        DATETIME deleted_at
    }

    ORDER_ITEM {
        BIGINT id PK
        BIGINT order_id FK
        BIGINT product_id FK
        VARCHAR product_name
        INT total_price
        INT amount
        DATETIME created_at
        DATETIME updated_at
        DATETIME deleted_at
    }

    PAYMENT {
        BIGINT id PK
        BIGINT order_id FK
        VARCHAR status
        INT total_amount
        DATETIME created_at
        DATETIME updated_at
        DATETIME deleted_at
    }

    PAYMENT_ITEM {
        BIGINT id PK
        BIGINT payment_id FK
        BIGINT order_item_id FK
        VARCHAR status
        INT amount
        DATETIME created_at
        DATETIME updated_at
        DATETIME deleted_at
    }

    PRODUCT_LIKE_COUNT {
        BIGINT product_id PK
        BIGINT product_like_count
        DATETIME created_at
        DATETIME updated_at
        DATETIME deleted_at
    }

    PRODUCT_LIKE {
        BIGINT id PK
        BIGINT member_id UK
        BIGINT product_id UK
        DATETIME created_at
        DATETIME updated_at
        DATETIME deleted_at
    }

    PAYMENT_GATEWAY_HISTORY {
        BIGINT id PK
        BIGINT payment_id FK
        VARCHAR type
        TEXT gateway_response
        DATETIME created_at
        DATETIME updated_at
        DATETIME deleted_at
    }
```
