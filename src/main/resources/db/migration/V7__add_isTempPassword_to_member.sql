-- is_temp_password 컬럼 추가 (기본값 false)
ALTER TABLE member
    ADD COLUMN is_temp_password BOOLEAN NOT NULL DEFAULT FALSE;

-- 이미 존재하는 row 에도 false 적용
UPDATE member
SET is_temp_password = FALSE
WHERE is_temp_password IS NULL;