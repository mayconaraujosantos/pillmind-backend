#!/usr/bin/env nu

# Colors for output
const GREEN = "\u001b[32m"
const BLUE = "\u001b[34m"
const YELLOW = "\u001b[33m"
const RED = "\u001b[31m"
const RESET = "\u001b[0m"

# Configuration
const BASE_URL = "http://localhost:8182/api"
const EMAIL = "teste@pillmind.com"
const PASSWORD = "Teste@123456"

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

# Step 1: Register or Login
log-step 1 "Register User"

let signup_response = (
  http post $"($BASE_URL)/auth/signup" \
    --header "Content-Type: application/json" \
    {
      "email": $EMAIL,
      "password": $PASSWORD,
      "name": "Usuário Teste",
      "dateOfBirth": "1990-05-15",
      "gender": "M"
    }
)

let token = ($signup_response | get accessToken?)

if ($token != null) {
  log-success $"Usuário criado com sucesso!"
  log-info $"Token: ($token | str substring 0..20)..."
} else {
  log-warning "Usuário pode já existir. Tentando fazer login..."

  let login_response = (
    http post $"($BASE_URL)/auth/signin" \
      --header "Content-Type: application/json" \
      {
        "email": $EMAIL,
        "password": $PASSWORD
      }
  )

  let token_login = ($login_response | get accessToken)

  if ($token_login != null) {
    log-success "Login realizado com sucesso!"
    log-info $"Token: ($token_login | str substring 0..20)..."
  } else {
    log-error "Falha ao registrar ou fazer login!"
    exit 1
  }
}

# If signup created user, use that token, otherwise use login token
let auth_token = if ($token != null) { $token } else { ($login_response | get accessToken) }

# Step 2: Create Medication
log-step 2 "Create Medication (Linandib 5mg)"

let med_response = (
  http post $"($BASE_URL)/medicines" \
    --header "Content-Type: application/json" \
    --header $"Authorization: Bearer ($auth_token)" \
    {
      "name": "Linandib",
      "dosage": "5mg",
      "frequency": "1x ao dia",
      "times": ["08:00"],
      "startDate": "2026-04-05",
      "endDate": null,
      "notes": "Tomar 1 comprimido pela manhã - USO CONTINUO",
      "medicineType": "Comprimido",
      "prescribedFor": "Controle",
      "quantity": 1,
      "reminderOnEmpty": true
    }
)

let medicine_id = ($med_response | get id)

if ($medicine_id != null) {
  log-success $"Medicamento criado com sucesso!"
  log-info $"ID: ($medicine_id)"
  log-info $"Nome: ($med_response | get name)"
  log-info $"Dosagem: ($med_response | get dosage)"
  log-info $"Frequência: ($med_response | get frequency)"
} else {
  log-error "Falha ao criar medicamento!"
  print ($med_response | to json)
  exit 1
}

# Step 3: List Medications
log-step 3 "List All Medications"

let list_response = (
  http get $"($BASE_URL)/medicines" \
    --header $"Authorization: Bearer ($auth_token)"
)

let med_count = ($list_response | length)
log-success $"Total de medicamentos: ($med_count)"

print "\nMedicamentos cadastrados:"
$list_response | each {|med|
  print $"  • ($med.name) - ($med.dosage)"
  print $"    Frequência: ($med.frequency)"
  print $"    Horários: ($med.times | str join ', ')"
  print $"    Data início: ($med.startDate)"
  print ""
}

# Step 4: Get Medicine by ID
log-step 4 "Get Medication Details"

let detail_response = (
  http get $"($BASE_URL)/medicines/($medicine_id)" \
    --header $"Authorization: Bearer ($auth_token)"
)

log-success "Detalhes do medicamento:"
print ($detail_response | to json)

# Step 5: List Doses (optional)
log-step 5 "List Doses for Today"

let today = (date now | format date '%Y-%m-%d')
let doses_response = (
  http get $"($BASE_URL)/medicines/($medicine_id)/doses?date=($today)" \
    --header $"Authorization: Bearer ($auth_token)"
)

let dose_count = ($doses_response | length)
log-success $"Total de doses hoje: ($dose_count)"

if ($dose_count > 0) {
  print "\nDoses para hoje:"
  $doses_response | each {|dose|
    print $"  • Horário agendado: ($dose.scheduledTime)"
    print $"    Status: ($dose.status)"
  }
}

# Summary
log-step 0 "RESUMO DO TESTE"
print $"($GREEN)✓ Usuário registrado/autenticado: ($EMAIL)($RESET)"
print $"($GREEN)✓ Medicamento criado: Linandib 5mg ($medicine_id)($RESET)"
print $"($GREEN)✓ Total de medicamentos: ($med_count)($RESET)"
print $"($GREEN)✓ Total de doses para hoje: ($dose_count)($RESET)"
print "\n"
log-success "Fluxo de teste concluído com sucesso! 🎉"
print "\nPróximos passos:"
print "1. Iniciar o app mobile"
print $"2. Fazer login com: ($EMAIL) / ($PASSWORD)"
print "3. Verificar se o medicamento 'Linandib 5mg' aparece na listagem"
print "4. Testar o agendamento de lembretes\n"
