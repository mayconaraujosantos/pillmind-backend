# 🚀 OAuth2 Google - Implementação Completa

## ✅ O que foi implementado

### 1. **Dependências Adicionadas** ([build.gradle](build.gradle))

```gradle
// Google OAuth2
implementation("com.google.api-client:google-api-client:2.2.0")
implementation("com.google.http-client:google-http-client-jackson2:1.43.3")
```

### 2. **GoogleTokenValidator** ([GoogleTokenValidator.java](src/main/java/com/pillmind/infra/oauth/GoogleTokenValidator.java))

- Valida ID Token do Google
- Verifica se email está verificado
- Extrai informações do usuário (name, email, googleId, picture)
- Retorna erro se token inválido/expirado

### 3. **GoogleAuthController** ([GoogleAuthController.java](src/main/java/com/pillmind/presentation/controllers/GoogleAuthController.java))

- Recebe `idToken` do mobile
- Valida com Google
- **Cria conta** se email não existe (SIGNUP automático)
- **Faz login** se email já existe (SIGNIN automático)
- Retorna JWT próprio da aplicação

### 4. **DbAuthentication Atualizado** ([DbAuthentication.java](src/main/java/com/pillmind/data/usecases/DbAuthentication.java))

- Suporta login OAuth2 (password = null)
- Diferencia contas Google de contas tradicionais
- Retorna erro apropriado se tentar usar senha em conta Google

### 5. **Nova Rota** ([AuthRoutes.java](src/main/java/com/pillmind/main/routes/AuthRoutes.java))

```
POST /api/auth/google
Body: { "idToken": "eyJhbG..." }
Response: { "accessToken": "jwt...", "accountId": "uuid", "name": "...", "email": "..." }
```

### 6. **Configuração** ([Env.java](src/main/java/com/pillmind/main/config/Env.java))

```bash
# .env ou variável de ambiente
GOOGLE_CLIENT_ID=123456789-abc.apps.googleusercontent.com
```

## 📱 Como Usar no Mobile

### React Native Example

```typescript
import { GoogleSignin } from '@react-native-google-signin/google-signin';

// 1. Configurar Google Sign In (no início do app)
GoogleSignin.configure({
  webClientId: '123456789-abc.apps.googleusercontent.com', // Mesmo do backend
  offlineAccess: true,
});

// 2. Função de signup/signin com Google
const handleGoogleAuth = async () => {
  try {
    // Abre popup do Google
    await GoogleSignin.hasPlayServices();
    const userInfo = await GoogleSignin.signIn();

    // Pega o idToken
    const idToken = userInfo.idToken;

    // Envia para backend
    const response = await fetch('http://seu-backend:7000/api/auth/google', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ idToken })
    });

    const data = await response.json();

    if (response.ok) {
      // Salva JWT e dados do usuário
      await AsyncStorage.setItem('accessToken', data.accessToken);
      await AsyncStorage.setItem('user', JSON.stringify({
        id: data.accountId,
        name: data.name,
        email: data.email
      }));

      // Navega para tela principal
      navigation.navigate('Home');
    } else {
      Alert.alert('Erro', data.error);
    }
  } catch (error) {
    console.error(error);
    Alert.alert('Erro', 'Falha ao fazer login com Google');
  }
};

// 3. No componente
<TouchableOpacity onPress={handleGoogleAuth}>
  <Image source={googleIcon} />
  <Text>Continue with Google</Text>
</TouchableOpacity>
```

## 🔧 Configuração Google Cloud

### Passos

