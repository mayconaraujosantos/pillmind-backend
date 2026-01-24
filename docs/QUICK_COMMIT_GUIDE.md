# Como Começar com Conventional Commits

## 🚀 Setup Rápido (5 minutos)

### 1. Configure o Template de Commit

```bash
# No diretório do projeto
git config --local commit.template .gitmessage
```

Agora, toda vez que você executar `git commit` sem `-m`, o template será exibido com exemplos e dicas!

### 2. Teste o Validador

```bash
# Windows
python scripts\validate-commit.py "feat(auth): add login feature"

# Linux/Mac
python3 scripts/validate-commit.py "feat(auth): add login feature"
```

## 📝 Formato Básico

```
<tipo>(<escopo>): <descrição>
```

### Tipos Mais Comuns

| Tipo | Use para... | Exemplo |
|------|-------------|---------|
| `feat` | Nova funcionalidade | `feat(api): add medicine endpoints` |
| `fix` | Correção de bug | `fix(auth): resolve token expiration issue` |
| `docs` | Documentação | `docs(readme): update setup instructions` |
| `test` | Testes | `test(service): add MedicineService tests` |
| `refactor` | Refatoração | `refactor(domain): extract validation logic` |

## ✅ Exemplos Práticos

### Desenvolvimento de Feature Completa

Ao implementar uma nova funcionalidade de lembretes de medicamento:

```bash
# 1. Criar entidade
git commit -m "feat(domain): add Reminder entity"

# 2. Criar repositório
git commit -m "feat(data): create ReminderRepository"

# 3. Criar serviço
git commit -m "feat(service): implement ReminderService"

# 4. Criar controller
git commit -m "feat(api): add reminder endpoints"

# 5. Adicionar testes
git commit -m "test(service): add ReminderService unit tests"

# 6. Documentar
git commit -m "docs(api): add Swagger docs for reminders"
```

### Correção de Bug

```bash
# Simples e direto
git commit -m "fix(api): handle null pointer in user search"

# Com mais contexto (corpo da mensagem)
git commit -m "fix(api): handle null pointer in user search

The search endpoint was crashing when no results were found.
Added null check before mapping results to DTOs.

Fixes: #123"
```

## 🔧 Git Aliases Úteis

Adicione ao seu `~/.gitconfig` ou `~/.git/config`:

```bash
[alias]
    # Commits rápidos
    cf = "!f() { git commit -m \"feat: $*\"; }; f"
    cx = "!f() { git commit -m \"fix: $*\"; }; f"
    cd = "!f() { git commit -m \"docs: $*\"; }; f"
    
    # Ver commits recentes
    ll = log --oneline --graph --decorate -10
    
    # Validar último commit
    vc = "!git log -1 --pretty=%B | python scripts/validate-commit.py"
```

Uso:
```bash
git cf "add new feature"          # Cria: feat: add new feature
git cx "resolve null pointer"     # Cria: fix: resolve null pointer
git ll                            # Lista últimos 10 commits
git vc                            # Valida último commit
```

## 📏 Regras de Ouro

1. **Use modo imperativo**: "add" não "added" ou "adds"
2. **Primeira letra minúscula**: "add feature" não "Add feature"
3. **Sem ponto final**: "add feature" não "add feature."
4. **Máximo 50 caracteres** no título (72 no máximo)
5. **Um commit = uma mudança** (princípio atômico)

## ⚠️ Erros Comuns

| ❌ Errado | ✅ Correto |
|----------|-----------|
| `Added new feature` | `feat: add new feature` |
| `fix bug` | `fix(api): resolve null pointer in search` |
| `Update code` | `refactor(service): extract validation logic` |
| `feat: Add Login And Profile.` | `feat(auth): add login feature` |

## 🎯 Workflow Diário

```bash
# 1. Faça uma mudança pequena
vim src/main/java/com/pillmind/domain/User.java

# 2. Stage apenas essa mudança
git add src/main/java/com/pillmind/domain/User.java

# 3. Commit (template abrirá no editor)
git commit
# Ou direto:
git commit -m "feat(domain): add email validation to User"

# 4. Valide (opcional mas recomendado)
git log -1 --pretty=%B | python scripts/validate-commit.py

# 5. Repita!
```

## 📚 Recursos

- [Guia Completo](docs/CONVENTIONAL_COMMITS.md) - Documentação detalhada
- [Contributing Guide](CONTRIBUTING.md) - Guia de contribuição
- [Conventional Commits Spec](https://www.conventionalcommits.org/) - Especificação oficial

## 💡 Dicas

### Mensagens com Corpo

Para commits que precisam de mais contexto:

```bash
git commit
```

No editor, escreva:
```
feat(auth): implement OAuth2 Google login

Add OAuth2 authentication flow for Google provider:
- Configure Spring Security OAuth2 client
- Add Google credentials to configuration
- Create OAuth2 success handler
- Add user registration on first login

This allows users to login using their Google account,
improving user experience and reducing friction.

Refs: #123
```

### Breaking Changes

Quando uma mudança quebra compatibilidade:

```bash
# Opção 1: Usar !
git commit -m "feat(api)!: change user endpoint response structure"

# Opção 2: Adicionar no rodapé
git commit -m "feat(api): change user endpoint response

BREAKING CHANGE: User endpoint now returns nested object
instead of flat structure. Update all API clients."
```

## 🎓 Aprenda Fazendo

Pratique com o validador:

```bash
# Teste diferentes mensagens
python scripts/validate-commit.py "feat: add feature"
python scripts/validate-commit.py "fix(api): resolve bug"
python scripts/validate-commit.py "Added new feature"  # Erro!
python scripts/validate-commit.py "feat(auth): add login."  # Erro!
```

---

**Pronto!** Você agora sabe o básico de Conventional Commits! 🎉

Comece devagar, e logo se tornará natural. Seus commits (e sua equipe) vão agradecer! 💙
