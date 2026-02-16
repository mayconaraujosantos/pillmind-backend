-- Migration V8: Estrutura final limpa e unificada
-- Esta migração substitui e limpa todas as migrations anteriores
-- Cria a estrutura final diretamente sem redundâncias

-- ============================================================================
-- LIMPEZA: Remover todas as tabelas antigas se existirem
-- ============================================================================

DROP TABLE IF EXISTS social_accounts;
DROP TABLE IF EXISTS oauth_accounts;
DROP TABLE IF EXISTS local_accounts;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS accounts;

-- ============================================================================
-- ESTRUTURA FINAL: Criar tabelas da arquitetura separada
-- ============================================================================

-- 1. Tabela de usuários (dados do perfil)
CREATE TABLE users (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    date_of_birth DATE,
    gender VARCHAR(20) CHECK (gender IN ('MALE', 'FEMALE', 'OTHER', 'PREFER_NOT_TO_SAY')),
    picture_url TEXT,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2. Tabela de autenticação local (email/senha)
CREATE TABLE local_accounts (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    last_login_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 3. Tabela de autenticação OAuth2
CREATE TABLE oauth_accounts (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    provider VARCHAR(50) NOT NULL, -- 'GOOGLE', 'FACEBOOK', 'GITHUB', etc.
    provider_user_id VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    provider_name VARCHAR(255), -- Nome no provedor
    profile_image_url TEXT,
    access_token TEXT,
    refresh_token TEXT,
    token_expiry TIMESTAMP,
    last_login_at TIMESTAMP,
    linked_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE (provider, provider_user_id)
);

-- ============================================================================
-- ÍNDICES PARA PERFORMANCE
-- ============================================================================

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_local_accounts_email ON local_accounts(email);
CREATE INDEX idx_local_accounts_user_id ON local_accounts(user_id);
CREATE INDEX idx_oauth_accounts_user_id ON oauth_accounts(user_id);
CREATE INDEX idx_oauth_accounts_provider_id ON oauth_accounts(provider, provider_user_id);

-- ============================================================================
-- COMENTÁRIOS SOBRE A ESTRUTURA
-- ============================================================================

-- Esta migração cria a estrutura final limpa:
-- 
-- 1. **users**: Dados do perfil (nome, email, data nascimento, gênero, foto)
-- 2. **local_accounts**: Credenciais locais (email/senha) 
-- 3. **oauth_accounts**: Credenciais OAuth2 (Google, Facebook, etc.)
--
-- Características:
-- - IDs usam VARCHAR(36) para suportar UUIDs completos
-- - Separação clara entre perfil e credenciais
-- - Suporte a múltiplos provedores OAuth2 por usuário
-- - Foreign Keys com CASCADE para integridade
-- - Índices otimizados para queries comuns
-- - Constraints para validação de dados
--
-- Esta migração substitui V1-V7 com uma estrutura limpa e final.