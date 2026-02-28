# PillMind API - Rotas e Casos de Uso

## 📋 Sumário

Esta API suporta todas as funcionalidades do aplicativo PillMind conforme o fluxo de telas fornecido.

## 🔐 Autenticação

### POST /api/signup

Cadastro de novo usuário

```json
{
  "name": "João Silva",
  "email": "joao@example.com",
  "password": "senha123",
  "passwordConfirmation": "senha123"
}
```

### POST /api/login

Login de usuário

```json
{
  "email": "joao@example.com",
  "password": "senha123"
}
```

**Response:**

```json
{
  "accessToken": "eyJhbGciOiJIUzI1...",
  "name": "João Silva"
}
```

---

## 💊 Medicamentos (Medicine)

### POST /api/medicines

Adicionar novo medicamento

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

### GET /api/medicines

Listar todos os medicamentos do usuário
**Headers:** `x-access-token: {token}`

### GET /api/medicines/:id

Obter detalhes de um medicamento específico

### PUT /api/medicines/:id

Editar medicamento

```json
{
  "name": "Paracetamol",
  "dosage": "750mg",
  "frequency": "A cada 6 horas",
  "instructions": "Tomar com água",
  "startDate": "2026-01-07T08:00:00",
  "endDate": "2026-01-17T08:00:00",
  "active": true
}
```

### DELETE /api/medicines/:id

Deletar medicamento

---

## ⏰ Lembretes (Reminders)

### POST /api/reminders

Criar lembrete para medicamento

```json
{
  "medicineId": "abc123",
  "times": ["08:00", "16:00", "00:00"],
  "daysOfWeek": ["MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"]
}
```

### GET /api/reminders

Listar lembretes do usuário

### GET /api/reminders/medicine/:medicineId

Listar lembretes de um medicamento específico

### PUT /api/reminders/:id

Editar lembrete

### DELETE /api/reminders/:id

Deletar lembrete

---

## 📅 Consultas (Appointments)

### POST /api/appointments

Agendar nova consulta

```json
{
  "doctorName": "Dr. João Cardiologista",
  "specialty": "Cardiologia",
  "location": "Hospital São Lucas - Sala 305",
  "dateTime": "2026-01-15T14:30:00",
  "notes": "Levar exames anteriores"
}
```

### GET /api/appointments

Listar todas as consultas do usuário

### GET /api/appointments/:id

Obter detalhes de uma consulta

### PUT /api/appointments/:id

Editar consulta

```json
{
  "doctorName": "Dr. João Cardiologista",
  "specialty": "Cardiologia",
  "location": "Hospital São Lucas - Sala 305",
  "dateTime": "2026-01-15T15:00:00",
  "notes": "Levar exames de sangue",
  "status": "SCHEDULED"
}
```

### DELETE /api/appointments/:id

Cancelar consulta

---

## 👨‍👩‍👧 Controle Parental (Parental Control)

### POST /api/parental/generate-code

Gerar código parental para compartilhar com conta monitorada
**Response:**

```json
{
  "code": "ABC123",
  "expiresAt": "2026-01-08T00:00:00"
}
```

### POST /api/parental/link

Vincular conta de filho usando código parental

```json
{
  "parentalCode": "ABC123"
}
```

### GET /api/parental/monitored-accounts

Listar contas monitoradas (para pais)

### GET /api/parental/monitored-accounts/:childId/medicines

Listar medicamentos de uma conta monitorada

### GET /api/parental/monitored-accounts/:childId/appointments

Listar consultas de uma conta monitorada

### POST /api/parental/monitored-accounts/:childId/medicines

Adicionar medicamento para conta monitorada (pai adicionando para filho)

### POST /api/parental/monitored-accounts/:childId/appointments

Agendar consulta para conta monitorada

### DELETE /api/parental/unlink/:childId

Desvincular conta monitorada

---

## 📍 Serviços Próximos (Nearby Services)

### GET /api/nearby/hospitals

Buscar hospitais próximos
**Query params:**

- `latitude`: Latitude atual
- `longitude`: Longitude atual
- `radius`: Raio de busca em km (default: 5)

**Response:**

```json
{
  "services": [
    {
      "id": "1",
      "name": "Hospital São Lucas",
      "type": "HOSPITAL",
      "address": "Rua das Flores, 123",
      "latitude": -23.55052,
      "longitude": -46.633308,
      "phone": "(11) 1234-5678",
      "openingHours": "24 horas",
      "distance": 1.2
    }
  ]
}
```

### GET /api/nearby/clinics

Buscar clínicas próximas
(mesmos parâmetros)

### GET /api/nearby/pharmacies

Buscar farmácias próximas
(mesmos parâmetros)

### GET /api/nearby/all

Buscar todos os serviços próximos
(mesmos parâmetros)

---

## 👤 Perfil e Conta (Profile & Account)

### GET /api/profile

Obter dados do perfil do usuário
**Headers:** `x-access-token: {token}`

**Response:**

