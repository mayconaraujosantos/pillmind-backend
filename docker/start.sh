#!/bin/bash

# Script para iniciar o banco de dados PostgreSQL

echo "🚀 Iniciando PostgreSQL Docker..."
echo ""

# Verifica se o Docker está rodando
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker não está rodando. Por favor, inicie o Docker primeiro."
    exit 1
fi

# Inicia o container
docker-compose up -d

# Aguarda o banco estar pronto
echo "⏳ Aguardando PostgreSQL estar pronto..."
sleep 5

# Verifica se está rodando
if docker-compose ps | grep -q "Up"; then
    echo "✅ PostgreSQL está rodando!"
    echo ""
    echo "📊 Informações de conexão:"
    echo "   Host:     localhost"
    echo "   Port:     5432"
    echo "   Database: pillmind"
    echo "   User:     postgres"
    echo "   Password: postgres"
    echo ""
    echo "🔍 Para ver logs: docker-compose logs -f postgres"
    echo "🛑 Para parar:    docker-compose down"
else
    echo "❌ Erro ao iniciar PostgreSQL. Verifique os logs:"
    docker-compose logs postgres
    exit 1
fi
