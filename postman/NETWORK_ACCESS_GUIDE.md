# Teste de Conectividade - PillMind API

## 🚀 Guia de Teste para Acesso em Rede

### 1. ✅ Verificar se o Backend está Rodando

No computador que hospeda o backend (`192.168.1.7`):

```bash
# Verificar se o processo está ativo
./gradlew run

# Em outro terminal, testar localmente
curl http://localhost:7000/api/health
```

### 2. 🔥 Testar Conectividade de Outro Computador

De **qualquer outro computador** na mesma rede:

```bash
# Teste básico de conectividade
curl http://192.168.1.7:7000/api/health

# Se funcionar, deve retornar algo como:
# {"status":"OK","timestamp":1642518000000}
```

### 3. 🌐 Teste no Navegador

Abra em qualquer navegador (de outro computador):
```
http://192.168.1.7:7000/api/health
```

### 4. 📱 Teste Completo com Postman

1. **Importe as coleções** do Postman
2. **Execute Authentication → Signup** para criar uma conta
3. **Execute Authentication → Login** para obter token
4. **Teste outros endpoints** com o token salvo automaticamente

### 5. 🔧 Troubleshooting

#### Se não conseguir conectar:

**a) Verificar Firewall (Ubuntu/Linux):**
```bash
# Permitir porta 7000 no firewall
sudo ufw allow 7000

# Verificar status
sudo ufw status
```

**b) Verificar se o serviço está escutando na rede:**
```bash
# Verificar se porta 7000 está aberta para conexões externas
sudo netstat -tlnp | grep 7000

# Deve mostrar algo como: 
# tcp6 0 0 :::7000 :::* LISTEN 12345/java
```

**c) Testar conectividade básica:**
```bash
# De outro computador, testar se consegue fazer ping
ping 192.168.1.7

# Testar se a porta está acessível
telnet 192.168.1.7 7000
```

**d) Verificar IP da máquina:**
```bash
# Confirmar IP atual
ip route get 1.1.1.1 | grep -oP 'src \K\S+'
```

### 6. ✨ URLs Configuradas nas Coleções

Todas as coleções do Postman já estão configuradas com:
- **Base URL**: `http://192.168.1.7:7000`
- **Autenticação**: Headers automáticos com token
- **Variáveis**: IDs salvos automaticamente

### 7. 📋 Checklist de Funcionamento

- [ ] Backend rodando em `192.168.1.7:7000`
- [ ] Health check funcionando localmente
- [ ] Health check funcionando de outro computador
- [ ] Firewall liberado (se necessário)
- [ ] Coleções do Postman importadas
- [ ] Login funcionando e token salvo
- [ ] Endpoints principais testados

### 8. 🎯 Exemplo de Teste Completo

```bash
# 1. Teste básico (de outro computador)
curl http://192.168.1.7:7000/api/health

# 2. Criar usuário
curl -X POST http://192.168.1.7:7000/api/signup \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Teste Usuario",
    "email": "teste@example.com", 
    "password": "senha123",
    "passwordConfirmation": "senha123"
  }'

# 3. Fazer login (salvar o token retornado)
curl -X POST http://192.168.1.7:7000/api/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "teste@example.com",
    "password": "senha123"
  }'

# 4. Usar token para listar medicamentos (substitua SEU_TOKEN)
curl http://192.168.1.7:7000/api/medicines \
  -H "x-access-token: SEU_TOKEN"
```

---

## 🎉 Pronto para Usar!

Com essas configurações, qualquer pessoa na sua rede local pode:
- Importar as coleções do Postman
- Testar todos os endpoints da API  
- Desenvolver integrações com o PillMind
- Colaborar no desenvolvimento

**IP configurado**: `192.168.1.7:7000`
**Status**: Acessível na rede local ✅