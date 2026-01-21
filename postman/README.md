# PillMind API - Coleções do Postman

Este diretório contém as coleções do Postman para testar a API do PillMind. Todas as coleções são organizadas por funcionalidade e incluem exemplos de requisições completos.

## 📦 Coleções Disponíveis

### 1. **Authentication** - Autenticação
- **Arquivo:** `PillMind-API-Authentication.postman_collection.json`
- **Endpoints:**
  - Cadastro de usuário (`POST /api/signup`)
  - Login (`POST /api/login`) - Salva automaticamente o token
  - Health check (`GET /api/health`)

### 2. **Medicines** - Medicamentos
- **Arquivo:** `PillMind-API-Medicines.postman_collection.json`
- **Endpoints:**
  - Adicionar medicamento
  - Listar medicamentos
  - Obter medicamento por ID
  - Editar medicamento
  - Deletar medicamento

### 3. **Reminders** - Lembretes
- **Arquivo:** `PillMind-API-Reminders.postman_collection.json`
- **Endpoints:**
  - Criar lembrete
  - Listar lembretes
  - Listar lembretes por medicamento
  - Editar lembrete
  - Deletar lembrete

### 4. **Appointments** - Consultas
- **Arquivo:** `PillMind-API-Appointments.postman_collection.json`
- **Endpoints:**
  - Agendar consulta
  - Listar consultas
  - Obter consulta por ID
  - Editar consulta
  - Cancelar consulta

### 5. **Parental Control** - Controle Parental
- **Arquivo:** `PillMind-API-Parental-Control.postman_collection.json`
- **Endpoints:**
  - Gerar código parental
  - Vincular conta monitorada
  - Listar contas monitoradas
  - Gerenciar medicamentos de filhos
  - Gerenciar consultas de filhos
  - Desvincular conta

### 6. **Nearby Services** - Serviços Próximos
- **Arquivo:** `PillMind-API-Nearby-Services.postman_collection.json`
- **Endpoints:**
  - Buscar hospitais próximos
  - Buscar clínicas próximas
  - Buscar farmácias próximas
  - Buscar todos os serviços

### 7. **Profile & Account** - Perfil e Conta
- **Arquivo:** `PillMind-API-Profile-Account.postman_collection.json`
- **Endpoints:**
  - Obter perfil
  - Atualizar perfil
  - Enviar feedback
  - Obter informações de suporte

## 🚀 Como Importar no Postman

### Método 1: Importar Arquivo por Arquivo
1. Abra o Postman
2. Clique em **Import** (botão no canto superior esquerdo)
3. Arraste e solte um dos arquivos `.json` ou clique em **Upload Files**
4. Selecione o arquivo da coleção desejada
5. Clique em **Import**

### Método 2: Importar Todas de Uma Vez
1. Abra o Postman
2. Clique em **Import**
3. Selecione **Folder** e escolha a pasta `postman/`
4. Todas as coleções serão importadas automaticamente

## ⚙️ Configuração Inicial

### Variáveis de Ambiente
Cada coleção possui variáveis configuradas:

- **`baseUrl`**: `http://192.168.1.7:7000` (URL base da API - acessível na rede local)
- **`accessToken`**: Token JWT (preenchido automaticamente após login)

### Para alterar a URL da API:
1. Selecione uma coleção
2. Vá em **Variables**
3. Altere o valor de `baseUrl` para sua URL

### 🌐 Acesso de Outros Computadores
As coleções estão configuradas para usar o IP da rede local (`192.168.1.7`), permitindo que outros dispositivos na mesma rede acessem a API:
- **Mesma rede WiFi**: Outros computadores, tablets ou smartphones
- **Rede corporativa**: Colegas de trabalho podem testar a API
- **Desenvolvimento em equipe**: Múltiplos desenvolvedores podem usar as mesmas coleções

## 🔑 Autenticação

### Passo a Passo:
1. **Importe a coleção Authentication primeiro**
2. Execute `Signup - Cadastro de Usuário` para criar uma conta
3. Execute `Login - Autenticação` para fazer login
   - ✅ **O token será salvo automaticamente** na variável `accessToken`
4. Agora você pode usar qualquer endpoint das outras coleções

