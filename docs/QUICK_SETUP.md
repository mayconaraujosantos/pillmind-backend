# 🚀 Setup Developer Completo - PillMind

**Data**: 9 de Janeiro de 2026  
**Stack**: Java 21 (Javalin) + React Native (Expo)  
**IDE**: VS Code

---

## ✅ O que foi Configurado

### 1. **VS Code Workspace Configuration**
- ✅ `.vscode/settings.json` - Configurações otimizadas para Java 21, JavaScript/TypeScript
- ✅ `.vscode/launch.json` - Debug configurations para backend (Java) e frontend (Node)
- ✅ `.vscode/tasks.json` - Build tasks automatizadas (Gradle + npm)
- ✅ `.vscode/extensions.json` - Extensões recomendadas

### 2. **Editor & Code Quality**
- ✅ `.editorconfig` - Configuração uniforme entre IDEs (indent, charset, etc)
- ✅ `.prettierrc` - Formatter padrão (semicolons, single quotes, print width)
- ✅ `.prettierignore` - Exclusões para prettier

### 3. **Git & Project Management**
- ✅ `.gitignore` - Configurado para Java + Node.js + React Native
- ✅ Estrutura de pastas otimizada

### 4. **Environment & Secrets**
- ✅ `.env.example` - Template completo de variáveis de ambiente
- ✅ `.nvmrc` - Versão Node.js (20.11.0)

### 5. **Documentation**
- ✅ `DEV_SETUP.md` - Guia completo de desenvolvimento
- ✅ `SETUP_CHECKLIST.md` - Checklist para validação do setup
- ✅ `setup.bat` - Script automático para Windows
- ✅ `setup.sh` - Script automático para macOS/Linux

---

## 🎯 Próximos Passos

### 1️⃣ Instalar Requisitos

#### **Java 21**
```powershell
# Verificar instalação
java -version

# Se não tiver, baixar em:
# https://www.oracle.com/java/technologies/downloads/#java21
# Ou: https://jdk.java.net/21/

# Configurar JAVA_HOME (Windows)
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
[Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Java\jdk-21", "User")
```

#### **Node.js 20.11.0+**
```powershell
# Verificar instalação
node -v
npm -v

# Se não tiver, baixar em: https://nodejs.org/

# Recomendado: Usar NVM
choco install nvm
nvm install 20.11.0
nvm use 20.11.0
```

### 2️⃣ Instalar Extensões VS Code

Abra o VS Code e execute:
```
Ctrl+Shift+P → Extensions: Show Recommended Extensions
```

Principais extensões:
- **Extension Pack for Java** (RedHat)
- **Spring Boot Dashboard** (vscjava)
- **Gradle for Java** (vscjava)
- **Prettier** - Code Formatter
- **ESLint**
- **GitHub Copilot**
- **GitLens**

### 3️⃣ Configurar Variáveis de Ambiente

```bash
# Copiar template
cp .env.example .env

# Editar com suas configurações
code .env
```

**Variáveis essenciais:**
- `PORT=8080` (porta do servidor)
- `JWT_SECRET=sua-chave-super-secreta` (mínimo 32 caracteres)
- `DB_HOST=localhost` (servidor PostgreSQL)
- `DB_PASSWORD=postgres` (senha do banco)

### 4️⃣ Compilar Backend

```powershell
# Windows
.\gradlew.bat build

# macOS/Linux
./gradlew build
```

Isso vai:
- ✅ Baixar todas as dependências Gradle
- ✅ Compilar o código Java
- ✅ Rodar testes (opcional com `-x test` para pular)

### 5️⃣ Instalar Frontend (se existir)

```bash
# Se já existe a pasta
cd pillmind-mobile
npm install

# Se não existe, criar novo projeto
npx create-expo-app pillmind-mobile
# ou
npx react-native init pillmind-mobile
```

### 6️⃣ Executar o Projeto

**Terminal 1 - Backend:**
```powershell
.\gradlew.bat run
# Ou via VS Code: F5 (Launch Debug)
```

**Terminal 2 - Frontend:**
```bash
cd pillmind-mobile
npm start
```

---

## 🔧 Comandos Úteis

### Backend (Java/Gradle)

```bash
# Build completo
./gradlew build

# Build sem testes
./gradlew build -x test

# Executar aplicação
./gradlew run

# Executar em contínuo (reload automático)
./gradlew run --continuous

# Testes unitários
./gradlew test

# Teste específico
./gradlew test --tests com.pillmind.data.usecases.CreateUserUseCaseTest

# Verificar dependências
./gradlew dependencies

# Limpar cache
./gradlew clean

# Debug
./gradlew run --debug
```

### Frontend (Node.js/npm)

```bash
# Instalar dependências
npm install

# Atualizar dependências
npm update

# Iniciar Expo
npm start

# Build para iOS
npm run ios

# Build para Android
npm run android

# Testes
npm test

# Lint
npm run lint

# Lint com fix automático
npm run lint -- --fix
```

### Git

```bash
# Verificar status
git status

# Adicionar arquivos
git add .

# Commit
git commit -m "message"

# Push
git push origin main

# Pull latest
git pull origin main
```

---

## 🐛 Debugging

### Backend (Java)

