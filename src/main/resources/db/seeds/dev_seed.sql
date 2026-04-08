-- ============================================================================
-- DEV SEED — dados para teste local e Swagger
-- Senha de todos os usuários: pillmind@123
--
-- ATENÇÃO: Execute apenas em ambiente de desenvolvimento!
-- Use: make seed
-- ============================================================================

-- Limpa dados de seed anteriores (ordem: filhos → pais)
DELETE FROM medicine_doses  WHERE user_id IN ('seed-user-001', 'seed-user-002');
DELETE FROM medicines       WHERE user_id IN ('seed-user-001', 'seed-user-002');
DELETE FROM local_accounts  WHERE user_id IN ('seed-user-001', 'seed-user-002');
DELETE FROM users           WHERE id IN ('seed-user-001', 'seed-user-002');

-- ============================================================================
-- USUÁRIOS
-- ============================================================================

INSERT INTO users (id, name, email, date_of_birth, gender, email_verified, created_at, updated_at)
VALUES
    ('seed-user-001', 'Alice Seed',   'alice@pillmind.dev',  '1990-03-15', 'FEMALE', TRUE, NOW(), NOW()),
    ('seed-user-002', 'Bob Seed',     'bob@pillmind.dev',    '1985-07-22', 'MALE',   TRUE, NOW(), NOW());

-- ============================================================================
-- CONTAS LOCAIS (senha: pillmind@123)
-- ============================================================================

INSERT INTO local_accounts (id, user_id, email, password_hash, created_at, updated_at)
VALUES
    ('seed-la-001', 'seed-user-001', 'alice@pillmind.dev', '$2a$10$PAgfVoSW/923NPDP2oNGwOeXs4jRDPvMqvN16mQ.87w0huyvORRkO', NOW(), NOW()),
    ('seed-la-002', 'seed-user-002', 'bob@pillmind.dev',   '$2a$10$PAgfVoSW/923NPDP2oNGwOeXs4jRDPvMqvN16mQ.87w0huyvORRkO', NOW(), NOW());

-- ============================================================================
-- MEDICAMENTOS (Alice — seed-user-001)
-- ============================================================================

INSERT INTO medicines (id, user_id, name, dosage, frequency, times, start_date, end_date,
                       notes, image_url, medicine_type, prescribed_for, quantity, reminder_on_empty, created_at, updated_at)
VALUES
    (
        'seed-med-001', 'seed-user-001',
        'Paracetamol', '500mg', 'twice-a-day',
        '["08:00","20:00"]',
        '2026-04-01', '2026-04-30',
        'Tomar após as refeições', NULL,
        'tablet', 'dor de cabeça', 20, TRUE,
        NOW(), NOW()
    ),
    (
        'seed-med-002', 'seed-user-001',
        'Amoxicilina', '875mg', 'three-times-a-day',
        '["08:00","14:00","20:00"]',
        '2026-04-01', '2026-04-10',
        'Antibiótico — tomar até terminar', NULL,
        'capsule', 'infecção', 30, TRUE,
        NOW(), NOW()
    ),
    (
        'seed-med-003', 'seed-user-001',
        'Ômega 3', '1000mg', 'once-a-day',
        '["12:00"]',
        '2026-01-01', NULL,
        'Suplemento diário', NULL,
        'capsule', 'suplementação', 60, TRUE,
        NOW(), NOW()
    ),
    (
        'seed-med-004', 'seed-user-001',
        'Dipirona', '1g', 'as-needed',
        '["08:00","16:00"]',
        '2026-04-01', '2026-04-07',
        'Apenas se sentir febre ou dor', NULL,
        'tablet', 'febre', 10, FALSE,
        NOW(), NOW()
    );

-- ============================================================================
-- MEDICAMENTOS (Bob — seed-user-002)
-- ============================================================================

INSERT INTO medicines (id, user_id, name, dosage, frequency, times, start_date, end_date,
                       notes, image_url, medicine_type, prescribed_for, quantity, reminder_on_empty, created_at, updated_at)
VALUES
    (
        'seed-med-005', 'seed-user-002',
        'Losartana', '50mg', 'once-a-day',
        '["08:00"]',
        '2026-01-01', NULL,
        'Controle de pressão — uso contínuo', NULL,
        'tablet', 'hipertensão', 30, TRUE,
        NOW(), NOW()
    ),
    (
        'seed-med-006', 'seed-user-002',
        'Metformina', '850mg', 'twice-a-day',
        '["07:30","19:30"]',
        '2026-01-01', NULL,
        'Tomar durante a refeição', NULL,
        'tablet', 'diabetes tipo 2', 60, TRUE,
        NOW(), NOW()
    );

-- ============================================================================
-- DOSES DE HOJE (para aparecer no app e no GET /api/medicines/doses/today)
-- ============================================================================

INSERT INTO medicine_doses (id, user_id, medicine_id, date, scheduled_time, taken_at, skipped, created_at, updated_at)
VALUES
    -- Alice: Paracetamol 08:00 — tomado
    ('seed-dose-001', 'seed-user-001', 'seed-med-001', CURRENT_DATE, '08:00',
     NOW() - INTERVAL '2 hours', FALSE, NOW(), NOW()),

    -- Alice: Amoxicilina 08:00 — tomado
    ('seed-dose-002', 'seed-user-001', 'seed-med-002', CURRENT_DATE, '08:00',
     NOW() - INTERVAL '2 hours', FALSE, NOW(), NOW()),

    -- Alice: Amoxicilina 14:00 — pulada
    ('seed-dose-003', 'seed-user-001', 'seed-med-002', CURRENT_DATE, '14:00',
     NULL, TRUE, NOW(), NOW()),

    -- Bob: Losartana 08:00 — tomado
    ('seed-dose-004', 'seed-user-002', 'seed-med-005', CURRENT_DATE, '08:00',
     NOW() - INTERVAL '3 hours', FALSE, NOW(), NOW());
