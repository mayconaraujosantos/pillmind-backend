# Clean Architecture - Estrutura de Pastas

## 📊 Diagrama de Dependências

```
┌─────────────────────────────────────────────────────────────┐
│                         MAIN LAYER                          │
│  (Composition Root - Factory Pattern)                       │
│  • Factories: Cria e conecta todas as dependências          │
│  • Adapters: Adapta frameworks externos                     │
│  • Routes: Configuração de rotas                            │
│  • Config: Variáveis de ambiente                            │
└────────────────────────┬────────────────────────────────────┘
                         │ depende de
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                        │
│  (Interface com usuário/HTTP)                               │
│  • Controllers: Recebem requisições HTTP                    │
│  • Protocols: Interfaces (Controller, Validation)           │
│  • Helpers: Auxiliares para respostas HTTP                  │
│  • Errors: Erros customizados                               │
└────────────────────────┬────────────────────────────────────┘
                         │ depende de
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                       DATA LAYER                            │
│  (Orquestração e Protocols)                                 │
│  • UseCases: Implementações dos casos de uso                │
│  • Protocols: Interfaces (Ports) para inversão              │
│    - db: Contratos de repositórios                          │
│    - cryptography: Contratos de criptografia                │
└────────────────────────┬────────────────────────────────────┘
                         │ depende de
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                      DOMAIN LAYER                           │
│  (Regras de Negócio - Núcleo)                              │
│  • Models: Entidades de domínio                             │
│  • UseCases: Interfaces dos casos de uso                    │
│  ⚠️  INDEPENDENTE DE FRAMEWORKS                             │
└─────────────────────────────────────────────────────────────┘
                         ▲
                         │ implementado por
┌────────────────────────┴────────────────────────────────────┐
│                  INFRASTRUCTURE LAYER                        │
│  (Implementações técnicas)                                  │
│  • DB: Repositórios concretos (MongoDB, PostgreSQL)         │
│  • Cryptography: BCrypt, JWT                                │
│  • Validators: Validadores de email, etc                    │
└─────────────────────────────────────────────────────────────┘
```

## 🔄 Fluxo de Execução (Exemplo: SignUp)

```
1. HTTP Request
   │
   └─> [Javalin] (Framework)
        │
        └─> [JavalinRouteAdapter] (Main/Adapters)
             │
             └─> [SignUpController] (Presentation/Controllers)
                  │
                  ├─> [Validation] (Presentation/Protocols)
                  │
                  └─> [AddAccount UseCase] (Domain/UseCases - Interface)
                       │
                       └─> [DbAddAccount] (Data/UseCases - Implementação)
                            │
                            ├─> [Hasher] (Data/Protocols - Interface)
                            │    └─> [BCryptAdapter] (Infra/Cryptography)
                            │
                            └─> [AddAccountRepository] (Data/Protocols - Interface)
                                 └─> [AccountRepositoryInMemory] (Infra/DB)
```

## 🎯 Princípios Aplicados

### 1. Dependency Inversion Principle (DIP)

- Camadas de alto nível não dependem de camadas de baixo nível
- Ambas dependem de abstrações (interfaces)
- Domain não conhece Infrastructure

### 2. Single Responsibility Principle (SRP)

- Cada classe tem uma única responsabilidade
- Controllers: apenas receber e responder HTTP
- UseCases: apenas orquestrar regras de negócio
- Repositories: apenas persistir dados

### 3. Open/Closed Principle (OCP)

- Aberto para extensão, fechado para modificação
- Novos validadores podem ser adicionados sem modificar código existente
- Novas implementações de repository podem ser criadas

### 4. Liskov Substitution Principle (LSP)

- Qualquer implementação de Hasher pode substituir outra
- Qualquer implementação de Repository pode substituir outra

### 5. Interface Segregation Principle (ISP)

- Interfaces específicas por responsabilidade
- AddAccountRepository vs LoadAccountByEmailRepository

## 📦 Pacotes e Responsabilidades

### com.pillmind.domain.models

**Responsabilidade**: Entidades de domínio

- Account, Survey, SurveyResult
- POJOs simples com regras de negócio mínimas

### com.pillmind.domain.usecases

**Responsabilidade**: Contratos dos casos de uso

- Interfaces que definem operações de negócio
- AddAccount, Authentication, LoadSurveys

### com.pillmind.data.protocols

**Responsabilidade**: Portas (Ports) para inversão de dependência

- Interfaces que a infra deve implementar
- AddAccountRepository, Hasher, Encrypter

### com.pillmind.data.usecases

**Responsabilidade**: Implementação dos casos de uso

- Orquestra chamadas aos repositories e services
- DbAddAccount, DbAuthentication

### com.pillmind.presentation.controllers

**Responsabilidade**: Receber e responder requisições HTTP

- Validação de entrada
- Chamada aos use cases
- Formatação de resposta

### com.pillmind.infra.db

**Responsabilidade**: Implementações de persistência

- AccountRepositoryInMemory, AccountMongoRepository
- Adapters para bancos de dados

### com.pillmind.infra.cryptography

**Responsabilidade**: Implementações de criptografia

- BCryptAdapter, JwtAdapter
- Adapters para bibliotecas de segurança

### com.pillmind.main.factories

**Responsabilidade**: Criação e composição de dependências

- Composition Root
- Factory Pattern
- Dependency Injection manual

## 🧪 Testes

Estrutura espelhada em `src/test/java/com/pillmind/`:

- **domain**: Testes de entidades e regras de negócio
- **data**: Testes de use cases com mocks
- **presentation**: Testes de controllers
- **infra**: Testes de integrações

## 🔐 Segurança

- Senhas hasheadas com BCrypt (salt 12)
- Tokens JWT com expiração de 24h
- Validação de dados de entrada
- CORS configurado

## 🚀 Próximos Passos

1. Implementar validações completas (Composite Pattern)
2. Adicionar banco de dados real (PostgreSQL/MongoDB)
3. Implementar middleware de autenticação
4. Adicionar logs estruturados
5. Implementar casos de uso de Survey
6. Adicionar documentação Swagger/OpenAPI
7. Configurar CI/CD
8. Adicionar métricas e monitoring
