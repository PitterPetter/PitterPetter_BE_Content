-- 초기 스키마 (diary)
CREATE TABLE IF NOT EXISTS diary (
    diary_id BIGSERIAL PRIMARY KEY,
    couple_id BIGINT NOT NULL,
    course_id BIGINT NULL,
    rating DOUBLE PRECISION NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

