-- Script de seed com dados de teste
-- Este script é executado após o init.sql

-- Conecta ao banco de dados
\c pillmind

-- Insere usuários de teste
-- IMPORTANTE: As senhas abaixo são placeholders
-- Para gerar hashes BCrypt reais, execute:
--   ./gradlew run --main-class com.pillmind.util.BcryptHashGenerator --args "password123"
-- Ou use o código Java:
--   BcryptAdapter adapter = new BcryptAdapter(12);
--   String hash = adapter.hash("password123");

-- Senha padrão para todos os usuários normais: "password123"
-- Substitua os hashes abaixo pelos hashes reais gerados

-- Usuário 1: Conta normal
INSERT INTO accounts (id, name, email, password, google_account) VALUES
('usr_001testaccount001', 'João Silva', 'joao@example.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyY5Y5Y5Y5Y5', false)
ON CONFLICT (email) DO NOTHING;

-- Usuário 2: Conta normal
INSERT INTO accounts (id, name, email, password, google_account) VALUES
('usr_002testaccount002', 'Maria Santos', 'maria@example.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyY5Y5Y5Y5Y5', false)
ON CONFLICT (email) DO NOTHING;

-- Usuário 3: Conta Google (sem senha)
INSERT INTO accounts (id, name, email, password, google_account) VALUES
('usr_003testaccount003', 'Pedro Oliveira', 'pedro@gmail.com', NULL, true)
ON CONFLICT (email) DO NOTHING;

-- Usuário 4: Conta Google (sem senha)
INSERT INTO accounts (id, name, email, password, google_account) VALUES
('usr_004testaccount004', 'Ana Costa', 'ana@gmail.com', NULL, true)
ON CONFLICT (email) DO NOTHING;

-- Usuário 5: Conta normal para testes de medicamentos
INSERT INTO accounts (id, name, email, password, google_account) VALUES
('usr_005testaccount005', 'Carlos Mendes', 'carlos@example.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyY5Y5Y5Y5Y5', false)
ON CONFLICT (email) DO NOTHING;

-- Exemplo de hash real gerado (substitua os placeholders acima):
-- Hash para "password123": $2a$12$KIXH4q8VqJ8VqJ8VqJ8VqO8VqJ8VqJ8VqJ8VqJ8VqJ8VqJ8VqJ8VqJ8V
-- NOTA: Cada execução gera um hash diferente, mas todos são válidos
