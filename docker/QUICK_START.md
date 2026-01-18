# 🚀 Quick Start - Docker PostgreSQL

## Início Rápido

```bash
# 1. Entre na pasta docker
cd docker

# 2. Inicie o banco de dados
docker-compose up -d

# 3. Verifique se está rodando
docker-compose ps

# 4. Acesse o banco (opcional)
docker exec -it pillmind-postgres psql -U postgres -d pillmind
```

## Usuários de Teste

### Contas Normais (com senha)
- **Email:** joao@example.com
- **Senha:** password123

- **Email:** maria@example.com  
- **Senha:** password123

- **Email:** carlos@example.com
- **Senha:** password123

### Contas Google (sem senha)
- **Email:** pedro@gmail.com
- **Email:** ana@gmail.com

## Gerar Hashes BCrypt Reais

```bash
# Na raiz do projeto
./gradlew run --main-class com.pillmind.util.BcryptHashGenerator --args "password123"
```

Copie o hash gerado e atualize o arquivo `docker/seed.sql`.

## Comandos Úteis

```bash
# Ver logs
docker-compose logs -f postgres

# Parar
docker-compose down

# Parar e apagar dados
docker-compose down -v

# Reiniciar
docker-compose restart postgres
```

## Conectar via Cliente Externo

- **Host:** localhost
- **Port:** 5432
- **Database:** pillmind
- **User:** postgres
- **Password:** postgres
