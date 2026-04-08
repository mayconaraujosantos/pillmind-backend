# Como Gerar Hashes BCrypt para os Usuários de Teste

## Método 1: Usando o Projeto Java

1. Crie um arquivo temporário `GenerateHashes.java` na raiz do projeto:

```java
import com.pillmind.infra.cryptography.BcryptAdapter;

public class GenerateHashes {
    public static void main(String[] args) {
        BcryptAdapter adapter = new BcryptAdapter(12);
        String password = "password123";
        String hash = adapter.hash(password);
        System.out.println("Senha: " + password);
        System.out.println("Hash:  " + hash);
    }
}
```

2. Compile e execute:

```bash
javac -cp "build/libs/*:$(./gradlew printClasspath)" GenerateHashes.java
java -cp ".:build/libs/*:$(./gradlew printClasspath)" GenerateHashes
```

## Método 2: Usando um Teste JUnit

Crie um teste temporário:

```java
@Test
public void generateHashes() {
    BcryptAdapter adapter = new BcryptAdapter(12);
    String hash = adapter.hash("password123");
    System.out.println("Hash: " + hash);
}
```

Execute: `./gradlew test --tests GenerateHashes`

## Método 3: Atualizar seed.sql Manualmente

1. Execute um dos métodos acima para obter o hash
2. Copie o hash gerado
3. Atualize o arquivo `docker/seed.sql` substituindo os placeholders
4. Recrie o container: `docker-compose down -v && docker-compose up -d`

## Exemplo de Hash Real

Para a senha `password123` com salt rounds 12, um exemplo de hash seria:

```
$2a$12$KIXH4q8VqJ8VqJ8VqJ8VqO8VqJ8VqJ8VqJ8VqJ8VqJ8VqJ8VqJ8VqJ8V
```

**Nota:** Cada execução gera um hash diferente devido ao salt aleatório, mas todos são válidos para a mesma senha.
