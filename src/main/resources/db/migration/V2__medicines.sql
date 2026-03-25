-- Medicamentos por usuário (CRUD)

CREATE TABLE medicines (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    name VARCHAR(255) NOT NULL,
    dosage VARCHAR(128) NOT NULL,
    frequency VARCHAR(128) NOT NULL,
    times_json TEXT NOT NULL DEFAULT '[]',
    start_date DATE NOT NULL,
    end_date DATE,
    notes TEXT,
    image_url TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_medicines_user_id ON medicines(user_id);
