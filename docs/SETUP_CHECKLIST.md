# Setup Checklist - PillMind Developer Environment

## Pre-requisitos Instalados
- [ ] **Java 21** (JDK)
  - Verificar: `java -version`
  - Path: `C:\Program Files\Java\jdk-21` (ou similar)
  - Variável `JAVA_HOME` configurada

- [ ] **Node.js 20.11.0+**
  - Verificar: `node -v` e `npm -v`
  - Recomendado: NVM (Node Version Manager)

- [ ] **Git**
  - Verificar: `git --version`
  - Configurado: `git config --global user.name` e `git config --global user.email`

---

## Configuração do Workspace

### VS Code Extensions
- [ ] Extension Pack for Java (RedHat)
- [ ] Spring Boot Dashboard (vscjava)
- [ ] Gradle for Java (vscjava)
- [ ] Prettier - Code Formatter
- [ ] ESLint
- [ ] GitHub Copilot
- [ ] GitLens

**Como instalar:**
```
Ctrl+Shift+P -> Extensions: Show Recommended Extensions
```

### Arquivos de Configuração Criados
- [x] `.vscode/settings.json` - Configurações do workspace
- [x] `.vscode/launch.json` - Configurações de debug
- [x] `.vscode/tasks.json` - Tasks de build/run
- [x] `.vscode/extensions.json` - Extensões recomendadas
- [x] `.env.example` - Template de variáveis de ambiente
- [x] `.nvmrc` - Versão do Node.js
- [x] `DEV_SETUP.md` - Guia completo de setup
- [x] `setup.bat` / `setup.sh` - Script de configuração automática

---

## Backend Setup (Java/Javalin)

### Dependências Gradle
- [ ] Javalin 6.0.0
- [ ] PostgreSQL Driver
- [ ] Hibernate 6.3.1
- [ ] Jackson (JSON)
- [ ] JWT (JSON Web Tokens)
- [ ] Spring Boot DevTools
- [ ] JUnit 5 (Tests)

### Compilar
```bash
./gradlew build
```

### Executar
```bash
./gradlew run
```

### Testar
```bash
./gradlew test
```

---

## Frontend Setup (React Native/Expo)

### Criar Projeto (se necessário)
```bash
npx create-expo-app pillmind-mobile
# ou
npx react-native init pillmind-mobile
```

### Dependências NPM
- [ ] react-native
- [ ] expo (ou react-native-cli)
- [ ] @react-navigation
- [ ] axios (HTTP client)
- [ ] zustand ou Redux (state management)
- [ ] jest (testing)

### Instalar
```bash
cd pillmind-mobile
npm install
```

### Executar
```bash
npm start
```

---

## Variáveis de Ambiente

### Backend (.env)
```bash
cp .env.example .env
# Editar valores:
```

Mínimo necessário:
- [ ] `PORT=8080`
- [ ] `JWT_SECRET=your-secret-key`
- [ ] `DB_HOST=localhost`
- [ ] `DB_PASSWORD=postgres`

---

## Database Setup

### PostgreSQL Local
```bash
# Verificar se está rodando
psql --version

# Conectar (Windows)
psql -U postgres

# Criar banco
CREATE DATABASE pillmind;
CREATE USER pillmind_user WITH PASSWORD 'password';
GRANT ALL PRIVILEGES ON DATABASE pillmind TO pillmind_user;
```

### Migrations
```bash
./gradlew flywayMigrate
```

---

## Debug & Testing

### Backend Debug
- [ ] Abrir arquivo Java
- [ ] Pressionar `F5` para iniciar debug
- [ ] Adicionar breakpoints clicando na linha

### Frontend Debug
- [ ] Usar Expo DevTools: `m` (no terminal) ou `http://localhost:19002`
- [ ] React Native Debugger: [Download](https://github.com/jhen0409/react-native-debugger)

### Testes
```bash
# Backend
./gradlew test

# Frontend
npm test
```

---

## Atalhos Úteis

### Java/IDE
| Atalho | Ação |
|--------|------|
| `F5` | Start Debug |
| `F9` | Toggle Breakpoint |
| `Ctrl+Shift+D` | Debug View |
| `Ctrl+Shift+T` | Run Tests |
| `Alt+Enter` | Quick Fix |
| `Ctrl+/` | Toggle Comment |

### VS Code General
| Atalho | Ação |
|--------|------|
| `Ctrl+Shift+P` | Command Palette |
| `Ctrl+K Ctrl+S` | Keyboard Shortcuts |
| `Ctrl+J` | Toggle Terminal |
| `Ctrl+L` | Select Line |

---

## Troubleshooting

### Problema: Gradle build falha
**Solução:**
```bash
./gradlew clean
./gradlew build -x test
```

### Problema: Java não encontrado
**Solução:**
```powershell
# Windows - Definir JAVA_HOME
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
[Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Java\jdk-21", "User")

# Verificar
echo $env:JAVA_HOME
```

### Problema: Porta 8080 em uso
**Solução:**
```powershell
# Windows
Get-NetTCPConnection -LocalPort 8080 | Stop-Process -Force

# Ou mudar porta no .env
PORT=3001
```

### Problema: npm install falha
**Solução:**
```bash
rm -r node_modules package-lock.json
npm cache clean --force
npm install
```

---

## Próximos Passos

1. ✅ Instalar Java 21 e Node.js
2. ✅ Clonar repositório
3. ✅ Executar `setup.bat` (Windows) ou `setup.sh` (Unix)
4. ✅ Configurar variáveis de ambiente (`.env`)
5. ✅ Instalar extensões VS Code
6. ✅ Compilar backend: `./gradlew build`
7. ✅ Instalar frontend: `npm install`
8. ✅ Executar: `./gradlew run` + `npm start`

---

## Recursos

- 📖 [Javalin Docs](https://javalin.io/)
- 📖 [React Native Docs](https://reactnative.dev/)
- 📖 [Java 21 Features](https://openjdk.java.net/projects/jdk/21/)
- 📖 [Gradle User Guide](https://gradle.org/guide/)
- 📖 [VS Code Tips](https://code.visualstudio.com/tips-and-tricks)

---

**Data de Última Atualização:** 2026-01-09
**Versão:** 1.0.0
