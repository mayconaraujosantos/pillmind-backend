# Como Adicionar Novas Features

## 📝 Exemplo: Adicionar funcionalidade de Survey (Enquete)

### Passo 1: Domain Layer (Regras de Negócio)

#### 1.1. Criar Model

```java
// src/main/java/com/pillmind/domain/models/Survey.java
package com.pillmind.domain.models;

import java.time.LocalDateTime;
import java.util.List;

public class Survey {
    private String id;
    private String question;
    private List<SurveyAnswer> answers;
    private LocalDateTime date;

    // Constructor, getters, setters
}

public class SurveyAnswer {
    private String answer;
    private String image; // opcional

    // Constructor, getters, setters
}
```

#### 1.2. Criar UseCase Interface

```java
// src/main/java/com/pillmind/domain/usecases/AddSurvey.java
package com.pillmind.domain.usecases;

public interface AddSurvey {
    void add(Params params);

    class Params {
        private final String question;
        private final List<SurveyAnswer> answers;

        // Constructor, getters
    }
}
```

### Passo 2: Data Layer (Orquestração)

#### 2.1. Criar Protocol (Port)

```java
// src/main/java/com/pillmind/data/protocols/db/AddSurveyRepository.java
package com.pillmind.data.protocols.db;

import com.pillmind.domain.usecases.AddSurvey;

public interface AddSurveyRepository {
    void add(AddSurvey.Params params);
}
```

#### 2.2. Implementar UseCase

```java
// src/main/java/com/pillmind/data/usecases/DbAddSurvey.java
package com.pillmind.data.usecases;

import com.pillmind.domain.usecases.AddSurvey;
import com.pillmind.data.protocols.db.AddSurveyRepository;

public class DbAddSurvey implements AddSurvey {
    private final AddSurveyRepository addSurveyRepository;

    public DbAddSurvey(AddSurveyRepository addSurveyRepository) {
        this.addSurveyRepository = addSurveyRepository;
    }

    @Override
    public void add(Params params) {
        addSurveyRepository.add(params);
    }
}
```

### Passo 3: Presentation Layer (Interface HTTP)

#### 3.1. Criar Controller

```java
// src/main/java/com/pillmind/presentation/controllers/AddSurveyController.java
package com.pillmind.presentation.controllers;

import com.pillmind.presentation.protocols.Controller;
import com.pillmind.presentation.protocols.HttpResponse;
import com.pillmind.presentation.protocols.Validation;
import com.pillmind.presentation.helpers.HttpHelper;
import com.pillmind.domain.usecases.AddSurvey;

public class AddSurveyController implements Controller<AddSurveyController.Request> {

    private final AddSurvey addSurvey;
    private final Validation validation;

    public AddSurveyController(AddSurvey addSurvey, Validation validation) {
        this.addSurvey = addSurvey;
        this.validation = validation;
    }

    @Override
    public HttpResponse handle(Request request) {
        try {
            var error = validation.validate(request);
            if (error != null) {
                return HttpHelper.badRequest(error);
            }

            var params = new AddSurvey.Params(
                request.question,
                request.answers
            );
            addSurvey.add(params);

            return HttpHelper.noContent();
        } catch (Exception e) {
            return HttpHelper.serverError(e);
        }
    }

    public static class Request {
        public String question;
        public List<SurveyAnswer> answers;

        // Constructor, getters, setters
    }
}
```

### Passo 4: Infrastructure Layer (Implementação Técnica)

#### 4.1. Implementar Repository

```java
// src/main/java/com/pillmind/infra/db/SurveyRepositoryInMemory.java
package com.pillmind.infra.db;

import com.pillmind.data.protocols.db.AddSurveyRepository;
import com.pillmind.domain.usecases.AddSurvey;
import com.pillmind.domain.models.Survey;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.time.LocalDateTime;

public class SurveyRepositoryInMemory implements AddSurveyRepository {

    private final Map<String, Survey> surveys = new HashMap<>();

    @Override
    public void add(AddSurvey.Params params) {
        String id = UUID.randomUUID().toString();
        Survey survey = new Survey(
            id,
            params.getQuestion(),
            params.getAnswers(),
            LocalDateTime.now()
        );
        surveys.put(id, survey);
    }
}
```

### Passo 5: Main Layer (Composition Root)

#### 5.1. Criar Factory

```java
// src/main/java/com/pillmind/main/factories/AddSurveyFactory.java
package com.pillmind.main.factories;

import com.pillmind.domain.usecases.AddSurvey;
import com.pillmind.data.usecases.DbAddSurvey;
import com.pillmind.data.protocols.db.AddSurveyRepository;
import com.pillmind.infra.db.SurveyRepositoryInMemory;

public class AddSurveyFactory {

    private static SurveyRepositoryInMemory surveyRepository;

    public static AddSurvey make() {
        if (surveyRepository == null) {
            surveyRepository = new SurveyRepositoryInMemory();
        }

        AddSurveyRepository repo = surveyRepository;
        return new DbAddSurvey(repo);
    }
}
```

#### 5.2. Criar Controller Factory

