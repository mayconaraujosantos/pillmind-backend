.PHONY: up down restart logs ps shell clean generate-hash

# Inicia o container
up:
	docker-compose up -d

# Para o container
down:
	docker-compose down

# Para e remove volumes (apaga dados)
clean:
	docker-compose down -v

# Reinicia o container
restart:
	docker-compose restart postgres

# Mostra logs
logs:
	docker-compose logs -f postgres

# Status dos containers
ps:
	docker-compose ps

# Acessa o shell do PostgreSQL
shell:
	docker exec -it pillmind-postgres psql -U postgres -d pillmind

# Gera hash BCrypt para senha de teste
generate-hash:
	cd .. && ./gradlew run --main-class com.pillmind.util.BcryptHashGenerator --args "password123"

# Recria o banco com dados limpos
reset: clean up
	@echo "Banco de dados resetado!"

# Executa SQL
sql:
	@echo "Digite o SQL (Ctrl+D para finalizar):"
	docker exec -i pillmind-postgres psql -U postgres -d pillmind
