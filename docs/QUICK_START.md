# Clean Architecture - Guia Rápido

## 📚 Estrutura Criada

### Camadas da Arquitetura

```
src/main/java/com/pillmind/
├── 📁 domain/              → Regras de Negócio (independente)
│   ├── models/             → Entidades (Account)
│   └── usecases/           → Interfaces (AddAccount, Authentication)
│
├── 📁 data/                → Orquestração
│   ├── protocols/          → Interfaces (Ports)
│   │   ├── db/             → Repositories
│   │   └── cryptography/   → Hasher, Encrypter
│   └── usecases/           → Implementações (DbAddAccount, DbAuthentication)
│
├── 📁 presentation/        → Interface HTTP
│   ├── controllers/        → SignUpController, LoginController
│   ├── protocols/          → Controller, Validation, HttpResponse
│   ├── helpers/            → HttpHelper (ok, badRequest, etc)
│   └── errors/             → ServerError, UnauthorizedError, etc
│
├── 📁 infra/               → Implementações Técnicas
│   ├── db/                 → AccountRepositoryInMemory
│   ├── cryptography/       → BCryptAdapter, JwtAdapter
│   └── validators/         → (a implementar)
│
└── 📁 main/                → Composition Root
    ├── factories/          → AddAccountFactory, AuthenticationFactory
    ├── adapters/           → JavalinRouteAdapter
    ├── routes/             → AuthRoutes
    └── config/             → Env
```

## 🎯 Design Patterns Implementados

### 1. Factory Pattern

```java
// main/factories/AddAccountFactory.java
public static AddAccount make() {
    Hasher hasher = new BCryptAdapter(Env.BCRYPT_SALT);
    // ... cria e conecta todas as dependências
    return new DbAddAccount(hasher, addAccountRepo, loadAccountRepo);
}
```

### 2. Adapter Pattern

```java
// infra/cryptography/BCryptAdapter.java
public class BCryptAdapter implements Hasher, HashComparer {
    // Adapta BCrypt para nossa interface
}

// main/adapters/JavalinRouteAdapter.java
public static <T> void adapt(Controller<T> controller, Context ctx) {
    // Adapta Javalin para nossos Controllers
}
```

### 3. Dependency Injection

```java
// Injeção via construtor em todos os lugares
public class DbAddAccount implements AddAccount {
    public DbAddAccount(
        Hasher hasher,
        AddAccountRepository addAccountRepository,
        LoadAccountByEmailRepository loadAccountByEmailRepository
    ) { ... }
}
```

### 4. Strategy Pattern

```java
// Diferentes estratégias de validação
public interface Validation {
    Exception validate(Object input);
}
```

### 5. Repository Pattern

```java
// Abstração da persistência
public interface AddAccountRepository {
    boolean add(AddAccount.Params params);
}
```

## ✅ Princípios SOLID Aplicados

### Single Responsibility Principle

- Cada classe tem uma única responsabilidade
- Controller → receber/responder HTTP
- UseCase → orquestrar regras de negócio
- Repository → persistir dados

### Open/Closed Principle

- Aberto para extensão, fechado para modificação
- Novos hasher podem ser adicionados sem alterar código existente

### Liskov Substitution Principle

- Qualquer Hasher pode substituir outro
- Qualquer Repository pode substituir outro

### Interface Segregation Principle

- Interfaces específicas: `AddAccountRepository` vs `LoadAccountByEmailRepository`
- Cliente não depende de métodos que não usa

### Dependency Inversion Principle

- Módulos de alto nível não dependem de baixo nível
- Domain não conhece Infrastructure
- Tudo depende de abstrações (interfaces)

## 🔧 Comandos Úteis

```bash
# Executar aplicação
./gradlew run

# Executar testes
./gradlew test

# Compilar
./gradlew build

# Limpar build
./gradlew clean
```

## 📝 Endpoints Criados

### Health Check

```bash
curl http://localhost:7000/api/health
```

### Cadastro

```bash
curl -X POST http://localhost:7000/api/signup \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "password": "123456",
    "passwordConfirmation": "123456"
  }'
```

### Login

```bash
curl -X POST http://localhost:7000/api/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "123456"
  }'
```

## 🧪 Testes Criados

- `DbAddAccountTest`: Testes unitários para AddAccount
  - ✓ Deve chamar Hasher com senha correta
  - ✓ Deve chamar Repository com valores corretos
  - ✓ Deve retornar true em sucesso
  - ✓ Deve retornar false se email já existe
  - ✓ Deve chamar LoadAccountByEmail com email correto

## 🚀 Próximos Passos

### Validações

- [ ] Implementar RequiredFieldValidation
- [ ] Implementar EmailValidation
- [ ] Implementar CompareFieldsValidation
- [ ] Implementar ValidationComposite (Composite Pattern)

### Banco de Dados

- [ ] Implementar AccountMongoRepository
- [ ] Implementar AccountPostgreSQLRepository
- [ ] Adicionar migrations (Flyway)

### Autenticação

- [ ] Implementar middleware de autenticação
- [ ] Implementar LoadAccountByToken
- [ ] Adicionar roles (admin, user)

### Casos de Uso Adicionais

- [ ] AddSurvey (criar enquete)
- [ ] LoadSurveys (listar enquetes)
- [ ] SaveSurveyResult (responder enquete)
- [ ] LoadSurveyResult (resultado da enquete)

### Infraestrutura

- [ ] Adicionar logs estruturados (SLF4J + Logback)
- [ ] Adicionar métricas (Micrometer)
- [ ] Configurar Docker
- [ ] Configurar CI/CD
- [ ] Adicionar documentação Swagger/OpenAPI

## 📖 Documentação

- `README.md`: Documentação principal
- `ARCHITECTURE.md`: Explicação detalhada da arquitetura
- Este arquivo: Guia rápido de referência

## 💡 Dicas

1. **Sempre comece pelo Domain**: Defina entidades e use cases primeiro
2. **Testes antes da implementação**: Siga TDD (Red → Green → Refactor)
3. **Mantenha o Domain puro**: Sem dependências de frameworks
4. **Use interfaces**: Facilita testes e mantém flexibilidade
5. **Composition Root**: Todas as dependências criadas em um só lugar (main/factories)

## 🎓 Conceitos Importantes

### Clean Architecture

- Separação em camadas concêntricas
- Dependências apontam para dentro
- Domain no centro, independente

### TDD (Test-Driven Development)

1. 🔴 Red: Escreva um teste que falha
2. 🟢 Green: Escreva código mínimo para passar
3. 🔵 Refactor: Melhore o código mantendo testes passando

### Ports and Adapters (Hexagonal Architecture)

- **Ports**: Interfaces (protocols)
- **Adapters**: Implementações concretas (infra)
- Facilita troca de implementações

## 📚 Referências

- [Clean Architecture - Uncle Bob](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Clean TypeScript API - Rodrigo Manguinho](https://github.com/rmanguinho/clean-ts-api)
- [SOLID Principles](https://en.wikipedia.org/wiki/SOLID)
- [Design Patterns - Gang of Four](https://refactoring.guru/design-patterns)
