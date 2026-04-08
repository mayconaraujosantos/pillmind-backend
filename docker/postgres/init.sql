-- Script de inicialização do banco de dados
-- Este script é executado automaticamente quando o container é criado pela primeira vez

-- Cria o banco de dados se não existir
SELECT 'CREATE DATABASE pillmind'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'pillmind')\gexec

-- Conecta ao banco de dados
\c pillmind

-- Cria a tabela de contas
CREATE TABLE IF NOT EXISTS accounts (
    id VARCHAR(26) NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255),
    google_account BOOLEAN NOT NULL DEFAULT FALSE,
    google_id VARCHAR(255) UNIQUE,
    picture_url TEXT,
    last_login_at TIMESTAMP,
    auth_provider VARCHAR(20) DEFAULT 'LOCAL',
    email_verified BOOLEAN DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Cria índice para melhor performance em buscas por email
CREATE INDEX IF NOT EXISTS idx_accounts_email ON accounts(email);
CREATE INDEX IF NOT EXISTS idx_accounts_auth_provider ON accounts(auth_provider);

-- Cria a tabela de contas sociais
CREATE TABLE IF NOT EXISTS social_accounts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id VARCHAR(26) NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
    provider VARCHAR(20) NOT NULL,
    provider_user_id VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    name VARCHAR(255),
    profile_image_url TEXT,
    access_token TEXT,
    refresh_token TEXT,
    token_expiry TIMESTAMP,
    linked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_primary BOOLEAN DEFAULT false,
    
    -- Garantir que um usuário não tenha múltiplas contas com o mesmo provedor
    CONSTRAINT uq_user_provider UNIQUE(user_id, provider),
    
    -- Garantir que um provider_user_id seja único por provedor
    CONSTRAINT uq_provider_user_id UNIQUE(provider, provider_user_id)
);

-- Índices para performance na tabela social_accounts
CREATE INDEX IF NOT EXISTS idx_social_accounts_user_id ON social_accounts(user_id);
CREATE INDEX IF NOT EXISTS idx_social_accounts_provider_user ON social_accounts(provider, provider_user_id);
CREATE INDEX IF NOT EXISTS idx_social_accounts_provider ON social_accounts(provider);
CREATE INDEX IF NOT EXISTS idx_social_accounts_primary ON social_accounts(is_primary) WHERE is_primary = true;

-- Cria função para atualizar updated_at automaticamente
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Cria trigger para atualizar updated_at
DROP TRIGGER IF EXISTS update_accounts_updated_at ON accounts;
CREATE TRIGGER update_accounts_updated_at
    BEFORE UPDATE ON accounts
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();
