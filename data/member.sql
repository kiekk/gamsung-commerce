SET SESSION cte_max_recursion_depth = 10000;

-- member 테이블에 더미 데이터 삽입
INSERT INTO member (created_at, deleted_at, updated_at, birthday, email, name, username, gender)
WITH RECURSIVE cte (n) AS
                   (SELECT 1
                    UNION ALL
                    SELECT n + 1
                    FROM cte
                    WHERE n < 10000 -- 생성하고 싶은 더미 데이터의 개수
                   )
SELECT TIMESTAMP(DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 3650 + 1) DAY) +
                 INTERVAL FLOOR(RAND() * 86400) SECOND) AS created_at, -- 최근 10년 내의 임의의 날짜와 시간 생성
       NULL                                             AS deleted_at, -- deleted_at은 NULL로 설정
       TIMESTAMP(DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 3650 + 1) DAY) +
                 INTERVAL FLOOR(RAND() * 86400) SECOND) AS updated_at, -- 최근 10년 내의 임의의 날짜와 시간 생성
       '2025-08-11'                                     AS birthday,   -- 생일은 고정된 날짜로 설정
       CONCAT('shyoon', n, '@gmail.com')                AS email,      -- 이메일은 고정된 값으로 설정
       CONCAT('User', LPAD(n, 7, '0'))                  AS name,       -- 'User' 다음에 7자리 숫자로 구성된 이름 생성
       CONCAT('user', LPAD(n, 7, '0'))                  AS username,   -- 'user' 다음에 7자리 숫자로 구성된 사용자 이름 생성
       CASE
           WHEN MOD(n, 2) = 0 THEN 'M'
           WHEN MOD(n, 2) = 1 THEN 'F'
           END                                          AS gender      -- 성별
FROM cte;