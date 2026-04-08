# 📦 VS Code Extensions Guide

## Como Instalar as Extensões Recomendadas

### Método 1: Automático (Recomendado)

Abra VS Code neste workspace e execute:

```
Ctrl+Shift+P → Extensions: Show Recommended Extensions
```

VS Code mostrará todas as extensões em `extensions.json` com um botão "Install" para cada uma.

### Método 2: Manual (Um a um)

```
Ctrl+Shift+P → Extensions: Install Extensions
```

Busque pelo nome ou ID (entre parênteses) e instale.

### Método 3: Command Line

```powershell
# Windows
code --install-extension redhat.java
code --install-extension vscjava.vscode-spring-boot-dashboard
code --install-extension vscjava.vscode-gradle
code --install-extension gabrielbb.vscode-lombok
code --install-extension esbenp.prettier-vscode
code --install-extension dbaeumer.vscode-eslint
# ... continue com as demais
```

---

## 🔧 Extensões por Categoria

### Java & Backend Development

#### 1. **Extension Pack for Java** (vscjava.extension-pack-for-java)
- 👑 **ESSENCIAL** - Pacote completo de desenvolvimento Java
- Inclui: Language Support, Debugger, Test Runner, Maven, VS Code Coding Pack
- Features: IntelliSense, debugging, testing integrado

#### 2. **Spring Boot Dashboard** (vscjava.vscode-spring-boot-dashboard)
- ⭐ Gerenciar aplicações Spring Boot
- Visualizar status de projetos
- Iniciar/parar services rapidamente

#### 3. **Gradle for Java** (vscjava.vscode-gradle)
- ✅ Integração com Gradle
- Run/debug Gradle tasks
- Project explorer

#### 4. **Lombok** (gabrielbb.vscode-lombok)
- 🔧 Suporte para Project Lombok
- Auto-complete para anotações
- Geração automática de getters/setters

---

### JavaScript/TypeScript & Frontend

#### 5. **Prettier - Code Formatter** (esbenp.prettier-vscode)
- 💅 **ESSENCIAL** - Formatter de código
- Suporta: JS, TS, JSX, CSS, JSON, Markdown
- Configurado para print width 100, single quotes, etc

#### 6. **ESLint** (dbaeumer.vscode-eslint)
- 🔍 Linter para JavaScript/TypeScript
- Detecta erros e padrões
- Auto-fix com Ctrl+Alt+F

#### 7. **TypeScript Vue Plugin** (ms-vscode.vscode-typescript-next)
- 📜 Suporte avançado TypeScript
- Vue support (se usar Vue)

#### 8. **JavaScript (ES6) code snippets** (xabikos.JavaScriptSnippets)
- ⚡ Snippets úteis para JS
- Arrow functions, classes, promises, etc

---

### Version Control

#### 9. **GitLens** (eamodio.gitlens)
- 📊 Git integrado avançado
- Blame, history, diff view
- Timeline e commits details

#### 10. **GitHub Copilot** (github.copilot)
- 🤖 **RECOMENDADO** - IA code suggestions
- Context-aware completions
- Requer conta GitHub

#### 11. **GitHub Copilot Chat** (github.copilot-chat)
- 💬 Chat com IA sobre código
- Gerar código, explicar funções

---

### Utilities & Helpers

#### 12. **Import Cost** (wix.vscode-import-cost)
- 📦 Mostrar tamanho dos imports
- Ajuda otimizar bundle size

#### 13. **Version Lens** (pflannery.vscode-versionlens)
- 🔗 Mostrar versões de dependências
- Links para documentação

#### 14. **Code Runner** (formulahendry.code-runner)
- ▶️ Executar código rapidamente
- Suporta múltiplas linguagens

#### 15. **DotENV** (mikestead.dotenv)
- 📝 Syntax highlighting para .env

---

### Productivity

#### 16. **Remote Containers** (ms-vscode-remote.remote-containers)
- 🐳 Develop inside Docker containers
- Isolate dependencies

