# 📊 Estratégia de Salvamento OAuth2 - Análise Implementação

## ✅ Status Atual - O que já está implementado

### Fluxo Completo de Signup/Signin Google:

```
1. React Native
   ├─ Obtém idToken via Google SDK
   └─ Envia POST /api/auth/google com { idToken }

2. GoogleAuthController
   ├─ Recebe idToken
   ├─ Valida com GoogleTokenValidator
   └─ Extrai: email, name, googleId, picture

3. Lógica de Decisão
   ├─ Tenta criar conta via AddAccount
   │  ├─ Se email NÃO existe → Cria (SIGNUP)
   │  └─ Se email JÁ existe → Captura erro
   │
   └─ Se email existe, faz login via Authentication

4. Banco de Dados (PostgreSQL)
   └─ Salva na tabela `accounts`:
      ├─ id (UUID)
      ├─ name (do Google)
      ├─ email (verificado)
      ├─ password (NULL para Google)
      ├─ google_account (true)
      ├─ created_at
      └─ updated_at

5. Retorna para App
   └─ { accessToken, accountId, name, email }
```

### Implementações-chave:

#### 1. **DbAddAccount** ([src/main/java/com/pillmind/data/usecases/DbAddAccount.java](src/main/java/com/pillmind/data/usecases/DbAddAccount.java))

- ✅ Verifica se email já existe
- ✅ Se Google: password = null
- ✅ Se tradicional: faz hash da senha
- ✅ Gera UUID único
- ✅ Salva no banco

#### 2. **AccountPostgresRepository** ([src/main/java/com/pillmind/infra/db/postgres/AccountPostgresRepository.java](src/main/java/com/pillmind/infra/db/postgres/AccountPostgresRepository.java))

- ✅ INSERT com todos os campos corretos
- ✅ Suporta password NULL
- ✅ Suporta google_account flag
- ✅ Timestamps automáticos

#### 3. **GoogleAuthController** ([src/main/java/com/pillmind/presentation/controllers/GoogleAuthController.java](src/main/java/com/pillmind/presentation/controllers/GoogleAuthController.java))

- ✅ Trata 2 casos: signup (novo email) e signin (email existente)
- ✅ Retorna JWT próprio da aplicação
- ✅ Retorna dados do usuário

---

## 🎯 Comparação com Mercado

### **Como GIGANTES fazem (Google, Facebook, Auth0)**

| Aspecto                | Mercado                              | Seu Projeto              |
| ---------------------- | ------------------------------------ | ------------------------ |
| **Geração ID**         | UUIDs v4/v5                          | ✅ UUID v4 aleatório     |
| **Password nulo**      | Sim, OAuth só gera token             | ✅ Correto               |
| **Google ID**          | Armazenam em campo separado          | ❌ Não está armazenado   |
| **Foto do usuário**    | Salva URL do profile picture         | ❌ Não está salvo        |
| **Validação Email**    | Já verificado pelo Google            | ✅ Correto               |
| **Criação automática** | Signup implícito (primeiro login)    | ✅ Correto               |
| **Atualizar dados**    | Se usuário mudou nome/foto no Google | ❌ Não implementado      |
| **Refresh token**      | Válido por X dias                    | ❌ Não há (apenas JWT)   |
| **Audit log**          | Registra tentativas (sucesso/falha)  | ⚠️ Só tem logs via SLF4J |

---

## 🔧 Melhorias Recomendadas (Best Practices do Mercado)

### **1. Adicionar campos ao banco para OAuth2**

```sql
ALTER TABLE accounts ADD COLUMN IF NOT EXISTS google_id VARCHAR(255) UNIQUE;
ALTER TABLE accounts ADD COLUMN IF NOT EXISTS picture_url TEXT;
ALTER TABLE accounts ADD COLUMN IF NOT EXISTS last_login_at TIMESTAMP;
ALTER TABLE accounts ADD COLUMN IF NOT EXISTS auth_provider VARCHAR(50) DEFAULT 'email';
```

**Por quê?**

- Google ID é identificador único no Google, diferente do seu UUID
- Facilita buscar conta por google_id (mais rápido se OAuth voltar com google_id)
- Picture URL pode ser usada no perfil do app
- Last login útil para analytics

### **2. Atualizar conta se Google mudou dados**

```java
// Após validar token do Google
var existingAccount = loadAccountByEmailRepository.loadByEmail(googleUserInfo.email());

if (existingAccount.isPresent()) {
    // Se nome ou foto mudou, atualiza
    var account = existingAccount.get();
    if (!account.name().equals(googleUserInfo.name()) ||
        !Objects.equals(account.pictureUrl(), googleUserInfo.pictureUrl())) {
        // Atualizar conta com novos dados
        updateAccountRepository.update(account.withUpdatedProfile(
            googleUserInfo.name(),
            googleUserInfo.pictureUrl()
        ));
    }
}
```

