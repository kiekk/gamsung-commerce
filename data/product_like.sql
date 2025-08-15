SET @P_SHARDS = 100;
SET @P_R = 0;
SET @K = 1000;

SET @sql = '
INSERT INTO product_like (product_id, created_at, deleted_at, updated_at, user_id)
SELECT
  p.id AS product_id,
  base_ts AS created_at,
  NULL AS deleted_at,
  base_ts + INTERVAL FLOOR(RAND()*86400) SECOND AS updated_at,
  m.id AS user_id
FROM (
  SELECT
    id,
    TIMESTAMP(
      DATE_SUB(NOW(), INTERVAL FLOOR(RAND()*3650 + 1) DAY) +
      INTERVAL FLOOR(RAND()*86400) SECOND
    ) AS base_ts
  FROM product
  WHERE (id % ?) = ?
) AS p
JOIN LATERAL (
  SELECT id
  FROM member
  ORDER BY RAND()
  LIMIT ?
) AS m ON TRUE
LEFT JOIN product_like pl
  ON pl.product_id = p.id AND pl.user_id = m.id
WHERE pl.product_id IS NULL
';

PREPARE s1 FROM @sql;
EXECUTE s1 USING @P_SHARDS, @P_R, @K;
DEALLOCATE PREPARE s1;