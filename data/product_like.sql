-- product_like 테이블에 더미 데이터 삽입
SET @P_SHARDS = 100;
SET @P_R = 0;

INSERT INTO product_like (product_id, created_at, deleted_at, updated_at, user_id)
SELECT p.id                                             AS product_id,
       TIMESTAMP(DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 3650 + 1) DAY) +
                 INTERVAL FLOOR(RAND() * 86400) SECOND) AS created_at,
       NULL                                             AS deleted_at,
       TIMESTAMP(DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 3650 + 1) DAY) +
                 INTERVAL FLOOR(RAND() * 86400) SECOND) AS updated_at,
       m.id                                             AS user_id
FROM product p
         STRAIGHT_JOIN member m
WHERE (p.id MOD @P_SHARDS) = @P_R;

# truncate table product_like;
