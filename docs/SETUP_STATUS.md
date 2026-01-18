# 📋 Setup Developer Checklist - PillMind

## Status: ✅ CONFIGURAÇÃO COMPLETA

---

## 📦 Arquivos Criados/Atualizados

### VS Code Configuration
- ✅ `.vscode/settings.json` - Otimizado para Java 21, JS/TS, Prettier
- ✅ `.vscode/launch.json` - Debug configs (Backend + Frontend)
- ✅ `.vscode/tasks.json` - Build/Run tasks automáticas
- ✅ `.vscode/extensions.json` - Extensões recomendadas

### Code Quality
- ✅ `.editorconfig` - Configuração uniforme (indent, charset, EOF)
- ✅ `.prettierrc` - Formatter settings
- ✅ `.prettierignore` - Exclusões prettier

### Project Configuration
- ✅ `.gitignore` - Atualizado para Java + Node + React Native
- ✅ `.env.example` - Template com variáveis essenciais
- ✅ `.nvmrc` - Versão Node.js (20.11.0)
- ✅ `build.gradle` - Atualizado com Java 21 target

### Documentation
- ✅ `QUICK_SETUP.md` - Guia rápido de setup (LEIA PRIMEIRO!)
- ✅ `DEV_SETUP.md` - Documentação detalhada
- ✅ `SETUP_CHECKLIST.md` - Checklist interativo
- ✅ `SETUP_STATUS.md` - Este arquivo

### Automation Scripts
- ✅ `setup.bat` - Setup automático Windows
- ✅ `setup.sh` - Setup automático macOS/Linux

---

## 🎯 Próximos Passos (Em Ordem)

### Fase 1: Pré-requisitos (Antes de qualquer coisa)

```powershell
# 1. Instalar Java 21
# Download: https://www.oracle.com/java/technologies/downloads/#java21
# Ou: https://jdk.java.net/21/

# 2. Verificar instalação
java -version
# Deve mostrar: openjdk 21.x.x ou similiar

# 3. Configurar JAVA_HOME (se necessário)
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
[Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Java\jdk-21", "User")
```

### Fase 2: Node.js

```powershell
# 1. Instalar Node 20.11.0+
# Download: https://nodejs.org/

# 2. Verificar
node -v  # v20.11.0+
npm -v   # 10.2.0+

# OU usar NVM (recomendado)
choco install nvm
nvm install 20.11.0
nvm use 20.11.0
```

### Fase 3: VS Code Extensions

Abra VS Code e execute:
```
Ctrl+Shift+P → Extensions: Show Recommended Extensions
```

**Essenciais:**
- [ ] Extension Pack for Java (vscjava)
- [ ] Spring Boot Dashboard (vscjava)
- [ ] Gradle for Java (vscjava)
- [ ] Prettier - Code formatter (esbenp)
- [ ] ESLint (dbaeumer)
- [ ] GitHub Copilot (github)
- [ ] GitLens (eamodio)

### Fase 4: Preparar Ambiente

```bash
# 1. Copiar arquivo de exemplo
cp .env.example .env

# 2. Editar .env
code .env

# Valores mínimos:
# PORT=8080
# JWT_SECRET=sua_chave_super_secreta_minimo_32_caracteres
# DB_HOST=localhost
# DB_PASSWORD=postgres
```

### Fase 5: Build Backend

```powershell
# Windows
.\gradlew.bat build

# macOS/Linux
./gradlew build

# Ou pular testes para mais rápido
./gradlew build -x test
```

Isso vai:
- ✅ Download de todas as dependências
- ✅ Compilar código Java
- ✅ Executar testes
- ✅ Criar JAR em `build/libs/`

### Fase 6: Setup Frontend (se existir)

```bash
# Se já existe pasta pillmind-mobile
cd pillmind-mobile
npm install

# Se não existe, criar novo
npx create-expo-app pillmind-mobile
cd pillmind-mobile
npm install
cd ..
```

### Fase 7: Executar Projeto

**Terminal 1 (Backend):**
```powershell
# Option A: Gradle run
.\gradlew.bat run

# Option B: VS Code Debug (F5)
# Abra qualquer arquivo .java e pressione F5
```

**Terminal 2 (Frontend):**
```bash
cd pillmind-mobile
npm start

# Abra Expo em:
# - Web: http://localhost:19000
# - Mobile: Escanear QR com câmera do celular
```

**Acessar Backend:**
- API: http://localhost:8080
- Health: http://localhost:8080/api/health

---

## 🔧 Comandos Essenciais

### Build & Run

```bash
# Backend - Build
./gradlew build

# Backend - Build sem testes
./gradlew build -x test

# Backend - Run desenvolvimento
./gradlew run

# Backend - Run contínuo (reload automático)
./gradlew run --continuous

# Backend - Debug
./gradlew run --debug

# Frontend - Install
npm install

# Frontend - Start
npm start

# Frontend - Build
npm run build
```

### Testes

```bash
# Backend - Todos
./gradlew test

# Backend - Específico
./gradlew test --tests com.pillmind.data.usecases.CreateUserUseCaseTest

# Frontend
npm test
```

### Desenvolvimento