1. Acesse [Google Cloud Console](https://console.cloud.google.com)
2. Crie um projeto (ou use existente)
3. Vá em **APIs & Services** → **Credentials**
4. Clique em **Create Credentials** → **OAuth client ID**
5. Configure:
   - **Application type**: Web application (para backend)
   - **Name**: PillMind Backend
   - **Authorized redirect URIs**: (deixe vazio se só usar mobile)
6. Crie outro OAuth client ID:
   - **Application type**: Android
   - **Package name**: com.pillmind.app
   - **SHA-1**: (do seu keystore)

7. Anote os Client IDs:
   - **Web Client ID**: usar no backend (.env) e mobile (GoogleSignin.configure)
   - **Android Client ID**: usar no Google Cloud Console

### No Backend (.env)

```bash
GOOGLE_CLIENT_ID=123456789-abc.apps.googleusercontent.com
```

### No Mobile (config)

```typescript
GoogleSignin.configure({
  webClientId: "123456789-abc.apps.googleusercontent.com", // Web Client ID
  iosClientId: "123456789-ios.apps.googleusercontent.com", // Se tiver iOS
});
```

## 🧪 Testando

### Teste Manual com cURL

**Não é possível testar diretamente com cURL** porque você precisa de um ID Token real do Google, que só pode ser obtido através do fluxo OAuth2 no mobile/web.

Para testar:

1. Configure o mobile app
2. Faça login com Google
3. Copie o idToken do console
4. Teste com cURL:

```bash
curl -X POST http://localhost:7000/api/auth/google \
  -H "Content-Type: application/json" \
  -d '{
    "idToken": "eyJhbGciOiJSUzI1NiIsImtpZCI6Ij..."
  }'
```

**Resposta esperada:**

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "accountId": "550e8400-e29b-41d4-a716-446655440000",
  "name": "John Doe",
  "email": "john@gmail.com"
}
```

## 🎯 Fluxo Completo

```
1. User abre app
2. User clica "Continue with Google"
3. Google SDK abre popup
4. User seleciona conta e aprova
5. Google retorna idToken para o app
6. App envia idToken para: POST /api/auth/google
7. Backend valida token com Google
8. Backend verifica se email existe:
   - NÃO existe → Cria conta (SIGNUP)
   - SIM existe → Faz login (SIGNIN)
9. Backend gera JWT próprio
10. Backend retorna JWT + dados do usuário
11. App salva JWT e navega para home
12. App usa JWT em todas as requisições futuras:
    Authorization: Bearer eyJhbGciOiJ...
```

## 🔐 Segurança

✅ **O que o backend faz:**

- Valida token com Google API (não confia cegamente)
- Verifica se email está verificado
- Gera JWT próprio (não usa token do Google)
- Diferencia contas Google de contas tradicionais

❌ **Não deixe de:**

- Configurar HTTPS em produção
- Rotacionar JWT_SECRET periodicamente
- Configurar expiração adequada do JWT
- Validar origem das requisições

## 📊 Diferenças: Signup vs Signin

**Com OAuth2, NÃO há diferença!**

```typescript
// Mesma função para signup E signin
const googleAuth = () => {
  // Backend decide automaticamente se cria ou autentica
};

// Na tela de Signup
<Button onPress={googleAuth}>Sign Up with Google</Button>

// Na tela de Signin
<Button onPress={googleAuth}>Sign In with Google</Button>

// MESMO ENDPOINT: POST /api/auth/google
```

## 📚 Próximos Passos

- [ ] Adicionar Facebook OAuth2 (similar ao Google)
- [ ] Adicionar Apple Sign In (obrigatório para iOS)
- [ ] Implementar refresh token
- [ ] Adicionar logout (revoke token)
- [ ] Adicionar linked accounts (vincular Google com conta tradicional)

## 🆘 Troubleshooting

**Erro: "Token do Google inválido ou expirado"**

- Verifique se GOOGLE_CLIENT_ID está correto
- Verifique se o webClientId no mobile é o mesmo
- Token expira em 1 hora, teste imediatamente

**Erro: "Email do Google não verificado"**

- User precisa verificar email no Google
- Isso é verificado automaticamente pelo backend

**Erro: "Esta conta usa login do Google"**

## 🆘 Troubleshooting

**Erro: "Token do Google inválido ou expirado"**

- Verifique se GOOGLE_CLIENT_ID está correto
- Verifique se o webClientId no mobile é o mesmo
- Token expira em 1 hora, teste imediatamente

**Erro: "Email do Google não verificado"**

- User precisa verificar email no Google
- Isso é verificado automaticamente pelo backend

**Erro: "Esta conta usa login do Google"**

- User tentou fazer login tradicional em conta criada com Google
- Direcione para usar "Sign in with Google"

## 📖 Documentação

- [Google Sign-In for Web](https://developers.google.com/identity/sign-in/web)
- [React Native Google Sign In](https://github.com/react-native-google-signin/google-signin)
- [Google ID Token Verification](https://developers.google.com/identity/sign-in/web/backend-auth)
