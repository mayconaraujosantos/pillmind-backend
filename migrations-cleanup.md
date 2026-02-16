# 🗄️ Migrations - Limpeza e Unificação

## ✅ **Problema Resolvido**

As migrations anteriores (V1-V7) tinham problemas de:
- **Redundâncias**: Tabela `social_accounts` duplicada com `oauth_accounts`
- **Correções repetidas**: Tamanho de ID corrigido em V2 e V7
- **Estrutura confusa**: Criação progressiva da `accounts` → depois reestruturação completa

## 📋 **Solução Implementada**

### **Nova Estrutura:**
- **V1__unified_clean_structure.sql**: Migração única e limpa
- **V[2-7]__*.sql.bak**: Backup das migrations antigas

### **Tabelas Finais:**
```sql
users (
    id VARCHAR(36) PRIMARY KEY,     -- UUID completo
    name, email, date_of_birth,     -- Dados do perfil
    gender, picture_url,            -- Campos adicionais
    email_verified, timestamps      -- Status e auditoria
)

local_accounts (
    id VARCHAR(36) PRIMARY KEY,     -- ID da conta local
    user_id VARCHAR(36) REFERENCES users(id),
    email, password_hash,           -- Credenciais locais
    last_login_at, timestamps
)

oauth_accounts (
    id VARCHAR(36) PRIMARY KEY,     -- ID da conta OAuth
    user_id VARCHAR(36) REFERENCES users(id),
    provider, provider_user_id,     -- Dados do provedor
    email, provider_name,           -- Info do perfil OAuth
    profile_image_url,              -- Avatar do provedor
    access_token, refresh_token,    -- Tokens OAuth
    token_expiry, last_login_at,    -- Gestão de tokens
    linked_at, is_primary,          -- Controle de contas
    timestamps
)
```

## 🎯 **Benefícios**

1. **Estrutura Limpa**: Uma única migração create todas as tabelas
2. **Sem Redundância**: Removidas tabelas e correções duplicadas
3. **UUIDs Corretos**: VARCHAR(36) desde o início
4. **Arquitetura Separada**: Perfil vs. Credenciais bem definidos
5. **Backup Seguro**: Migrations antigas preservadas como .bak

## ✅ **Validação**

- ✅ Testes de integração passando
- ✅ Estrutura final funcionando
- ✅ Migrations limpas
- ✅ Backup das versões antigas

## 📝 **Próximos Passos**

Com as migrations limpas, agora podemos fazer os conventional commits da refatoração completa.