```bash
# Java - Hot reload
./gradlew run --continuous

# Frontend - Hot reload
npm start

# Lint JavaScript
npm run lint

# Format código
npm run format
```

---

## 🐛 Debug

### Backend (Java) - F5

1. Abra arquivo Java
2. Clique à esquerda do número da linha para adicionar breakpoint
3. Pressione `F5` ou `Debug → Start Debugging`
4. Controle com:
   - `F10` = Próxima linha
   - `F11` = Entrar em função
   - `Shift+F11` = Sair de função
   - `Ctrl+Shift+D` = Debug view

### Frontend (React Native)

```bash
# No terminal npm start:
m     # Menu Expo
d     # DevTools
w     # Web preview

# React DevTools
npm install -g react-devtools
react-devtools
```

---

## 📁 Estrutura Final

```
pillmind-backend/
├── .vscode/
│   ├── settings.json       # ✅ Configurado
│   ├── launch.json         # ✅ Configurado
│   ├── tasks.json          # ✅ Configurado
│   └── extensions.json     # ✅ Configurado
├── src/main/java/com/pillmind/
│   ├── Main.java           # Entry point
│   ├── data/               # Data layer
│   ├── domain/             # Domain layer
│   ├── infra/              # Infrastructure
│   ├── main/               # Composition
│   └── presentation/       # Controllers
├── .vscode/
├── .editorconfig           # ✅ Configurado
├── .env.example            # ✅ Template
├── .env                    # TODO: Copiar e editar
├── .gitignore              # ✅ Configurado
├── .nvmrc                  # ✅ Node 20.11.0
├── .prettierrc             # ✅ Configurado
├── build.gradle            # ✅ Java 21
├── gradlew                 # ✅ Gradle wrapper
├── gradlew.bat             # ✅ Gradle wrapper Windows
├── setup.bat               # ✅ Auto setup Windows
├── setup.sh                # ✅ Auto setup Unix
├── QUICK_SETUP.md          # ✅ Leia primeiro!
├── DEV_SETUP.md            # ✅ Detalhado
├── SETUP_CHECKLIST.md      # ✅ Checklist
└── SETUP_STATUS.md         # ✅ Este arquivo

pillmind-mobile/
├── src/
├── app.json                # Expo config
├── package.json            # TODO: npm install
└── ...
```

---

## ✨ Features Configurados

### Java 21 ✅
- Target compatibility Java 21
- Compiler args otimizados
- Main class configurado

### Javalin ✅
- Web framework configurado
- CORS enabled
- Health check endpoint

### Database ✅
- PostgreSQL driver
- Hibernate 6.3.1
- HikariCP connection pool
- Flyway migrations

### Security ✅
- JWT (JSON Web Tokens)
- BCrypt password hashing
- CORS configuration

### Testing ✅
- JUnit 5
- Mockito
- Javalin test tools

### Frontend Ready ✅
- Node.js 20.11.0
- React Native / Expo
- Jest testing
- Prettier formatting

---

## 🚀 Modo Automático

### Windows
```powershell
.\setup.bat
```

### macOS/Linux
```bash
chmod +x setup.sh
./setup.sh
```

Isso irá executar automaticamente:
1. ✅ Verificar Java 21
2. ✅ Verificar Node.js
3. ✅ Build backend
4. ✅ Install frontend

---

## 📚 Documentação

| Arquivo | Propósito |
|---------|-----------|
| `QUICK_SETUP.md` | 🚀 Guia rápido (COMECE AQUI) |
| `DEV_SETUP.md` | 📖 Documentação completa |
| `SETUP_CHECKLIST.md` | ✅ Checklist interativo |
| `SETUP_STATUS.md` | 📋 Status atual (este arquivo) |
| `API_ROUTES.md` | 🔌 Rotas da API |
| `ARCHITECTURE.md` | 🏗️ Arquitetura do projeto |
| `README.md` | ℹ️ Info geral |

---

## ❌ Possíveis Problemas & Soluções

| Problema | Solução |
|----------|---------|
| Java not found | Instalar JDK 21, configurar JAVA_HOME |
| Gradle build fails | `./gradlew clean && ./gradlew build -x test` |
| Porta 8080 em uso | `Get-NetTCPConnection -LocalPort 8080 \| Stop-Process -Force` |
| npm install falha | `rm -r node_modules && npm cache clean --force && npm install` |
| Node version mismatch | `nvm use 20.11.0` (se usando NVM) |
| VS Code extensions não instalam | Abrir Command Palette e instalar manualmente |

---

## 🎓 Learning Resources

- [Javalin Docs](https://javalin.io/)
- [Java 21 Features](https://openjdk.java.net/projects/jdk/21/)
- [React Native](https://reactnative.dev/)
- [Gradle Guide](https://gradle.org/guide/)
- [VS Code Tips](https://code.visualstudio.com/tips-and-tricks)

---

## 📝 Seu Setup está Pronto!

Agora é só:

1. Executar `setup.bat` (Windows) ou `setup.sh` (Unix)
2. Editar `.env` com suas configurações
3. `./gradlew run` (backend)
4. `npm start` (frontend)
5. Começar a programar! 🚀

**Happy Coding! ❤️**

---

**Generated**: 2026-01-09  
**Updated**: Conforme necessário
