SET SESSION cte_max_recursion_depth = 100000;

-- product 테이블에 더미 데이터 삽입
INSERT INTO product (brand_id, created_at, deleted_at, price, updated_at, description, name, status)
WITH RECURSIVE cte (n) AS
                   (SELECT 1
                    UNION ALL
                    SELECT n + 1
                    FROM cte
                    WHERE n < 100000 -- 생성하고 싶은 더미 데이터의 개수
                   )
SELECT FLOOR((n - 1) / 5000) + 1                      AS brand_id,    -- 5천건당 brandId가 1씩 증가
       TIMESTAMP(DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 3650 + 1) DAY) +
                 INTERVAL FLOOR(RAND() * 86400) SECOND) AS created_at,  -- 최근 10년 내의 임의의 날짜와 시간 생성
       NULL                                             AS deleted_at,  -- deleted_at은 NULL로 설정
       FLOOR(RAND() * (500000 - 1000 + 1) + 1000)       AS price,       -- 가격은 1000 ~ 500000 사이의 임의의 값 생성
       TIMESTAMP(DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 3650 + 1) DAY) +
                 INTERVAL FLOOR(RAND() * 86400) SECOND) AS updated_at,  -- 최근 10년 내의 임의의 날짜와 시간 생성
       ''                                               AS description, -- description은 빈 문자열로 설정
       CONCAT('Product', LPAD(n, 7, '0'))               AS name,        -- 'Product' 다음에 7자리 숫자로 구성된 이름 생성
       # 상태는 3으로 나누기 했을 때 0, 1, 2일 경우 0 => ACTIVE, 1 => INACTIVE, 2 => DELETED
       CASE
           WHEN MOD(n, 3) = 0 THEN 'ACTIVE'
           WHEN MOD(n, 3) = 1 THEN 'INACTIVE'
           WHEN MOD(n, 3) = 2 THEN 'DELETED'
           END                                          AS status
FROM cte;
