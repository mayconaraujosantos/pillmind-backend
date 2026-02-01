# Guia de Conventional Commits e Small Commits

## 📋 Índice

- [O que são Conventional Commits?](#o-que-são-conventional-commits)
- [Por que Small Commits?](#por-que-small-commits)
- [Formato](#formato)
- [Tipos de Commit](#tipos-de-commit)
- [Escopo (opcional)](#escopo-opcional)
- [Exemplos Práticos](#exemplos-práticos)
- [Boas Práticas](#boas-práticas)
- [Breaking Changes](#breaking-changes)
- [Ferramentas](#ferramentas)

## O que são Conventional Commits?

Conventional Commits é uma convenção para escrever mensagens de commit de forma padronizada e semântica. Isso facilita:

- 📖 Geração automática de CHANGELOGs
- 🔢 Versionamento semântico automático (SemVer)
- 🔍 Histórico de mudanças mais legível
- 🤖 Automação de CI/CD
- 👥 Melhor colaboração em equipe

## Por que Small Commits?

Small commits (commits pequenos e atômicos) trazem benefícios:

- ✅ **Revisão mais fácil**: Menos código para revisar por vez
- 🔄 **Revert simplificado**: Desfazer mudanças específicas sem afetar outras
- 🐛 **Debugging facilitado**: Usar `git bisect` para encontrar bugs
- 📚 **Histórico claro**: Cada commit conta uma história
- 🧪 **Testes incrementais**: Cada mudança pode ser testada isoladamente

## Formato

```
<tipo>[escopo opcional]: <descrição>

[corpo opcional]

[rodapé(s) opcional(is)]
```

### Estrutura Detalhada

```
<tipo>(<escopo>): <descrição curta>
│       │             │
│       │             └─⫸ Resumo no imperativo, sem ponto final
│       │
│       └─⫸ Escopo opcional: auth, api, database, etc
│
└─⫸ Tipo: feat, fix, docs, style, refactor, test, chore

[Corpo opcional - explica o "porquê" da mudança]

[Rodapé opcional - breaking changes, issues relacionadas]
```

## Tipos de Commit

| Tipo | Descrição | Exemplo |
|------|-----------|---------|
| **feat** | Nova funcionalidade | `feat(auth): add OAuth2 Google login` |
| **fix** | Correção de bug | `fix(api): resolve null pointer in user endpoint` |
| **docs** | Apenas documentação | `docs: update API endpoints in README` |
| **style** | Formatação, ponto e vírgula, etc | `style: format code with Google Java Style` |
| **refactor** | Refatoração sem mudar funcionalidade | `refactor(service): extract validation logic` |
| **perf** | Melhoria de performance | `perf(query): optimize database index` |
| **test** | Adicionar ou corrigir testes | `test(auth): add unit tests for login service` |
| **build** | Mudanças no build ou dependências | `build: upgrade Spring Boot to 3.2.0` |
| **ci** | Mudanças em CI/CD | `ci: add GitHub Actions workflow` |
| **chore** | Tarefas de manutenção | `chore: update .gitignore` |
| **revert** | Reverter commit anterior | `revert: revert "feat: add new feature"` |

## Escopo (opcional)

O escopo indica qual parte do código foi afetada:

### Escopos Comuns no PillMind

- **auth**: Autenticação e autorização
- **api**: Endpoints REST
- **domain**: Lógica de domínio
- **data**: Camada de dados/repositórios
- **config**: Configurações
- **security**: Segurança
- **migration**: Migrações de banco de dados
- **dto**: Data Transfer Objects
- **validation**: Validações
- **exception**: Tratamento de exceções
- **docs**: Documentação (Swagger/OpenAPI)

## Exemplos Práticos

### ✅ Bons Exemplos

```bash
# Nova funcionalidade
feat(auth): implement JWT token refresh mechanism

# Correção de bug
fix(api): handle null pointer in medicine search endpoint

# Documentação
docs(readme): add setup instructions for local development

# Refatoração
refactor(service): extract user validation to separate class

# Teste
test(domain): add unit tests for Medicine entity

# Build/Dependências
build(deps): upgrade PostgreSQL driver to 42.7.1

# Performance
perf(query): add database index on user_email column

# Breaking Change
feat(api)!: change authentication response structure

BREAKING CHANGE: auth endpoint now returns nested user object
```

### ❌ Exemplos Ruins

```bash
# Muito vago
fix: bug

# Não segue o formato
Fixed the login bug

# Múltiplas mudanças em um commit
feat: add login, update user profile, fix bug in medicines

# Descrição muito longa no título
feat(auth): implement the complete OAuth2 authentication flow with Google provider including token refresh and user profile synchronization
```

## Boas Práticas

### 1. Commits Atômicos

Cada commit deve fazer **uma coisa** bem feita:

```bash
# ✅ BOM: Commits separados
git commit -m "feat(domain): add Medicine entity"
git commit -m "feat(data): create MedicineRepository interface"
git commit -m "feat(service): implement MedicineService"
git commit -m "test(service): add MedicineService unit tests"

# ❌ RUIM: Tudo em um commit
git commit -m "feat: add complete medicine module"
```

### 2. Descrição Clara e Concisa

- Use o **modo imperativo**: "add" não "added" ou "adds"
- Máximo de **50 caracteres** no título
- Sem ponto final no título
- Corpo da mensagem com até **72 caracteres por linha**

```bash
# ✅ BOM
feat(auth): add password reset functionality

# ❌ RUIM
Added the password reset feature.
```

### 3. Explique o "Porquê"

O código mostra o "o quê", o commit deve explicar o "porquê":

```bash
feat(api): add rate limiting to authentication endpoints

Prevent brute force attacks by limiting login attempts to 5 per minute
per IP address. This improves security without significantly impacting
legitimate users.

Refs: #123
```

### 4. Teste Antes de Commitar

```bash
# Execute os testes
./gradlew test

# Verifique o build
./gradlew build

# Então commit
git commit -m "feat(service): add medicine reminder notification"
```

### 5. Commits Frequentes

Faça commits pequenos e frequentes:

```bash
# A cada mudança significativa
git add src/main/java/com/pillmind/domain/Medicine.java
git commit -m "feat(domain): add Medicine entity"

git add src/main/java/com/pillmind/data/MedicineRepository.java
git commit -m "feat(data): create MedicineRepository interface"

# E assim por diante...
```

## Breaking Changes

Mudanças que quebram compatibilidade devem ser marcadas:

### Opção 1: Usar `!` no tipo

```bash
feat(api)!: change user endpoint response structure
```

### Opção 2: Adicionar no rodapé

```bash
feat(api): update authentication flow

BREAKING CHANGE: The /auth/login endpoint now requires
email instead of username. Update all clients accordingly.
```

## Ferramentas

### Template de Commit

Configure um template para ajudar:

```bash
git config --local commit.template .gitmessage
```

### Validação no Pre-commit

Use hooks do Git para validar commits:

```bash
# Instalar commitlint (Node.js)
npm install -g @commitlint/cli @commitlint/config-conventional

# Ou use o script Python incluído
chmod +x scripts/validate-commit.py
```

### Aliases Úteis

Adicione ao seu `.gitconfig`:

```bash
[alias]
    cf = "commit -m 'feat: '"
    cx = "commit -m 'fix: '"
    cd = "commit -m 'docs: '"
    ct = "commit -m 'test: '"
```

## Workflow Recomendado

1. **Faça uma mudança pequena**
   ```bash
   # Edite apenas os arquivos necessários para uma mudança específica
   vim src/main/java/com/pillmind/domain/User.java
   ```

2. **Teste a mudança**
   ```bash
   ./gradlew test --tests UserTest
   ```

3. **Stage e commit**
   ```bash
   git add src/main/java/com/pillmind/domain/User.java
   git commit -m "feat(domain): add email validation to User entity"
   ```

4. **Repita para a próxima mudança**

## Exemplos de Sequência de Commits

Ao adicionar uma nova feature completa, quebre em commits pequenos:

```bash
# 1. Criar entidade
feat(domain): add Reminder entity

# 2. Criar repositório
feat(data): create ReminderRepository interface

# 3. Criar DTO
feat(dto): add ReminderDTO for API responses

# 4. Criar serviço
feat(service): implement ReminderService logic

# 5. Adicionar controller
feat(api): add reminder endpoints

# 6. Adicionar testes
test(service): add ReminderService unit tests

# 7. Documentar
docs(api): add Swagger annotations for reminder endpoints

# 8. Atualizar migrações
build(migration): add reminders table migration
```

## Referências

- [Conventional Commits Specification](https://www.conventionalcommits.org/)
- [Semantic Versioning](https://semver.org/)
- [How to Write a Git Commit Message](https://chris.beams.io/posts/git-commit/)
- [Angular Commit Guidelines](https://github.com/angular/angular/blob/main/CONTRIBUTING.md#commit)

---

**Lembre-se**: Commits pequenos e bem descritos são um presente para seu futuro eu e para sua equipe! 🎁
