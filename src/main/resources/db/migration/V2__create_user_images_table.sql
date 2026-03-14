CREATE TABLE IF NOT EXISTS user_images (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    image_id VARCHAR(255) NOT NULL UNIQUE,
    kind VARCHAR(20) NOT NULL CHECK (kind IN ('PROFILE', 'MEDICATION')),
    status VARCHAR(20) NOT NULL CHECK (status IN ('REQUESTED', 'CONFIRMED')),
    delivery_url TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_user_images_user_id ON user_images(user_id);
CREATE INDEX IF NOT EXISTS idx_user_images_kind ON user_images(kind);
