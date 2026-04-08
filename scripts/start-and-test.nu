#!/usr/bin/env nu

# Script para:
# 1. Iniciar o PostgreSQL em Docker
# 2. Iniciar o Backend (que executará migrations automaticamente)
# 3. Testar o fluxo completo: Login → Listar medicamentos

const GREEN = "\u001b[32m"
const BLUE = "\u001b[34m"
const YELLOW = "\u001b[33m"
const RED = "\u001b[31m"
const RESET = "\u001b[0m"

def log-info [msg: string] {
  print $"($BLUE)ℹ  ($msg)($RESET)"
}

def log-success [msg: string] {
  print $"($GREEN)✓ ($msg)($RESET)"
}

def log-warning [msg: string] {
  print $"($YELLOW)⚠  ($msg)($RESET)"
}

def log-error [msg: string] {
  print $"($RED)✗ ($msg)($RESET)"
}

def log-step [step: int, msg: string] {
  print $"\n($BLUE)━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━($RESET)"
  print $"($BLUE)STEP ($step): ($msg)($RESET)"
  print $"($BLUE)━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━($RESET)\n"
}

# Configuration
const BASE_URL = "http://localhost:8182/api"
const EMAIL = "teste@pillmind.com"
const PASSWORD = "Teste@123456"

# Step 1: Check PostgreSQL
log-step 1 "Verificar PostgreSQL"

let container_status = (docker ps --all --filter "name=pillmind-postgres" --format "table {{.Names}}\t{{.Status}}" 2>/dev/null | str trim)

if ($container_status == "") {
  log-warning "Container PostgreSQL não existe. Iniciando Docker Compose..."
  cd "d:\projects\pillmind-backend\docker\postgres"
  docker-compose up -d
  log-info "Aguardando PostgreSQL ficar pronto... (30s)"
  sleep 30s
  cd "d:\projects\pillmind-backend"
} else {
  log-info $"Status: ($container_status)"
  log-success "PostgreSQL já está em execução"
}

# Step 2: Build Backend
log-step 2 "Build Backend"
log-info "Compilando projeto..."
cd "d:\projects\pillmind-backend"
let build_result = (gradlew.bat build -x test 2>&1 | tail -5)
if ($build_result | str contains "BUILD SUCCESSFUL") {
  log-success "Build concluído com sucesso!"
} else {
  log-error "Falha no build!"
  exit 1
}

# Step 3: Start Backend (background)
log-step 3 "Iniciar Backend"
log-info "Iniciando servidor na porta 8182..."
log-info "O Flyway executará automaticamente a migration V7 com os dados de teste..."

# Verify backend port is free
let port_check = (netstat -ano | grep "8182" | str trim)
if ($port_check != "") {
  log-warning "Porta 8182 já está em uso. Encerrando processo existente..."
  # Extract PID and kill
  let pid = ($port_check | str split " " | last)
  taskkill /PID $pid /F 2>/dev/null
  sleep 2s
}

# Start backend in background
let backend_log = "d:\projects\pillmind-backend\backend.log"
print "" > $backend_log
log-info $"Logs salvos em: ($backend_log)"

# Using nohup equivalent for Windows
start "" cmd /c "cd d:\projects\pillmind-backend && gradlew.bat run >> $backend_log 2>&1"

log-info "Aguardando backend iniciar... (20s)"
sleep 20s

# Step 4: Test Backend Health
log-step 4 "Testar Backend"
let max_retries = 5
let mut retry = 0

while ($retry < $max_retries) {
  let health_check = (curl -s "http://localhost:8182/health" 2>/dev/null | str trim)

  if ($health_check != "") {
    log-success "Backend está respondendo!"
    break
  } else {
    log-info "Tentativa $($retry + 1) de $max_retries..."
    sleep 3s
    $retry = $retry + 1
  }
}

if ($retry == $max_retries) {
  log-error "Backend não respondeu após $max_retries tentativas"
  log-info "Verifique os logs: $backend_log"
  exit 1
}

# Step 5: Test Login
log-step 5 "Testar Login"

let login_response = (
  http post $"($BASE_URL)/auth/signin" \
    --header "Content-Type: application/json" \
    {
      "email": $EMAIL,
      "password": $PASSWORD
    } 2>/dev/null
)

let token = ($login_response | get accessToken? | str substring 0..40)

if ($token != null and $token != "") {
  log-success "Login realizado com sucesso!"
  log-info $"Token: $token..."
} else {
  log-error "Falha no login!"
  print ($login_response | to json)
  exit 1
}

let auth_token = ($login_response | get accessToken)

# Step 6: List Medications
log-step 6 "Listar Medicamentos"

let medicines = (
  http get $"($BASE_URL)/medicines" \
    --header $"Authorization: Bearer ($auth_token)" 2>/dev/null
)

let med_count = ($medicines | length)
log-success $"Total de medicamentos: ($med_count)"

if ($med_count > 0) {
  print "\n📋 Medicamentos cadastrados:"
  $medicines | each {|med|
    print $"\n  💊 Nome: ($med.name)"
    print $"     Dosagem: ($med.dosage)"
    print $"     Frequência: ($med.frequency)"
    print $"     Horários: ($med.times | str join ', ')"
    print $"     Data início: ($med.startDate)"
    print $"     Tipo: ($med.medicineType)"
    print $"     Notas: ($med.notes)"
  }
} else {
  log-warning "Nenhum medicamento encontrado!"
}

# Step 7: Summary
print "\n"
log-step 0 "✨ RESUMO DO TESTE"

print $"
($GREEN)Dados de Login:($RESET)
  Email: $EMAIL
  Senha: $PASSWORD

($GREEN)Medicamento Cadastrado:($RESET)
  Nome: Linandib
  Dosagem: 5mg
  Frequência: 1x ao dia
  Horário: 08:00
  Tipo: Comprimido
  Uso: Contínuo

($GREEN)URLs Úteis:($RESET)
  Backend API: http://localhost:8182/api
  Swagger Docs: http://localhost:8182/swagger-ui.html
  Backend Logs: $backend_log
"

log-success "Teste concluído com sucesso! 🎉"

print "
($YELLOW)Próximos passos:($RESET)
  1. Abrir o app mobile (npm start / pnpm start)
  2. Fazer login com as credenciais acima
  3. Verificar se o medicamento aparece na listagem
  4. Testar o agendamento de lembretes

($YELLOW)Para parar o backend:($RESET)
  gradlew.bat --stop  (na pasta pillmind-backend)
  ou Kill task do Java
"
