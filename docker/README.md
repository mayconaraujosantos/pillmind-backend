# Docker Setup - PillMind Backend

Este diretório contém a configuração Docker para o banco de dados PostgreSQL usado no projeto PillMind.

## 📋 Pré-requisitos

- Docker instalado
- Docker Compose instalado

## 🚀 Como usar

### 1. Iniciar o banco de dados

```bash
# Na raiz do projeto
cd docker
docker-compose up -d
```

Isso irá:
- Criar um container PostgreSQL na porta 5432
- Executar automaticamente os scripts `init.sql` e `seed.sql`
- Criar a tabela `accounts` com dados de teste

### 2. Verificar se está rodando

```bash
docker-compose ps
```

### 3. Parar o banco de dados

```bash
docker-compose down
```

### 4. Parar e remover volumes (apaga dados)

```bash
docker-compose down -v
```

### 5. Ver logs

```bash
docker-compose logs -f postgres
```

## 🔧 Configuração

As variáveis de ambiente podem ser configuradas no arquivo `.env` na raiz do projeto ou diretamente no `docker-compose.yml`:

```env
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
POSTGRES_DB=pillmind
POSTGRES_PORT=5432
```

## 📊 Dados de Teste

O script `seed.sql` cria os seguintes usuários:

### Contas Normais (com senha)
- **joao@example.com** / senha: `password123`
- **maria@example.com** / senha: `password123`
- **carlos@example.com** / senha: `password123`

### Contas Google (sem senha)
- **pedro@gmail.com** (conta Google)
- **ana@gmail.com** (conta Google)

## 🔐 Gerar Hash BCrypt

Para gerar hashes BCrypt reais para as senhas de teste:

### Opção 1: Usando o projeto

Crie uma classe temporária ou use o código:

```java
BcryptAdapter adapter = new BcryptAdapter(12);
String hash = adapter.hash("password123");
System.out.println(hash);
```

### Opção 2: Atualizar seed.sql

1. Execute o código acima para gerar o hash
2. Atualize o arquivo `seed.sql` com o hash gerado
3. Recrie o container: `docker-compose down -v && docker-compose up -d`

## 🔌 Conectar ao banco

### Via psql (dentro do container)

```bash
docker exec -it pillmind-postgres psql -U postgres -d pillmind
```

### Via cliente externo

- **Host:** localhost
- **Port:** 5432
- **Database:** pillmind
- **User:** postgres
- **Password:** postgres

## 📝 Scripts SQL

- `init.sql` - Cria a estrutura do banco (tabelas, índices, triggers)
- `seed.sql` - Insere dados de teste

## 🐛 Troubleshooting

### Container não inicia

```bash
# Ver logs
docker-compose logs postgres

# Verificar se a porta está em uso
netstat -an | grep 5432
```

### Resetar banco de dados

```bash
docker-compose down -v
docker-compose up -d
```

### Acessar banco diretamente

```bash
docker exec -it pillmind-postgres psql -U postgres -d pillmind
```

## 📚 Comandos úteis

```bash
# Ver status
docker-compose ps

# Ver logs em tempo real
docker-compose logs -f

# Reiniciar serviço
docker-compose restart postgres

# Executar SQL
docker exec -i pillmind-postgres psql -U postgres -d pillmind < script.sql
```