```json
{
  "id": "user123",
  "name": "João Silva",
  "email": "joao@example.com",
  "dateOfBirth": "1990-05-15",
  "gender": "MALE",
  "pictureUrl": "https://example.com/avatar.jpg",
  "emailVerified": true,
  "updatedAt": "2026-01-01T00:00:00"
}
```

### PUT /api/profile

Atualizar perfil do usuário

```json
{
  "name": "João Silva Santos",
  "email": "joao.santos@example.com",
  "dateOfBirth": "1990-05-15",
  "gender": "MALE",
  "pictureUrl": "https://example.com/avatar-updated.jpg"
}
```

**Campos disponíveis:**
- `name` - Nome completo do usuário
- `email` - Email do usuário  
- `dateOfBirth` - Data de nascimento (formato: YYYY-MM-DD)
- `gender` - Gênero (MALE, FEMALE, OTHER, PREFER_NOT_TO_SAY)
- `pictureUrl` - URL da foto de perfil/avatar

**Todos os campos são opcionais** - apenas os campos enviados serão atualizados.

### POST /api/feedback

Enviar feedback sobre o aplicativo

```json
{
  "message": "Ótimo aplicativo, muito útil!",
  "rating": 5
}
```

### GET /api/helpline

Obter informações de helpline/suporte
**Response:**

```json
{
  "phone": "0800-123-4567",
  "email": "suporte@pillmind.com",
  "whatsapp": "+55 11 98765-4321"
}
```

---

## 🏥 Casos de Uso Implementados

### Autenticação

- [x] AddAccount - Cadastro de usuário
- [x] Authentication - Login

### Medicamentos

- [x] AddMedicine - Adicionar medicamento
- [x] ListMedicines - Listar medicamentos
- [x] UpdateMedicine - Editar medicamento
- [x] DeleteMedicine - Deletar medicamento

### Lembretes

- [x] SetReminder - Criar lembrete
- [x] ListReminders - Listar lembretes
- [x] UpdateReminder - Editar lembrete
- [x] DeleteReminder - Deletar lembrete

### Consultas

- [x] AddAppointment - Agendar consulta
- [x] ListAppointments - Listar consultas
- [x] UpdateAppointment - Editar consulta
- [x] DeleteAppointment - Cancelar consulta

### Controle Parental

- [x] GenerateParentalCode - Gerar código parental
- [x] LinkMonitoredAccount - Vincular conta monitorada
- [x] ListMonitoredAccounts - Listar contas monitoradas
- [x] UnlinkMonitoredAccount - Desvincular conta

### Serviços Próximos

- [x] FindNearbyServices - Buscar serviços próximos (hospitais, clínicas, farmácias)

### Perfil

- [x] UpdateProfile - Atualizar perfil
- [x] GetProfile - Obter perfil
- [x] SubmitFeedback - Enviar feedback

---

## 🔒 Autenticação e Autorização

Todas as rotas exceto `/api/signup`, `/api/login` e `/api/health` requerem autenticação via token JWT.

**Header obrigatório:**

```
x-access-token: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## 📊 Status Codes

- `200` - OK
- `201` - Created
- `204` - No Content
- `400` - Bad Request
- `401` - Unauthorized
- `403` - Forbidden
- `404` - Not Found
- `500` - Internal Server Error

---

## 🧪 Testes com cURL

### Cadastro

```bash
curl -X POST http://localhost:7000/api/signup \
  -H "Content-Type: application/json" \
  -d '{
    "name": "João Silva",
    "email": "joao@example.com",
    "password": "senha123",
    "passwordConfirmation": "senha123"
  }'
```

### Adicionar Medicamento

```bash
curl -X POST http://localhost:7000/api/medicines \
  -H "Content-Type: application/json" \
  -H "x-access-token: {seu-token}" \
  -d '{
    "name": "Paracetamol",
    "dosage": "500mg",
    "frequency": "A cada 8 horas",
    "instructions": "Tomar após as refeições",
    "startDate": "2026-01-07T08:00:00",
    "endDate": "2026-01-17T08:00:00"
  }'
```

### Buscar Hospitais Próximos

```bash
curl "http://localhost:7000/api/nearby/hospitals?latitude=-23.550520&longitude=-46.633308&radius=5" \
  -H "x-access-token: {seu-token}"
```

---

## 📝 Notas de Implementação

### Próximos Passos

1. Implementar middleware de autenticação JWT
2. Implementar repositories in-memory para testes
3. Adicionar validações nos controllers
4. Implementar busca de serviços próximos (integração com API externa ou banco de dados)
5. Adicionar paginação em listagens
6. Implementar notificações push para lembretes
7. Adicionar testes unitários para todos os casos de uso

### Melhorias Futuras

- [ ] Upload de imagem de perfil
- [ ] Histórico de medicamentos tomados
- [ ] Relatórios de adesão ao tratamento
- [ ] Compartilhamento de relatórios com médicos
- [ ] Integração com calendário do dispositivo
- [ ] Suporte a múltiplos idiomas
- [ ] Dark mode (configuração do perfil)
