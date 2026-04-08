-- Script para criar dados de teste: Medicamento Linandib 5mg com usuário
-- Este script inserirá dados diretamente no banco PostgreSQL

-- ============================================================================
-- 1. CRIAR USUÁRIO TESTE
-- ============================================================================

-- UUID para o usuário (pode gerar um novo se necessário)
-- Usando um UUID fixo para facilitar testes
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
    '550e8400-e29b-41d4-a716-446655440000'::text,
    'Usuário Teste',
    'teste@pillmind.com',
    '1990-05-15'::date,
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
    '550e8400-e29b-41d4-a716-446655440001'::text,
    '550e8400-e29b-41d4-a716-446655440000'::text,
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
    '550e8400-e29b-41d4-a716-446655440100'::text,
    '550e8400-e29b-41d4-a716-446655440000'::text,
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
    '550e8400-e29b-41d4-a716-446655440200'::text,
    '550e8400-e29b-41d4-a716-446655440000'::text,
    '550e8400-e29b-41d4-a716-446655440100'::text,
    '08:00',
    'SEGUNDA,TERÇA,QUARTA,QUINTA,SEXTA,SÁBADO,DOMINGO',
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
) ON CONFLICT DO NOTHING;

-- ============================================================================
-- 5. CRIAR DOSES PARA TODAY
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
) VALUES (
    '550e8400-e29b-41d4-a716-446655440300'::text,
    '550e8400-e29b-41d4-a716-446655440000'::text,
    '550e8400-e29b-41d4-a716-446655440100'::text,
    CURRENT_DATE,
    '08:00',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
) ON CONFLICT DO NOTHING;

-- ============================================================================
-- VERIFICAR DADOS INSERIDOS
-- ============================================================================

SELECT '=== USUÁRIO ===' as "Seção";
SELECT id, name, email, email_verified, created_at 
FROM users 
WHERE email = 'teste@pillmind.com';

SELECT '' as "";
SELECT '=== MEDICAMENTO ===' as "Seção";
SELECT id, name, dosage, frequency, times, start_date, end_date, notes, medicine_type 
FROM medicines 
WHERE name = 'Linandib';

SELECT '' as "";
SELECT '=== REMINDER ===' as "Seção";
SELECT id, times, days_of_week, active 
FROM reminders 
WHERE medicine_id IN (SELECT id FROM medicines WHERE name = 'Linandib');

SELECT '' as "";
SELECT '=== DOSES DE HOJE ===' as "Seção";
SELECT id, date, scheduled_time, skipped 
FROM medicine_doses 
WHERE medicine_id IN (SELECT id FROM medicines WHERE name = 'Linandib')
AND date = CURRENT_DATE;

SELECT '' as "";
SELECT '=== RESUMO ===' as "";
SELECT 
    'Login email' as "Item",
    'teste@pillmind.com' as "Valor"
UNION ALL
SELECT 'Senha', 'Teste@123456'
UNION ALL
SELECT 'Medicamento', 'Linandib 5mg'
UNION ALL
SELECT 'Frequência', '1x ao dia à 08:00'
UNION ALL
SELECT 'Tipo', 'Comprimido'
UNION ALL
SELECT 'Uso', 'Contínuo (sem data final)';
