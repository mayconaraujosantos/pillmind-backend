# Guia de Contribuição - PillMind Backend

## 📋 Índice

- [Bem-vindo](#bem-vindo)
- [Como Contribuir](#como-contribuir)
- [Padrões de Código](#padrões-de-código)
- [Conventional Commits](#conventional-commits)
- [Small Commits](#small-commits)
- [Processo de Pull Request](#processo-de-pull-request)
- [Testes](#testes)
- [Dúvidas](#dúvidas)

## Bem-vindo

Obrigado por considerar contribuir com o PillMind Backend! 🎉

Este documento fornece diretrizes para contribuições ao projeto. Seguir estas diretrizes ajuda a manter a qualidade do código e facilita a colaboração.

## Como Contribuir

### 1. Fork e Clone

```bash
# Fork o repositório no GitHub
# Clone seu fork
git clone https://github.com/seu-usuario/pillmind-backend.git
cd pillmind-backend

# Adicione o repositório original como upstream
git remote add upstream https://github.com/mayconaraujosantos/pillmind-backend.git
```

### 2. Configure o Template de Commit

```bash
# Configure o template de commit message
git config --local commit.template .gitmessage

# Verifique se foi configurado
git config --local commit.template
```

### 3. Crie uma Branch

Use nomes descritivos para suas branches:

```bash
# Padrão: <tipo>/<descrição-curta>
git checkout -b feat/add-medicine-reminders
git checkout -b fix/null-pointer-user-endpoint
git checkout -b docs/update-api-documentation
```

### 4. Faça suas Mudanças

- Siga os padrões de código do projeto
- Escreva testes para novas funcionalidades
- Atualize a documentação quando necessário
- Faça commits pequenos e frequentes

## Padrões de Código

### Java/Spring Boot

- Siga o [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- Use nomes descritivos para classes, métodos e variáveis
- Mantenha métodos pequenos (máximo 20-30 linhas)
- Adicione JavaDoc para APIs públicas
- Use injeção de dependência apropriadamente

### Estrutura de Packages

```
com.pillmind
├── domain/       # Entidades e lógica de domínio
├── data/         # Repositórios e acesso a dados
├── presentation/ # Controllers e DTOs
├── infra/        # Configurações e infraestrutura
└── util/         # Utilitários compartilhados
```

### Nomenclatura

```java
// ✅ BOM
public class MedicineService { }
public interface MedicineRepository extends JpaRepository<Medicine, Long> { }
public class MedicineDTO { }
public class MedicineMapper { }

// ❌ EVITE
public class MedService { }  // Nome muito curto
public class IMedicineRepo { } // Prefixo 'I' desnecessário em Java
public class MedicineData { } // Ambíguo
```

## Conventional Commits

**Obrigatório**: Todas as mensagens de commit devem seguir o padrão [Conventional Commits](https://www.conventionalcommits.org/).

### Formato

```
<tipo>(<escopo>): <descrição>

[corpo opcional]

[rodapé(s) opcional(is)]
```

### Tipos Permitidos

| Tipo | Quando Usar |
|------|-------------|
| `feat` | Nova funcionalidade |
| `fix` | Correção de bug |
| `docs` | Apenas documentação |
| `style` | Formatação, ponto e vírgula, etc |
| `refactor` | Refatoração sem mudar funcionalidade |
| `perf` | Melhoria de performance |
| `test` | Adicionar ou corrigir testes |
| `build` | Mudanças no build ou dependências |
| `ci` | Mudanças em CI/CD |
| `chore` | Tarefas de manutenção |

### Exemplos

```bash
# ✅ Bons exemplos
feat(auth): add JWT token refresh mechanism
fix(api): resolve null pointer in medicine search endpoint
docs(readme): update setup instructions
test(service): add unit tests for MedicineService

# ❌ Exemplos ruins
Add new feature      # Não segue o formato
fix bug             # Muito vago
feat: add login and update profile  # Múltiplas mudanças
```

### Validação

Valide suas mensagens antes de commitar:

```bash
# Windows
scripts\validate-commit.bat "feat(auth): add login feature"

# Linux/Mac
chmod +x scripts/validate-commit.sh
./scripts/validate-commit.sh "feat(auth): add login feature"

# Ou valide o último commit
git log -1 --pretty=%B | python scripts/validate-commit.py
```

📚 **Consulte**: [docs/CONVENTIONAL_COMMITS.md](docs/CONVENTIONAL_COMMITS.md) para guia completo

## Small Commits

### Princípio

**Um commit = Uma mudança lógica**

Cada commit deve ser:
- ✅ **Atômico**: Faz uma coisa bem feita
- ✅ **Completo**: Compila e passa nos testes
- ✅ **Independente**: Pode ser revertido sem afetar outros commits
- ✅ **Descritivo**: A mensagem explica claramente a mudança

### ❌ Exemplo Ruim (Commit Grande)

```bash
# Commit único com muitas mudanças
git add .
git commit -m "feat: add complete medicine module with tests and docs"

# Mudanças:
# - Medicine entity
# - MedicineRepository
# - MedicineService
# - MedicineController
# - MedicineDTO
# - Unit tests
# - Integration tests
# - API documentation
```

**Problemas**:
- Difícil de revisar
- Difícil de reverter parcialmente
- Histórico pouco claro
- Dificulta uso de `git bisect`

### ✅ Exemplo Bom (Small Commits)

```bash
# Commit 1: Entidade
git add src/main/java/com/pillmind/domain/Medicine.java
git commit -m "feat(domain): add Medicine entity

Add basic Medicine entity with fields:
- name, dosage, frequency, startDate, endDate"

# Commit 2: Repositório
git add src/main/java/com/pillmind/data/MedicineRepository.java
git commit -m "feat(data): create MedicineRepository interface"

# Commit 3: Service
git add src/main/java/com/pillmind/service/MedicineService.java
git commit -m "feat(service): implement MedicineService

Add methods:
- create, update, delete, findById, findAll"

# Commit 4: DTO
git add src/main/java/com/pillmind/dto/MedicineDTO.java
git commit -m "feat(dto): add MedicineDTO for API responses"

# Commit 5: Controller
git add src/main/java/com/pillmind/controller/MedicineController.java
git commit -m "feat(api): add medicine endpoints

Add REST endpoints:
- GET /api/medicines
- GET /api/medicines/{id}
- POST /api/medicines
- PUT /api/medicines/{id}
- DELETE /api/medicines/{id}"

# Commit 6: Testes
git add src/test/java/com/pillmind/service/MedicineServiceTest.java
git commit -m "test(service): add MedicineService unit tests"

# Commit 7: Documentação
git add src/main/java/com/pillmind/controller/MedicineController.java
git commit -m "docs(api): add Swagger annotations for medicine endpoints"
```

**Benefícios**:
- ✅ Cada commit é revisável isoladamente
- ✅ Fácil encontrar quando um bug foi introduzido
- ✅ Fácil reverter uma mudança específica
- ✅ Histórico claro e documentado

### Quando Commitar?

Faça um commit quando:

1. ✅ Você completou uma unidade de trabalho lógica
2. ✅ O código compila sem erros
3. ✅ Os testes passam
4. ✅ A mudança pode ser descrita em uma frase clara

**NÃO** commite:
- ❌ Código que não compila
- ❌ Código com testes falhando
- ❌ Múltiplas mudanças não relacionadas
- ❌ Trabalho incompleto (use `git stash` em vez disso)

### Workflow Recomendado

```bash
# 1. Trabalhe em uma mudança específica
vim src/main/java/com/pillmind/domain/User.java

# 2. Execute os testes
./gradlew test --tests UserTest

# 3. Stage apenas os arquivos relacionados
git add src/main/java/com/pillmind/domain/User.java

# 4. Verifique o que será commitado
git diff --cached

# 5. Commit
git commit -m "feat(domain): add email validation to User entity"

# 6. Repita para a próxima mudança
```

### Git Aliases Úteis

Adicione ao seu `~/.gitconfig`:

```bash
[alias]
    # Commits rápidos com validação
    cf = !sh -c 'git add . && git commit -m \"feat: $1\"' -
    cx = !sh -c 'git add . && git commit -m \"fix: $1\"' -
    
    # Ver últimos commits de forma compacta
    ll = log --oneline --graph --decorate -10
    
    # Ver o que vai ser commitado
    staged = diff --cached
    
    # Desfazer último commit (mantém mudanças)
    undo = reset --soft HEAD~1
```

## Processo de Pull Request

### Antes de Abrir um PR

1. **Atualize sua branch com a develop**
   ```bash
   git checkout develop
   git pull upstream develop
   git checkout sua-branch
   git rebase develop
   ```

2. **Execute todos os testes**
   ```bash
   ./gradlew clean test
   ```

3. **Verifique o build**
   ```bash
   ./gradlew build
   ```

4. **Revise seus commits**
   ```bash
   git log --oneline develop..HEAD
   ```

### Abrindo o PR

1. Push para seu fork:
   ```bash
   git push origin sua-branch
   ```

2. Abra um PR no GitHub com:
   - Título descritivo seguindo Conventional Commits
   - Descrição clara do que foi mudado
   - Screenshots (se aplicável)
   - Issues relacionadas (use `Closes #123`)

### Template de PR

```markdown
## Descrição
Breve descrição das mudanças

## Tipo de Mudança
- [ ] 🐛 Bug fix
- [ ] ✨ Nova funcionalidade
- [ ] 📝 Documentação
- [ ] 🔨 Refatoração
- [ ] ⚡ Performance

## Checklist
- [ ] Código segue os padrões do projeto
- [ ] Commits seguem Conventional Commits
- [ ] Commits são pequenos e atômicos
- [ ] Testes adicionados/atualizados
- [ ] Documentação atualizada
- [ ] Todos os testes passam
- [ ] Build passa sem erros

## Issues Relacionadas
Closes #123

## Screenshots (se aplicável)
```

### Revisão de Código

- Responda aos comentários prontamente
- Faça commits adicionais para correções
- Não faça `force push` após revisão (use `git push`)
- Mantenha a conversa profissional e construtiva

## Testes

### Executar Testes

```bash
# Todos os testes
./gradlew test

# Testes específicos
./gradlew test --tests MedicineServiceTest

# Com coverage
./gradlew test jacocoTestReport
```

### Escrever Testes

- Use JUnit 5
- Nomeie testes descritivamente: `shouldReturnUserWhenValidIdProvided`
- Organize com: Given/When/Then ou Arrange/Act/Assert
- Teste casos de sucesso e falha
- Mock dependências externas

```java
@Test
void shouldCreateMedicineWhenValidDataProvided() {
    // Given
    MedicineDTO medicineDTO = new MedicineDTO("Aspirin", "100mg");
    
    // When
    Medicine result = medicineService.create(medicineDTO);
    
    // Then
    assertNotNull(result.getId());
    assertEquals("Aspirin", result.getName());
}
```

## Dúvidas

### Documentação Adicional

- 📚 [Conventional Commits Guide](docs/CONVENTIONAL_COMMITS.md)
- 🏗️ [Architecture Guide](docs/ARCHITECTURE.md)
- 🚀 [Quick Start](docs/QUICK_START.md)
- 🔧 [Dev Setup](docs/DEV_SETUP.md)

### Contato

- Abra uma [issue](https://github.com/mayconaraujosantos/pillmind-backend/issues) para perguntas
- Use [Discussions](https://github.com/mayconaraujosantos/pillmind-backend/discussions) para ideias

---

**Obrigado por contribuir com o PillMind!** 💊💙

Lembre-se:
- ✅ Commits pequenos e frequentes
- ✅ Mensagens descritivas
- ✅ Testes sempre
- ✅ Código limpo e documentado
