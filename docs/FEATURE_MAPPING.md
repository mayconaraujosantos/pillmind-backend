# PillMind - Mapeamento de Features e Casos de Uso

## 📱 Telas → Casos de Uso → Rotas

### 1. Onboarding & Autenticação

#### Telas:

- Splash Screen
- Onboarding
- Sign In / Sign Up

#### Casos de Uso:

- `AddAccount` - Cadastro de nova conta
- `Authentication` - Login de usuário

#### Rotas:

```
POST /api/signup
POST /api/login
```

---

### 2. Homepage - Gerenciamento de Medicamentos

#### Telas:

- Homepage → Add Medicine
- Homepage → Edit Medicine
- Homepage → Delete Medicine
- Add Medicine → Set Reminder

#### Casos de Uso:

- `AddMedicine` - Adicionar novo medicamento
- `ListMedicines` - Listar medicamentos do usuário
- `UpdateMedicine` - Editar medicamento existente
- `DeleteMedicine` - Remover medicamento
- `SetReminder` - Definir lembrete para medicamento
- `ListReminders` - Listar lembretes
- `UpdateReminder` - Editar lembrete
- `DeleteReminder` - Remover lembrete

#### Rotas:

```
POST   /api/medicines
GET    /api/medicines
GET    /api/medicines/:id
PUT    /api/medicines/:id
DELETE /api/medicines/:id

POST   /api/reminders
GET    /api/reminders
GET    /api/reminders/medicine/:medicineId
PUT    /api/reminders/:id
DELETE /api/reminders/:id
```

---

### 3. Appointments - Consultas Médicas

#### Telas:

- Homepage → Appointments
- Appointments → Set Appointment
- Appointments → Edit Appointment
- Appointments → Delete Appointment

#### Casos de Uso:

- `AddAppointment` - Agendar nova consulta
- `ListAppointments` - Listar consultas
- `UpdateAppointment` - Editar consulta
- `DeleteAppointment` - Cancelar consulta

#### Rotas:

```
POST   /api/appointments
GET    /api/appointments
GET    /api/appointments/:id
PUT    /api/appointments/:id
DELETE /api/appointments/:id
```

---

### 4. Parental - Controle Parental

#### Telas:

- Homepage → Parental
- Parental → Scan to add (Código)
- Parental → Monitored Account
- Monitored Account → Add Medicine
- Monitored Account → Appointments
- Monitored Account → Set Reminder
- Monitored Account → Set Appointment

#### Casos de Uso:

- `GenerateParentalCode` - Gerar código para vínculo
- `LinkMonitoredAccount` - Vincular conta usando código
- `ListMonitoredAccounts` - Listar contas monitoradas
- `GetMonitoredAccountMedicines` - Ver medicamentos do filho
- `GetMonitoredAccountAppointments` - Ver consultas do filho
- `AddMedicineForMonitored` - Adicionar medicamento para filho
- `AddAppointmentForMonitored` - Agendar consulta para filho
- `SetReminderForMonitored` - Definir lembrete para filho
- `UnlinkMonitoredAccount` - Desvincular conta

#### Rotas:

```
POST   /api/parental/generate-code
POST   /api/parental/link
GET    /api/parental/monitored-accounts
GET    /api/parental/monitored-accounts/:childId/medicines
GET    /api/parental/monitored-accounts/:childId/appointments
POST   /api/parental/monitored-accounts/:childId/medicines
POST   /api/parental/monitored-accounts/:childId/appointments
POST   /api/parental/monitored-accounts/:childId/reminders
DELETE /api/parental/unlink/:childId
```

---

### 5. Nearby - Serviços de Saúde Próximos

#### Telas:

- Homepage → Nearby
- Nearby → Hospitals
- Nearby → Clinics
- Nearby → Pharmacies

#### Casos de Uso:

- `FindNearbyServices` - Buscar serviços próximos por tipo e localização

#### Rotas:

```
GET /api/nearby/hospitals?latitude={lat}&longitude={lng}&radius={km}
GET /api/nearby/clinics?latitude={lat}&longitude={lng}&radius={km}
GET /api/nearby/pharmacies?latitude={lat}&longitude={lng}&radius={km}
GET /api/nearby/all?latitude={lat}&longitude={lng}&radius={km}
```

---

### 6. Account - Perfil e Configurações

#### Telas:

- Homepage → Account
- Account → Edit Profile
- Account → Helpline
- Account → Parental Code
- Account → Give Feedback
- Account → Log out

#### Casos de Uso:

- `GetProfile` - Obter dados do perfil
- `UpdateProfile` - Atualizar perfil do usuário
- `SubmitFeedback` - Enviar feedback
- `GetHelpline` - Obter informações de suporte
- `Logout` - Encerrar sessão

