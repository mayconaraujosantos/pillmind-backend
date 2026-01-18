#!/bin/bash

# Script para atualizar os hashes BCrypt no seed.sql
# Este script gera hashes reais usando o projeto Java

echo "Gerando hashes BCrypt para as senhas de teste..."
echo ""

# Senha padrão para todos os usuários de teste
PASSWORD="password123"

# Cria um script Java temporário
cat > /tmp/GenerateHashes.java << 'JAVA_EOF'
import org.mindrot.jbcrypt.BCrypt;

public class GenerateHashes {
    public static void main(String[] args) {
        String password = "password123";
        System.out.println("Hash para '" + password + "':");
        System.out.println(BCrypt.hashpw(password, BCrypt.gensalt(12)));
    }
}
JAVA_EOF

echo "Para gerar os hashes, execute no projeto:"
echo ""
echo "  cd .."
echo "  ./gradlew run --args='generate-hash'"
echo ""
echo "Ou use o código Java diretamente:"
echo ""
echo "  BcryptAdapter adapter = new BcryptAdapter(12);"
echo "  String hash = adapter.hash(\"password123\");"
echo "  System.out.println(hash);"
echo ""
echo "Depois atualize o arquivo seed.sql com o hash gerado."
