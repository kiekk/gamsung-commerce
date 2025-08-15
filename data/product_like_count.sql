SET SESSION cte_max_recursion_depth = 1000000;

-- product_like_count 테이블에 더미 데이터 삽입
INSERT INTO product_like_count (product_like_count, created_at, deleted_at, product_id, updated_at)
WITH RECURSIVE cte (n) AS
                   (SELECT 1
                    UNION ALL
                    SELECT n + 1
                    FROM cte
                    WHERE n < 100000 -- 생성하고 싶은 더미 데이터의 개수
                   )
SELECT FLOOR(RAND() * (50000 - 1) + 1)                  AS product_like_count, -- product_like_count는 고정된 값 10000으로 설정
       TIMESTAMP(DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 3650 + 1) DAY) +
                 INTERVAL FLOOR(RAND() * 86400) SECOND) AS created_at,         -- 최근 10년 내의 임의의 날짜와 시간 생성
       NULL                                             AS deleted_at,         -- deleted_at은 NULL로 설정
       n                                                AS product_id,         -- product_id는 1부터 시작하여 증가
       TIMESTAMP(DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 3650 + 1) DAY) +
                 INTERVAL FLOOR(RAND() * 86400) SECOND) AS updated_at          -- 최근 10년 내의 임의의 날짜와 시간 생성
FROM cte;