#### Rotas:

```
GET    /api/profile
PUT    /api/profile
POST   /api/feedback
GET    /api/helpline
POST   /api/logout
```

---

## 🎯 Resumo por Domínio

### Authentication (Autenticação)

- 2 casos de uso
- 2 rotas

### Medicine (Medicamentos)

- 4 casos de uso
- 5 rotas

### Reminder (Lembretes)

- 4 casos de uso
- 5 rotas

### Appointment (Consultas)

- 4 casos de uso
- 5 rotas

### Parental Control (Controle Parental)

- 8 casos de uso
- 8 rotas

### Nearby Services (Serviços Próximos)

- 1 caso de uso
- 4 rotas (diferentes filtros)

### Profile & Account (Perfil e Conta)

- 5 casos de uso
- 5 rotas

---

## 📊 Estatísticas

- **Total de Telas:** ~25 telas
- **Total de Casos de Uso:** 28 casos de uso
- **Total de Rotas:** ~40 endpoints
- **Domínios:** 7 domínios principais

---

## 🔄 Fluxos Principais

### Fluxo 1: Adicionar Medicamento Completo

```
1. Usuario clica em "Add Medicine" na Homepage
2. Sistema chama AddMedicineController
3. Controller valida dados
4. Controller chama AddMedicine UseCase
5. UseCase persiste via MedicineRepository
6. Usuario é redirecionado para "Set Reminder"
7. Sistema chama SetReminderController
8. Controller chama SetReminder UseCase
9. Lembrete é criado e associado ao medicamento
10. Usuario retorna à Homepage
```

### Fluxo 2: Controle Parental (Vinculação)

```
1. PAI: Clica em "Parental" na Homepage
2. PAI: Sistema chama GenerateParentalCodeController
3. PAI: Código é gerado (ex: ABC123)
4. PAI: Compartilha código com filho

5. FILHO: Acessa "Parental" → "Scan to add"
6. FILHO: Insere código ABC123
7. FILHO: Sistema chama LinkMonitoredAccountController
8. Sistema verifica código e cria vínculo ParentalControl
9. FILHO: Conta fica vinculada ao pai

10. PAI: Acessa "Monitored Account"
11. PAI: Sistema chama ListMonitoredAccountsController
12. PAI: Visualiza conta do filho
13. PAI: Pode adicionar medicamentos/consultas para o filho
```

### Fluxo 3: Buscar Farmácias Próximas

```
1. Usuario clica em "Nearby" → "Pharmacies"
2. App obtém localização atual (GPS)
3. Sistema chama FindNearbyServicesController
4. Controller chama FindNearbyServices UseCase
5. UseCase busca no banco/API externa
6. Retorna lista ordenada por distância
7. Usuario visualiza farmácias próximas no mapa
```

---

## 🏗️ Arquitetura Clean

Cada feature segue o padrão:

```
User Interface (Mobile App)
         ↓
   HTTP Request
         ↓
    [Controller] ← Presentation Layer
         ↓
   [Use Case] ← Domain Layer
         ↓
   [Repository] ← Data Layer
         ↓
   [Database/API] ← Infrastructure Layer
```

---

## 🔐 Segurança e Autorização

### Rotas Públicas (sem autenticação):

- `POST /api/signup`
- `POST /api/login`
- `GET /api/health`

### Rotas Autenticadas:

Todas as demais rotas requerem token JWT no header:

```
x-access-token: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Autorização Parental:

Rotas de controle parental verificam:

1. Se o usuário está autenticado
2. Se o usuário tem permissão para acessar dados do filho
3. Se o vínculo parental está ativo

---

## 🚀 Próximas Implementações

### Fase 1 - Core Features (Atual)

- [x] Models de domínio criados
- [x] Casos de uso definidos
- [x] Controllers básicos criados
- [ ] Repositories in-memory
- [ ] Rotas configuradas
- [ ] Middleware de autenticação

### Fase 2 - Persistência

- [ ] Integração com banco de dados (PostgreSQL/MongoDB)
- [ ] Migrations
- [ ] Repositories implementados

### Fase 3 - Features Avançadas

- [ ] Notificações push para lembretes
- [ ] Integração com API de geolocalização
- [ ] Upload de imagens (foto de perfil)
- [ ] Histórico de medicamentos tomados
- [ ] Relatórios de adesão

### Fase 4 - Melhorias

- [ ] Cache (Redis)
- [ ] Rate limiting
- [ ] Logs estruturados
- [ ] Monitoramento
- [ ] Testes de integração
- [ ] Documentação Swagger/OpenAPI