#### 17. **Remote WSL** (ms-vscode-remote.remote-wsl)
- 🐧 Develop in Windows Subsystem for Linux
- Best of both worlds

---

### Appearance & Themes

#### 18. **GitHub Theme** (github.github-vscode-theme)
- 🎨 Clean theme based on GitHub
- Light & Dark variants

#### 19. **Catppuccin for VSCode** (catppuccin.catppuccin-vsc)
- 🎨 Beautiful pastel theme
- Warm & cozy colors

---

## 🚀 Extensões Mais Importantes (Prioridade)

### Não Instale Sem Elas (CRÍTICO)
1. ✅ **Extension Pack for Java** - Desenvolvimento Java
2. ✅ **Prettier** - Formatação código
3. ✅ **ESLint** - Linting JavaScript
4. ✅ **Gradle for Java** - Build system

### Muito Recomendadas (IMPORTANTE)
5. ✅ **Spring Boot Dashboard** - Gerenciar apps
6. ✅ **GitLens** - Integração Git avançada
7. ✅ **GitHub Copilot** - IA suggestions
8. ✅ **Lombok** - Reduz boilerplate Java

### Úteis (OPCIONAL)
- Import Cost
- Code Runner
- Version Lens
- DotENV

---

## ⚙️ Configurações Pós-Instalação

### Prettier
Já configurado em `.prettierrc`:
- Semi: true (ponto-e-vírgula)
- Single Quote: true
- Trailing Comma: es5
- Print Width: 100
- Format on Save: true ✅

### ESLint
Configurado em `.vscode/settings.json`:
- Auto-validate JS/TS/JSX/TSX
- Format on save
- Auto-fix on save

### Java
Configurado em `.vscode/settings.json`:
- Java 21 target
- Organize imports on save
- Null analysis automático

### Spring Boot Dashboard
Pronto para usar, aparecerá:
- Na sidebar esquerda
- Ícone Spring Boot
- Lista projetos Spring Boot

---

## 🔄 Atualizar Extensões

```
Ctrl+Shift+P → Extensions: Update All
```

Ou menu: Extensions → ... → Update All

---

## 🗑️ Desinstalar Extensões

Se precisar remover:

```
Ctrl+Shift+P → Extensions: Uninstall Extension
```

E selecione qual desinstalar.

---

## 📊 Extensões Instaladas vs Recomendadas

Comando para listar o que está instalado:

```powershell
code --list-extensions
```

Compare com `extensions.json` neste projeto.

---

## 🆘 Troubleshooting

### Extensão não aparece
1. Abra Command Palette (`Ctrl+Shift+P`)
2. "Developer: Reload Window"
3. Aguarde recarregar

### Conflitos entre extensões
1. Verifique em Extensions aba "Themes" (apenas 1 ativa)
2. Desative extensões conflitantes
3. Abra Issue se persistir

### Performance ruim
1. Desative extensões não-essenciais
2. `Ctrl+Shift+P` → "Developer: Profile Extensions"
3. Veja qual usa mais recursos

### Prettier vs ESLint conflitam
Já resolvido em `.vscode/settings.json`:
- ESLint valida
- Prettier formata
- Sem conflitos

---

## 📝 Extensão Custom para PillMind (Futuro)

Se quiser criar uma extensão customizada:

```bash
npm install -g yo generator-code
yo code
# Seguir prompts
```

Mas por enquanto, as padrões são suficientes!

---

## ✅ Checklist Final

- [ ] Extension Pack for Java instalado
- [ ] Prettier instalado
- [ ] ESLint instalado
- [ ] GitLens instalado
- [ ] GitHub Copilot (opcional)
- [ ] Prettier formatando ao salvar
- [ ] ESLint linting ao abrir arquivo JS
- [ ] Java IntelliSense funcionando

---

**Última atualização**: 2026-01-09

Se tiver dúvidas sobre qualquer extensão, veja a documentação oficial no VS Code Marketplace.
