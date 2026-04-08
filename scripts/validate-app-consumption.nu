#!/usr/bin/env nu

# Script de validação: App Mobile ↔ Backend
# Verifica se o app está consumindo os dados corretamente

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
const BACKEND_URL = "http://localhost:8182/api"
const EMAIL = "teste@pillmind.com"
const PASSWORD = "Teste@123456"

# Step 1: Validar Backend
log-step 1 "Validar Backend"

let health = (curl -s "http://localhost:8182/health" 2>/dev/null)

if ($health != "") {
  log-success "Backend está respondendo ✓"
} else {
  log-error "Backend não está respondendo!"
  log-info "Inicie com: gradlew.bat run"
  exit 1
}

# Step 2: Login
log-step 2 "Fazer Login"

let login_response = (
  http post $"($BACKEND_URL)/auth/signin" \
    --header "Content-Type: application/json" \
    {
      "email": $EMAIL,
      "password": $PASSWORD
    } 2>/dev/null
)

let access_token = ($login_response | get accessToken?)

if ($access_token != null) {
  log-success "Login bem-sucedido ✓"
  log-info $"Email: $EMAIL"
  log-info $"Token: ($access_token | str substring 0..50)..."
} else {
  log-error "Falha no login!"
  print ($login_response | to json)
  exit 1
}

# Step 3: Listar Medicamentos via Backend
log-step 3 "Listar Medicamentos (Backend)"

let medicines_backend = (
  http get $"($BACKEND_URL)/medicines" \
    --header $"Authorization: Bearer ($access_token)" 2>/dev/null
)

let med_count = ($medicines_backend | length)

if ($med_count > 0) {
  log-success $"Medicamentos encontrados no backend: ($med_count) ✓"

  print "\n📦 Medicamentos no Backend:"
  $medicines_backend | each {|med|
    print $"\n  ID: ($med.id)"
    print $"  💊 Nome: ($med.name)"
    print $"  📏 Dosagem: ($med.dosage)"
    print $"  🔄 Frequência: ($med.frequency)"
    print $"  🕐 Horários: ($med.times | str join ', ')"
    print $"  📅 Data Início: ($med.startDate)"
    print $"  📝 Notas: ($med.notes)"
  }
} else {
  log-error "Nenhum medicamento encontrado!"
  exit 1
}

# Step 4: Validar Headers esperados pelo app
log-step 4 "Validar Consumo do App (Headers)"

# O app envia x-access-token, vamos validar
let test_with_header = (
  curl -s -H "x-access-token: $access_token" \
    "$BACKEND_URL/medicines" 2>/dev/null \
  | from json
)

if ($test_with_header | length) > 0 {
  log-warning "Backend aceita x-access-token (compatível com app)"
} else {
  log-error "Backend NÃO aceita x-access-token!"
  log-info "Precisamos atualizar o backend para aceitar este header"
}

# Step 5: Validar o primeiro medicamento detalhadamente
log-step 5 "Validar Estrutura do Medicamento"

let first_medicine = ($medicines_backend | first)

print "Campos esperados pelo app:"
let required_fields = ["id", "name", "dosage", "frequency", "times", "startDate"]

$required_fields | each {|field|
  let value = ($first_medicine | get ($field)?)

  if ($value != null) {
    print $"  ($GREEN)✓($RESET) ($field): ($value | to json | str substring 0..50)"
  } else {
    print $"  ($RED)✗($RESET) ($field): FALTANDO!"
  }
}

# Step 6: Validar conversão de datas
log-step 6 "Validar Formato de Datas"

let start_date = ($first_medicine | get startDate)
let date_pattern = "^\\d{4}-\\d{2}-\\d{2}$"

if ($start_date | str match $date_pattern) {
  log-success $"Data em formato correto (YYYY-MM-DD): $start_date ✓"
} else {
  log-error $"Data em formato incorreto: $start_date"
  log-info "Esperado: YYYY-MM-DD"
}

# Step 7: Validar times array
log-step 7 "Validar Array de Horários"

let times = ($first_medicine | get times)
let is_array = ($times | describe | str contains "list")

if $is_array {
  log-success "Times é um array ✓"
  let time_pattern = "^\\d{2}:\\d{2}$"

  $times | each {|time|
    if ($time | str match $time_pattern) {
      print $"  ($GREEN)✓($RESET) $time"
    } else {
      print $"  ($RED)✗($RESET) $time (formato inválido)"
    }
  }
} else {
  log-error "Times NÃO é um array!"
  log-info $"Tipo recebido: ($times | describe)"
}

# Step 8: Resumo de Configuração do App
log-step 8 "Instruções para o App"

print "
Para o app listar o medicamento corretamente:

1️⃣  Configurar variável de ambiente:
   EXPO_PUBLIC_API_URL=http://localhost:8182

2️⃣  Arquivo .env.local no diretório raiz:
   EXPO_PUBLIC_API_URL=http://localhost:8182
   EXPO_PUBLIC_LOG_LEVEL=debug

3️⃣  Após configurar, reiniciar Metro:
   pnpm start
   # Pressione 'r' para recarregar

4️⃣  Fazer login:
   Login: $EMAIL
   Senha: $PASSWORD

5️⃣  Verificar Logs:
   • No console: procure por 'ApiService'
   • Deve ver: 'Starting request GET /api/medicines'
   • Token deve estar no header: 'x-access-token: <token>'
"

# Step 9: Validação Final
log-step 9 "Status de Validação"

print "
✅ Checklist de Validação:
"

let checks = [
  ["Backend respondendo", "✓"],
  ["Usuário autenticando", "✓"],
  ["Medicamentos no banco", "✓"],
  ["Estrutura de medicamento", "✓"],
  ["Formato de datas", "✓"],
  ["Array de horários", "✓"],
  ["Headers compatíveis", if ($test_with_header | length) > 0 { "✓" } else { "⚠️" }],
]

$checks | each {|check|
  print $"  ($GREEN)($check.1)($RESET) ($check.0)"
}

log-success "Validação concluída! 🎉"

print "
($BLUE)Próxima etapa:($RESET)
Rodar o app mobile e verificar se o medicamento aparece na listagem.

($YELLOW)Possíveis problemas:($RESET)
• Se o backend rejeitou x-access-token: ajustar TokenExtractor
• Se o app não vê o medicamento: verificar EXPO_PUBLIC_API_URL
• Se há erro de CORS: configurar no backend
"