1. **Abrir arquivo Java** → `src/main/java/com/pillmind/Main.java`
2. **Adicionar breakpoint** → Clicar à esquerda do número da linha
3. **Iniciar debug** → Pressionar `F5` ou Menu → Run → Start Debugging
4. **Controlar execução**:
   - `F10` - Próxima linha (Step Over)
   - `F11` - Entrar em função (Step Into)
   - `Shift+F11` - Sair de função (Step Out)
   - `F5` - Continuar até próximo breakpoint
   - `Shift+F5` - Parar debug

### Frontend (React Native)

```bash
# 1. No terminal Expo (npm start):
m  # Abre menu do Expo
d  # DevTools WebSockets
w  # Web preview

# 2. Usar React DevTools:
npm install -g react-devtools
react-devtools

# 3. Debugger no Chrome:
chrome://inspect
```

---

## 📁 Estrutura de Pastas

```
pillmind-backend/
├── .vscode/                      # Configurações VS Code
│   ├── settings.json             # Settings do workspace
│   ├── launch.json               # Debug configurations
│   ├── tasks.json                # Build tasks
│   └── extensions.json           # Extensões recomendadas
├── src/
│   ├── main/java/com/pillmind/   # Código fonte
│   │   ├── Main.java             # Entrada da aplicação
│   │   ├── data/                 # Data layer (repositories)
│   │   ├── domain/               # Domain layer (lógica de negócio)
│   │   ├── infra/                # Infrastructure layer
│   │   ├── main/                 # Composição/factories
│   │   └── presentation/         # Controllers/rotas
│   └── test/java/                # Testes
├── build.gradle                  # Configuração Gradle
├── gradlew / gradlew.bat         # Gradle Wrapper
├── .env.example                  # Template .env
├── .gitignore                    # Git ignore
├── .editorconfig                 # Editor settings
├── .prettierrc                   # Prettier config
├── DEV_SETUP.md                  # Documentação completa
├── SETUP_CHECKLIST.md            # Checklist
├── setup.bat / setup.sh          # Scripts automáticos
└── README.md                     # Info geral do projeto

pillmind-mobile/                 # Frontend React Native
├── src/
├── package.json
├── app.json                      # Expo config
└── ...
```

---

## ⚙️ Configurações Importantes

### Java
- **Versão**: OpenJDK 21 ou Oracle JDK 21
- **JAVA_HOME**: Variável de ambiente configurada
- **Charset**: UTF-8

### Node.js
- **Versão**: 20.11.0+ (usar `.nvmrc`)
- **NPM**: 10.2.0+

### Database
- **Engine**: PostgreSQL 14+
- **Host**: localhost:5432
- **Usuário**: postgres (ou custom)
- **Banco**: pillmind

---

## 🚀 Scripts Automáticos

### Windows
```powershell
# Executar script de setup
.\setup.bat

# Isso irá:
# ✅ Verificar Java 21
# ✅ Verificar Node.js
# ✅ Compilar backend (./gradlew build -x test)
# ✅ Instalar frontend (npm install)
```

### macOS/Linux
```bash
# Executar script de setup
chmod +x setup.sh
./setup.sh
```

---

## 📚 Documentação & Links

- [Javalin Documentation](https://javalin.io/)
- [Java 21 Features](https://openjdk.java.net/projects/jdk/21/)
- [React Native Docs](https://reactnative.dev/)
- [Expo Docs](https://docs.expo.dev/)
- [Gradle User Guide](https://gradle.org/guide/)
- [VS Code Tips & Tricks](https://code.visualstudio.com/tips-and-tricks)
- [EditorConfig](https://editorconfig.org/)

---

## ❓ Troubleshooting

### Problema: "Java not found"
```powershell
# Verificar instalação
java -version

# Configurar JAVA_HOME
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
```

### Problema: Gradle build falha
```bash
# Limpar cache
./gradlew clean

# Rebuild
./gradlew build -x test
```

### Problema: Porta 8080 em uso
```powershell
# Verificar processo
Get-NetTCPConnection -LocalPort 8080 | Select-Object -Property *

# Matar processo
Stop-Process -Id <PID> -Force
```

### Problema: npm install falha
```bash
rm -r node_modules package-lock.json
npm cache clean --force
npm install
```

---

## ✨ Dicas Pro

1. **Use tasks do VS Code**: `Ctrl+Shift+B` para build rápido
2. **Prettier on save**: Salva arquivo e formata automaticamente
3. **Git integration**: `Ctrl+Shift+G` para Git view
4. **Terminal splits**: `Ctrl+Shift+\` para dividir terminal
5. **Quick Run**: `Ctrl+F5` para executar último comando
6. **Command Palette**: `Ctrl+Shift+P` é seu melhor amigo

---

## 📝 Próximas Ações Recomendadas

1. ✅ Executar `setup.bat` (Windows) ou `setup.sh` (Unix)
2. ✅ Validar checklist em `SETUP_CHECKLIST.md`
3. ✅ Copiar `.env.example` para `.env` e configurar
4. ✅ Instalar extensões VS Code recomendadas
5. ✅ Compilar backend com `./gradlew build`
6. ✅ Instalar frontend com `npm install`
7. ✅ Iniciar servidor: `./gradlew run`
8. ✅ Iniciar frontend: `npm start`
9. ✅ Acessar em: `http://localhost:8080` (backend) e `localhost:19000` (frontend)

---

**Desenvolvido com ❤️ para PillMind**  
**Last Updated**: 2026-01-09
