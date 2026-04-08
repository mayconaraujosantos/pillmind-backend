#!/bin/bash

# Script para gerar hash BCrypt de senhas
# Uso: ./generate-bcrypt-hash.sh "senha"

if [ -z "$1" ]; then
    echo "Uso: $0 <senha>"
    echo "Exemplo: $0 password123"
    exit 1
fi

PASSWORD="$1"

# Cria um script Java temporário para gerar o hash
cat > /tmp/BcryptHashGenerator.java << 'EOF'
import org.mindrot.jbcrypt.BCrypt;

public class BcryptHashGenerator {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Uso: java BcryptHashGenerator <senha>");
            System.exit(1);
        }
        String password = args[0];
        String hash = BCrypt.hashpw(password, BCrypt.gensalt(12));
        System.out.println("Senha: " + password);
        System.out.println("Hash:  " + hash);
    }
}
EOF

# Compila e executa (requer jbcrypt no classpath)
# Alternativa: usar um script Java/Gradle no projeto
echo "Para gerar o hash BCrypt, execute no projeto:"
echo ""
echo "  ./gradlew run --args=\"generate-hash $PASSWORD\""
echo ""
echo "Ou use o seguinte código Java:"
echo ""
echo "  BcryptAdapter adapter = new BcryptAdapter(12);"
echo "  String hash = adapter.hash(\"$PASSWORD\");"
echo "  System.out.println(hash);"
