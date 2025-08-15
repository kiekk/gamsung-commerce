SET SESSION cte_max_recursion_depth = 200;

-- brand 테이블에 더미 데이터 삽입
INSERT INTO brand (created_at, deleted_at, updated_at, name, status)
WITH RECURSIVE cte (n) AS
                   (SELECT 1
                    UNION ALL
                    SELECT n + 1
                    FROM cte
                    WHERE n < 200 -- 생성하고 싶은 더미 데이터의 개수
                   )
SELECT TIMESTAMP(DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 3650 + 1) DAY) +
                 INTERVAL FLOOR(RAND() * 86400) SECOND) AS created_at, -- 최근 10년 내의 임의의 날짜와 시간 생성
       NULL                                             AS deleted_at, -- deleted_at은 NULL로 설정
       TIMESTAMP(DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 3650 + 1) DAY) +
                 INTERVAL FLOOR(RAND() * 86400) SECOND) AS updated_at, -- 최근 10년 내의 임의의 날짜와 시간 생성
       CONCAT('Brand', LPAD(n, 7, '0'))                 AS name,       -- 'Brand' 다음에 7자리 숫자로 구성된 이름 생성
       CASE
           WHEN MOD(n, 3) = 0 THEN 'ACTIVE'
           WHEN MOD(n, 3) = 1 THEN 'CLOSED'
           WHEN MOD(n, 3) = 2 THEN 'INACTIVE'
           END                                          AS status      -- 상태
FROM cte;
