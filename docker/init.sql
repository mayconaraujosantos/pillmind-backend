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
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Cria índice para melhor performance em buscas por email
CREATE INDEX IF NOT EXISTS idx_accounts_email ON accounts(email);

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
