# Setup Developer - PillMind

Guia completo para configurar o ambiente de desenvolvimento com Java 21, Spring Boot/Javalin e React Native.

## Requisitos

### Java 21
- **Download**: [Oracle JDK 21](https://www.oracle.com/java/technologies/downloads/#java21) ou [OpenJDK 21](https://jdk.java.net/21/)
- **Instalação Windows**:
  ```powershell
  # Verify installation
  java -version
  javac -version
  ```
- **Variável de ambiente**: `JAVA_HOME` deve apontar para o diretório do JDK 21

### Node.js
- **Versão**: 20.11.0+ (ver `.nvmrc`)
- **Download**: [nodejs.org](https://nodejs.org/)
- **Recomendado**: Usar NVM (Node Version Manager)
  ```powershell
  # Windows - Instalar NVM
  choco install nvm
  
  # Usar versão correta
  nvm install 20.11.0
  nvm use 20.11.0
  
  # Verificar
  node -v
  npm -v
  ```

### Gradle (opcional, já incluído)
- Usa `gradlew` (Gradle Wrapper) - sem instalação necessária

---

## Setup Inicial

### 1. Clonar repositório
```bash
git clone <repo-url>
cd pillmind-backend
```

### 2. Configurar variáveis de ambiente
```bash
# Copiar arquivo de exemplo
cp .env.example .env

# Editar .env com suas configurações
code .env
```

### 3. Backend Java/Javalin

#### Instalar dependências Gradle
```powershell
./gradlew build
```

#### Executar servidor
```powershell
# Desenvolvimento (com reload automático)
./gradlew run --continuous

# Ou debug
./gradlew run --debug
```

#### Build para produção
```powershell
./gradlew build -x test
```

#### Executar testes
```powershell
# Todos os testes
./gradlew test

# Teste específico
./gradlew test --tests com.pillmind.data.usecases.CreateUserUseCaseTest
```

### 4. Frontend React Native

#### Criar projeto React Native (se ainda não existir)
```bash
npx create-expo-app pillmind-mobile --template
# ou
npx react-native init pillmind-mobile
```

#### Instalar dependências
```bash
cd pillmind-mobile
npm install
# ou
yarn install
```

#### Executar em desenvolvimento
```bash
# Expo (recomendado para início rápido)
npm start

# React Native CLI
npm run android
npm run ios
```

---

## VS Code Setup

### Extensões Instaladas
Abra a paleta de comandos (`Ctrl+Shift+P`) e digite:
```
Extensions: Show Recommended Extensions
```

Extensões principais:
- **Java**: Extension Pack for Java (RedHat)
- **Spring Boot**: Spring Boot Dashboard (vscjava)
- **Gradle**: Gradle for Java (vscjava)
- **Prettier**: Code formatter
- **ESLint**: Linter para JavaScript
- **Copilot**: GitHub Copilot
- **GitLens**: Git features

### Atalhos Úteis

#### Java/Backend
- `F5` - Iniciar debug
- `Ctrl+Shift+D` - Debug view
- `Ctrl+Shift+T` - Abrir testes
- `F9` - Toggle breakpoint
- `Ctrl+F5` - Restart debugger

#### JavaScript/TypeScript
- `Shift+Alt+F` - Format document
- `Ctrl+Space` - Code completion
- `F8` - Go to next error

#### Git
- `Ctrl+Shift+G` - Git view
- `Ctrl+Alt+G` - GitLens commands

---

## Estrutura do Projeto

```
pillmind-backend/
├── src/main/java/com/pillmind/
│   ├── Main.java                    # Entry point
│   ├── data/                        # Data layer
│   │   ├── protocols/               # Interfaces
│   │   └── usecases/                # Use cases implementation
│   ├── domain/                      # Domain layer (business logic)
│   │   ├── models/                  # Domain models/entities
│   │   └── usecases/                # Use case interfaces
│   ├── infra/                       # Infrastructure layer
│   │   ├── cryptography/            # Crypto implementations
│   │   ├── db/                      # Database/repositories
│   │   └── validators/              # Input validators
│   ├── main/                        # Main/composition layer
│   │   ├── adapters/                # HTTP adapters
│   │   ├── config/                  # Configuration
│   │   ├── factories/               # Dependency factories
│   │   └── routes/                  # API routes
│   └── presentation/                # Presentation layer
│       ├── controllers/             # HTTP controllers
│       ├── errors/                  # Error handling
│       ├── helpers/                 # Utilities
│       └── protocols/               # HTTP protocols
├── src/test/java/                   # Unit and integration tests
├── build.gradle                     # Gradle configuration
├── .env.example                     # Environment template
├── .vscode/
│   ├── settings.json                # VS Code workspace settings
│   ├── launch.json                  # Debug configurations
│   └── extensions.json              # Recommended extensions
└── DEV_SETUP.md                     # Este arquivo
```

---

## Debugging

### Backend (Java/Javalin)

#### Configurar breakpoint
1. Abra arquivo Java
2. Clique na linha desejada (ao lado do número)
3. Pressione `F5` ou clique em "Run and Debug"

#### Inspecionar variáveis
- Abra a aba "Debug" (Ctrl+Shift+D)
- Variables, Watch, Call Stack estão disponíveis

#### Conditional Breakpoints
- Clique com botão direito no breakpoint
- Selecione "Edit Breakpoint"
- Adicione condição (ex: `count > 10`)

### Frontend (React Native)

#### Remote Debugger
```bash
# Terminal 1 - Inicie Expo
npm start

# Terminal 2 - Debugger
npm run android -- --localhost
```

#### React DevTools
```bash
npm install -g react-devtools
react-devtools
```

---

## Docker (Opcional)

### Backend
```dockerfile
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY . .
RUN ./gradlew build -x test
EXPOSE 8080
CMD ["java", "-jar", "build/libs/pillmind-backend-1.0-SNAPSHOT.jar"]
```

### Executar
```bash
docker build -t pillmind-backend .
docker run -p 8080:8080 --env-file .env pillmind-backend
```

---

## Troubleshooting

### Java não encontrado
```powershell
# Verificar JAVA_HOME
echo $env:JAVA_HOME

# Definir manualmente
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
[Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Java\jdk-21", "User")
```

### Gradle build falha
```powershell
# Limpar cache
./gradlew clean

# Rebuild
./gradlew build
```

### Porta 8080 já em uso
```powershell
# Encontrar processo
Get-NetTCPConnection -LocalPort 8080

# Matar processo (substitua PID)
Stop-Process -Id <PID> -Force

# Ou usar porta diferente
$env:PORT = 3000
```

### Node modules corrompido
```bash
rm -r node_modules package-lock.json
npm install
```

---

## Comandos Úteis

### Backend
```bash
# Build
./gradlew build

# Testes
./gradlew test
./gradlew test --watch

# Clean
./gradlew clean

# Dependências
./gradlew dependencies
```

### Frontend
```bash
# Instalar
npm install

# Start dev
npm start

# Build
npm run build

# Test
npm test

# Lint
npm run lint
npm run lint -- --fix
```

---

## Links Úteis

- [Javalin Documentation](https://javalin.io/)
- [React Native Docs](https://reactnative.dev/)
- [Java 21 Features](https://openjdk.java.net/projects/jdk/21/)
- [Gradle Guide](https://gradle.org/guide/)
- [VS Code Tips](https://code.visualstudio.com/tips-and-tricks)

---

## Suporte

Para problemas ou dúvidas:
1. Verificar logs: `build/reports/`
2. Consultar documentação oficial
3. Abrir issue no repositório