### **3. Adicionar Refresh Token (JWT com validade longa)**

```java
// Em vez de retornar só accessToken:
{
  "accessToken": "eyJhbGc...", // Expira em 1h
  "refreshToken": "eyJhbGc...", // Expira em 30 dias
  "expiresIn": 3600,
  "accountId": "uuid",
  "name": "John",
  "email": "john@gmail.com"
}
```

### **4. Implementar /api/auth/refresh**

```java
POST /api/auth/refresh
Body: { "refreshToken": "..." }
Response: { "accessToken": "novo", "refreshToken": "novo" }
```

### **5. Adicionar Audit Log**

```java
// Antes de retornar sucesso
auditLogRepository.log(new AuditLog(
    accountId,
    "OAUTH2_LOGIN_SUCCESS",
    "google",
    googleUserInfo.email(),
    Instant.now()
));
```

### **6. Implementar Logout (invalidar token)**

```java
POST /api/auth/logout
Header: Authorization: Bearer <token>

// Adicionar à blacklist ou usar stateless + TTL curto
```

---

## 🏆 Comparação: Antes vs Depois

### **Antes (Hoje)**

```
App → idToken → Backend → Valida → Cria/Login → JWT
(Simples, funciona, mas básico)
```

### **Depois (Recomendado)**

```
App → idToken → Backend → Valida → {
  - Busca conta
  - Atualiza dados (nome/foto) se mudou
  - Registra audit log
  - Gera access token (1h)
  - Gera refresh token (30d)
  - Retorna ambos + dados
}
→ App salva ambos
→ Usa access token em requisições
→ Quando expira, usa refresh token para novo access token
```

---

## 📋 Implementação Passo-a-Passo

### **Passo 1: Migração do Banco**

```bash
# Editar docker/init.sql ou criar migração
ALTER TABLE accounts ADD COLUMN google_id VARCHAR(255) UNIQUE;
ALTER TABLE accounts ADD COLUMN picture_url TEXT;
ALTER TABLE accounts ADD COLUMN last_login_at TIMESTAMP;
```

### **Passo 2: Atualizar Entity Account**

```java
public class Account {
    private String googleId;
    private String pictureUrl;
    private LocalDateTime lastLoginAt;

    // getters, builders, etc
}
```

### **Passo 3: Atualizar Repository**

```java
// Em AccountPostgresRepository
public Account add(Account account) {
    String sql = "INSERT INTO accounts (..., google_id, picture_url, last_login_at) " +
                 "VALUES (..., ?, ?, ?)";
    // ...
}

public Account update(Account account) {
    String sql = "UPDATE accounts SET name=?, picture_url=?, last_login_at=? WHERE id=?";
    // ...
}
```

### **Passo 4: Atualizar GoogleAuthController**

```java
// Se email existe
if (existingAccount.isPresent()) {
    var account = existingAccount.get();

    // Atualiza dados se mudou
    if (!account.name().equals(googleUserInfo.name())) {
        account = new Account(
            account.id(),
            googleUserInfo.name(), // novo nome
            account.email(),
            account.password(),
            account.googleAccount(),
            account.createdAt(),
            LocalDateTime.now() // updated_at
        );
        accountRepository.update(account);
    }

    // Atualiza last_login_at
    account = account.withLastLoginAt(LocalDateTime.now());
    accountRepository.updateLastLogin(account);
}
```

### **Passo 5: Implementar Refresh Token**

```java
// Novo controller: RefreshTokenController
public record RefreshTokenResponse(
    String accessToken,
    String refreshToken,
    long expiresIn
) {}

// Endpoint: POST /api/auth/refresh
```

---

## 🎯 Status Final do Seu Projeto

**Situação Atual:**

- ✅ Signup automático ao fazer login com Google
- ✅ Signin ao usar email já cadastrado
- ✅ JWT retornado corretamente
- ✅ Email verificado (Google já fez isso)
- ✅ Banco salva corretamente

**Falta (Opcional, mas recomendado):**

- ⚠️ Armazenar google_id
- ⚠️ Atualizar dados se mudou
- ⚠️ Refresh tokens
- ⚠️ Audit logs
- ⚠️ Logout/blacklist

**Conclusão:**
Seu projeto já salva corretamente no banco. A estratégia implementada é sólida. As melhorias sugeridas são padrões de mercado mas **não essenciais** para funcionar.

---

## 🚀 Próximos Passos (Sugestão)

1. **Teste em produção** com usuários reais
2. **Adicione monitoramento** de logins (quantas pessoas, quando, de onde)
3. **Implemente refresh token** apenas se tokens expirarem frequentemente
4. **Audit log** se precisar rastrear abusos
5. **Multi-device** - um usuário em múltiplos celulares