### Token Automático
O endpoint de login possui um script que salva automaticamente o token retornado:
```javascript
if (pm.response.code === 200) {
    const responseJson = pm.response.json();
    pm.collectionVariables.set('accessToken', responseJson.accessToken);
}
```

## 📋 Fluxo de Teste Sugerido

### 1. Primeiro Acesso
1. **Authentication** → Signup
2. **Authentication** → Login (salva token)
3. **Medicines** → Adicionar Medicamento
4. **Reminders** → Criar Lembrete
5. **Appointments** → Agendar Consulta

### 2. Funcionalidades Avançadas
1. **Profile** → Obter/Atualizar Perfil
2. **Nearby Services** → Buscar Serviços
3. **Parental Control** → Gerar Código (se aplicável)

### 3. Testes CRUD Completos
Para cada recurso (Medicines, Appointments, etc.):
1. **POST** → Criar
2. **GET** → Listar todos
3. **GET/:id** → Obter específico
4. **PUT/:id** → Editar
5. **DELETE/:id** → Deletar

## 🌍 Ambientes

### Desenvolvimento Local (Rede)
```
baseUrl: http://192.168.1.7:7000
```
✅ **Configuração atual** - Acessível de outros computadores na rede

### Apenas Local (se necessário)
```
baseUrl: http://localhost:7000
```

### Staging/Produção
```
baseUrl: https://pillmind.192.168.1.7.nip.io
```

## 📡 Configuração de Rede

### Requisitos para Acesso Externo
1. **Backend rodando**: Certifique-se que a API está executando (`./gradlew run`)
2. **Firewall**: Porta 7000 deve estar liberada
3. **Mesma rede**: Dispositivos devem estar na mesma rede WiFi/LAN

### Testando Conectividade
De outro computador, teste se consegue acessar:
```bash
# Teste básico de conexão
curl http://192.168.1.7:7000/api/health

# Ou abra no navegador
http://192.168.1.7:7000/api/health
```

### Compartilhando com a Equipe
1. Compartilhe os arquivos `.json` das coleções
2. Instrua para importar no Postman
3. A URL `http://192.168.1.7:7000` já estará configurada
4. Todos na mesma rede poderão testar a API

## 📝 Exemplos de Dados

### Medicamento
```json
{
  "name": "Paracetamol",
  "dosage": "500mg",
  "frequency": "A cada 8 horas",
  "instructions": "Tomar após as refeições",
  "startDate": "2026-01-07T08:00:00",
  "endDate": "2026-01-17T08:00:00"
}
```

### Consulta
```json
{
  "doctorName": "Dr. João Cardiologista",
  "specialty": "Cardiologia",
  "location": "Hospital São Lucas - Sala 305",
  "dateTime": "2026-01-15T14:30:00",
  "notes": "Levar exames anteriores"
}
```

## 🔧 Funcionalidades Especiais

### Salvamento Automático de IDs
Muitos endpoints salvam automaticamente IDs retornados:
- **Medicine ID** (após criar medicamento)
- **Appointment ID** (após criar consulta)
- **Reminder ID** (após criar lembrete)
- **Parental Code** (após gerar código)

### Scripts Pós-Execução
Várias requisições incluem scripts que:
- Salvam tokens de autenticação
- Extraem e armazenam IDs de recursos criados
- Facilitam o fluxo de testes em sequência

## ❓ Troubleshooting

### Erro 401 (Unauthorized)
- Certifique-se de que fez login primeiro
- Verifique se o token foi salvo nas variáveis da coleção
- Token pode ter expirado - faça login novamente

### Erro 404 (Not Found)
- Verifique se a URL base está correta
- Certifique-se de que a API está rodando
- Verifique se os IDs utilizados existem

### Erro de Conexão
- Verifique se a API está rodando (`./gradlew run`)
- Confirme a porta (padrão: 7000)
- Teste o health check primeiro

## 📞 Suporte

Para problemas com as coleções:
1. Verifique se a API está rodando
2. Confirme as variáveis de ambiente
3. Teste o fluxo de autenticação primeiro
4. Consulte a documentação da API em `docs/API_ROUTES.md`