```java
// src/main/java/com/pillmind/main/factories/AddSurveyControllerFactory.java
package com.pillmind.main.factories;

import com.pillmind.presentation.controllers.AddSurveyController;
import com.pillmind.domain.usecases.AddSurvey;
import com.pillmind.presentation.protocols.Validation;

public class AddSurveyControllerFactory {

    public static AddSurveyController make() {
        AddSurvey addSurvey = AddSurveyFactory.make();
        Validation validation = ValidationFactory.makeAddSurveyValidation();

        return new AddSurveyController(addSurvey, validation);
    }
}
```

#### 5.3. Adicionar Rota

```java
// src/main/java/com/pillmind/main/routes/SurveyRoutes.java
package com.pillmind.main.routes;

import com.pillmind.main.adapters.JavalinRouteAdapter;
import com.pillmind.main.factories.AddSurveyControllerFactory;
import com.pillmind.presentation.controllers.AddSurveyController;
import io.javalin.Javalin;

public class SurveyRoutes {

    public static void setup(Javalin app) {
        // POST /api/surveys
        app.post("/api/surveys", ctx -> {
            var controller = AddSurveyControllerFactory.make();
            JavalinRouteAdapter.adapt(controller, ctx, AddSurveyController.Request.class);
        });
    }
}
```

#### 5.4. Registrar Rotas no Main

```java
// src/main/java/com/pillmind/Main.java
// Adicionar:
SurveyRoutes.setup(app);
```

### Passo 6: Testes (TDD)

#### 6.1. Criar Teste

```java
// src/test/java/com/pillmind/data/usecases/DbAddSurveyTest.java
package com.pillmind.data.usecases;

import com.pillmind.domain.usecases.AddSurvey;
import com.pillmind.data.protocols.db.AddSurveyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DbAddSurveyTest {

    @Mock
    private AddSurveyRepository addSurveyRepository;

    private DbAddSurvey sut;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sut = new DbAddSurvey(addSurveyRepository);
    }

    @Test
    void shouldCallAddSurveyRepositoryWithCorrectValues() {
        // Arrange
        var params = new AddSurvey.Params("any_question", List.of());

        // Act
        sut.add(params);

        // Assert
        verify(addSurveyRepository, times(1)).add(params);
    }
}
```

## 🔄 Checklist para Adicionar Nova Feature

- [ ] **Domain**: Criar model e interface do use case
- [ ] **Data**: Criar protocol (port) e implementar use case
- [ ] **Presentation**: Criar controller
- [ ] **Infrastructure**: Implementar repository/adapter
- [ ] **Main**: Criar factories e adicionar rotas
- [ ] **Tests**: Escrever testes unitários
- [ ] **Integration**: Testar endpoint completo

## 📋 Boas Práticas

### 1. Sempre comece pelo Domain

```
Domain → Data → Presentation → Infra → Main
```

### 2. Siga TDD

```
Red (teste falhando) → Green (implementação) → Refactor
```

### 3. Mantenha Single Responsibility

- Uma classe = uma responsabilidade
- Um método = uma coisa

### 4. Use Dependency Injection

```java
// ✅ BOM: Injeção via construtor
public class MyClass {
    private final Dependency dep;

    public MyClass(Dependency dep) {
        this.dep = dep;
    }
}

// ❌ RUIM: Instanciação direta
public class MyClass {
    private final Dependency dep = new ConcreteDependency();
}
```

### 5. Interfaces para Inversão de Dependência

```java
// ✅ BOM: Depende de abstração
public class UseCase {
    private final Repository repository;
}

// ❌ RUIM: Depende de implementação concreta
public class UseCase {
    private final MongoRepository repository;
}
```

## 🎯 Exemplo Completo: Middleware de Autenticação

### 1. Criar UseCase

```java
// domain/usecases/LoadAccountByToken.java
public interface LoadAccountByToken {
    Account load(String accessToken, String role);
}
```

### 2. Implementar UseCase

```java
// data/usecases/DbLoadAccountByToken.java
public class DbLoadAccountByToken implements LoadAccountByToken {
    private final Decrypter decrypter;
    private final LoadAccountByTokenRepository repository;

    @Override
    public Account load(String accessToken, String role) {
        String accountId = decrypter.decrypt(accessToken);
        if (accountId != null) {
            return repository.loadByToken(accountId, role);
        }
        return null;
    }
}
```

### 3. Criar Middleware

```java
// presentation/middlewares/AuthMiddleware.java
public class AuthMiddleware {
    private final LoadAccountByToken loadAccountByToken;

    public void handle(Context ctx) {
        String accessToken = ctx.header("x-access-token");
        if (accessToken != null) {
            Account account = loadAccountByToken.load(accessToken, null);
            if (account != null) {
                ctx.attribute("accountId", account.getId());
                return;
            }
        }
        ctx.status(403).json(Map.of("error", "Access denied"));
    }
}
```

### 4. Aplicar Middleware

```java
// main/routes/SurveyRoutes.java
app.post("/api/surveys", ctx -> {
    authMiddleware.handle(ctx);
    // ... resto do código
});
```

## 📚 Referências

- README.md: Documentação principal
- ARCHITECTURE.md: Detalhes da arquitetura
- QUICK_START.md: Guia rápido
- Este arquivo: Como adicionar features

## 💡 Dicas Finais

1. **Mantenha o Domain puro**: Sem dependências de frameworks
2. **Teste primeiro**: TDD garante qualidade
3. **Refatore constantemente**: Código limpo é código mantível
4. **Use nomes descritivos**: Código legível é melhor que comentários
5. **Siga SOLID**: Facilita manutenção e escalabilidade
