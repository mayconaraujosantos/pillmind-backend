-- V4: Tabela de doses de medicamento (tomadas ou puladas)
CREATE TABLE medicine_doses (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    medicine_id VARCHAR(36) NOT NULL,
    date DATE NOT NULL,
    scheduled_time VARCHAR(5) NOT NULL,
    taken_at TIMESTAMP,
    skipped BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (medicine_id) REFERENCES medicines(id) ON DELETE CASCADE,
    UNIQUE (medicine_id, date, scheduled_time)
);

CREATE INDEX idx_medicine_doses_user_date ON medicine_doses(user_id, date);
CREATE INDEX idx_medicine_doses_medicine_id ON medicine_doses(medicine_id);
