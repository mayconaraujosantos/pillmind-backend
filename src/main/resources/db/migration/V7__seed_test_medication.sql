-- V7: Seed dados de teste - Medicamento Linandib 5mg com usuário de teste
-- Esta migration insere dados de teste para validar o fluxo de cadastro de medicamentos

-- ============================================================================
-- 1. CRIAR USUÁRIO TESTE
-- ============================================================================

INSERT INTO users (
    id,
    name,
    email,
    date_of_birth,
    gender,
    email_verified,
    created_at,
    updated_at
) VALUES (
    '550e8400-e29b-41d4-a716-446655440000',
    'Usuário Teste',
    'teste@pillmind.com',
    '1990-05-15',
    'MALE',
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
) ON CONFLICT DO NOTHING;

-- ============================================================================
-- 2. CRIAR CONTA LOCAL (EMAIL/SENHA)
-- ============================================================================

-- Senha: Teste@123456
-- Hash bcrypt: $2a$10$O9uj9FPgOoFrxMWnxyR8K.0M9F.FqYBpX9UpCvtpgPvNy.0aPx7Ye
INSERT INTO local_accounts (
    id,
    user_id,
    email,
    password_hash,
    last_login_at,
    created_at,
    updated_at
) VALUES (
    '550e8400-e29b-41d4-a716-446655440001',
    '550e8400-e29b-41d4-a716-446655440000',
    'teste@pillmind.com',
    '$2a$10$O9uj9FPgOoFrxMWnxyR8K.0M9F.FqYBpX9UpCvtpgPvNy.0aPx7Ye',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
) ON CONFLICT DO NOTHING;

-- ============================================================================
-- 3. CRIAR MEDICAMENTO: Linandib 5mg
-- ============================================================================

INSERT INTO medicines (
    id,
    user_id,
    name,
    dosage,
    frequency,
    times,
    start_date,
    end_date,
    notes,
    image_url,
    medicine_type,
    prescribed_for,
    quantity,
    reminder_on_empty,
    created_at,
    updated_at
) VALUES (
    '550e8400-e29b-41d4-a716-446655440100',
    '550e8400-e29b-41d4-a716-446655440000',
    'Linandib',
    '5mg',
    '1x ao dia',
    '08:00',
    CURRENT_DATE,
    NULL,
    'Tomar 1 comprimido pela manhã - USO CONTINUO',
    NULL,
    'Comprimido',
    'Controle',
    1,
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
) ON CONFLICT DO NOTHING;

-- ============================================================================
-- 4. CRIAR REMINDER PARA O MEDICAMENTO
-- ============================================================================

INSERT INTO reminders (
    id,
    user_id,
    medicine_id,
    times,
    days_of_week,
    active,
    created_at,
    updated_at
) VALUES (
    '550e8400-e29b-41d4-a716-446655440200',
    '550e8400-e29b-41d4-a716-446655440000',
    '550e8400-e29b-41d4-a716-446655440100',
    '08:00',
    'SEGUNDA,TERÇA,QUARTA,QUINTA,SEXTA,SÁBADO,DOMINGO',
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
) ON CONFLICT DO NOTHING;

-- ============================================================================
-- 5. CRIAR DOSES PARA TODAY (próximos 7 dias)
-- ============================================================================

INSERT INTO medicine_doses (
    id,
    user_id,
    medicine_id,
    date,
    scheduled_time,
    taken_at,
    skipped,
    created_at,
    updated_at
) VALUES
    ('550e8400-e29b-41d4-a716-446655440300', '550e8400-e29b-41d4-a716-446655440000', '550e8400-e29b-41d4-a716-446655440100', CURRENT_DATE, '08:00', NULL, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('550e8400-e29b-41d4-a716-446655440301', '550e8400-e29b-41d4-a716-446655440000', '550e8400-e29b-41d4-a716-446655440100', CURRENT_DATE + INTERVAL '1 day', '08:00', NULL, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('550e8400-e29b-41d4-a716-446655440302', '550e8400-e29b-41d4-a716-446655440000', '550e8400-e29b-41d4-a716-446655440100', CURRENT_DATE + INTERVAL '2 days', '08:00', NULL, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('550e8400-e29b-41d4-a716-446655440303', '550e8400-e29b-41d4-a716-446655440000', '550e8400-e29b-41d4-a716-446655440100', CURRENT_DATE + INTERVAL '3 days', '08:00', NULL, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('550e8400-e29b-41d4-a716-446655440304', '550e8400-e29b-41d4-a716-446655440000', '550e8400-e29b-41d4-a716-446655440100', CURRENT_DATE + INTERVAL '4 days', '08:00', NULL, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('550e8400-e29b-41d4-a716-446655440305', '550e8400-e29b-41d4-a716-446655440000', '550e8400-e29b-41d4-a716-446655440100', CURRENT_DATE + INTERVAL '5 days', '08:00', NULL, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('550e8400-e29b-41d4-a716-446655440306', '550e8400-e29b-41d4-a716-446655440000', '550e8400-e29b-41d4-a716-446655440100', CURRENT_DATE + INTERVAL '6 days', '08:00', NULL, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;
