# 📱 Guia de Integração OAuth2 Mobile

## 🎯 Visão Geral

Este guia explica como integrar autenticação Google/Facebook no seu app mobile com o backend PillMind.

## 🔐 Fluxo Completo

### 1. **Tela de Signup/Signin no Mobile**

```
┌─────────────────────────┐
│   Welcome Screen        │
│                         │
│  ┌──────────────────┐   │
│  │  Sign Up Email   │   │ ← Signup tradicional
│  └──────────────────┘   │
│                         │
│  ┌──────────────────┐   │
│  │ 🔵 Google Sign   │   │ ← OAuth2 Google
│  └──────────────────┘   │
│                         │
│  ┌──────────────────┐   │
│  │ 🔷 Facebook Sign │   │ ← OAuth2 Facebook
│  └──────────────────┘   │
│                         │
│  Already have account?  │
│  Sign In                │
└─────────────────────────┘
```

### 2. **Implementação Mobile (React Native exemplo)**

#### Instalar dependências:

```bash
npm install @react-native-google-signin/google-signin
npm install react-native-fbsdk-next
```

#### Componente de Signup:

```typescript
// screens/SignUpScreen.tsx
import { GoogleSignin } from '@react-native-google-signin/google-signin';
import { LoginManager, AccessToken } from 'react-native-fbsdk-next';

export function SignUpScreen() {
  // Configurar Google Sign In
  useEffect(() => {
    GoogleSignin.configure({
      webClientId: 'YOUR_WEB_CLIENT_ID.apps.googleusercontent.com',
      offlineAccess: true,
    });
  }, []);

  // Signup com Google
  const handleGoogleSignUp = async () => {
    try {
      await GoogleSignin.hasPlayServices();
      const userInfo = await GoogleSignin.signIn();
      const idToken = userInfo.idToken;

      // Enviar para backend
      const response = await fetch('http://seu-backend:7000/api/auth/google', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ idToken })
      });

      const data = await response.json();

      // Salvar token JWT
      await AsyncStorage.setItem('accessToken', data.accessToken);
      await AsyncStorage.setItem('user', JSON.stringify({
        id: data.accountId,
        name: data.name,
        email: data.email
      }));

      // Navegar para tela principal
      navigation.navigate('Home');
    } catch (error) {
      console.error(error);
    }
  };

  // Signup com Facebook
  const handleFacebookSignUp = async () => {
    try {
      const result = await LoginManager.logInWithPermissions(['public_profile', 'email']);

      if (result.isCancelled) return;

      const data = await AccessToken.getCurrentAccessToken();
      const accessToken = data.accessToken.toString();

      // Enviar para backend
      const response = await fetch('http://seu-backend:7000/api/auth/facebook', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ accessToken })
      });

      const userData = await response.json();

      // Salvar e navegar...
    } catch (error) {
      console.error(error);
    }
  };

  // Signup tradicional
  const handleEmailSignUp = async (email: string, password: string, name: string) => {
    const response = await fetch('http://seu-backend:7000/api/signup', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password, name, googleAccount: false })
    });

    const data = await response.json();
    // Salvar token e navegar...
  };

  return (
    <View>
      <TextInput placeholder="Name" />
      <TextInput placeholder="Email" />
      <TextInput placeholder="Password" secureTextEntry />
      <Button title="Sign Up" onPress={handleEmailSignUp} />

      <Text>--- OR ---</Text>

      <Button title="Continue with Google" onPress={handleGoogleSignUp} />
      <Button title="Continue with Facebook" onPress={handleFacebookSignUp} />
    </View>
  );
}
```

### 3. **Backend - Endpoints Necessários**

#### ✅ Já Implementados:

```
POST /api/signup          - Signup tradicional (email/senha)
POST /api/signin          - Signin tradicional (email/senha)
```

#### 🔄 A Implementar:

```
POST /api/auth/google     - Signup/Signin com Google
POST /api/auth/facebook   - Signup/Signin com Facebook
```

### 4. **Estrutura do Banco de Dados**

A tabela `accounts` já suporta OAuth2:

```sql
CREATE TABLE accounts (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255),              -- NULL para contas Google/Facebook
    google_account BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Importante:**

- `password` pode ser NULL (para contas OAuth2)
- `google_account = true` indica que é conta Google
- Mesmo email não pode ter duas contas (tradicional + Google)

### 5. **Fluxo de Decisão no Backend**

```java
// POST /api/auth/google
1. Recebe idToken do mobile
2. Valida token com Google API
3. Extrai email do usuário
4. Verifica se email existe no banco:
   - SIM → Faz login (retorna JWT)
   - NÃO → Cria conta e faz login (retorna JWT)
5. Mobile salva JWT e usa em requisições futuras
```

### 6. **Usando o JWT em Requisições**

Depois do login (tradicional ou OAuth2), o mobile recebe um JWT:

```typescript
// Fazer requisições autenticadas
const response = await fetch("http://seu-backend:7000/api/medications", {
  headers: {
    Authorization: `Bearer ${accessToken}`,
    "Content-Type": "application/json",
  },
});
```

### 7. **Configuração do Google Cloud**

Para usar Google Sign In, você precisa:

1. Acessar [Google Cloud Console](https://console.cloud.google.com)
2. Criar um projeto
3. Ativar "Google+ API"
4. Criar credenciais OAuth 2.0:
   - **Web Client ID** (para backend validar)
   - **Android Client ID** (para app Android)
   - **iOS Client ID** (para app iOS)

**No backend (.env):**

```bash
GOOGLE_CLIENT_ID=123456789-abc.apps.googleusercontent.com
```

**No mobile:**

```typescript
GoogleSignin.configure({
  webClientId: "123456789-abc.apps.googleusercontent.com", // Mesmo do backend
  iosClientId: "123456789-ios.apps.googleusercontent.com", // Específico iOS
});
```

### 8. **Biblioteca Necessária no Backend**

Para validar tokens do Google:

```gradle
// build.gradle
dependencies {
    implementation 'com.google.api-client:google-api-client:2.2.0'
    implementation 'com.google.http-client:google-http-client-jackson2:1.43.3'
}
```

### 9. **Validação de Token Google (Produção)**

```java
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;

public class GoogleTokenValidator {
    private final GoogleIdTokenVerifier verifier;

    public GoogleTokenValidator(String clientId) {
        this.verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
            .setAudience(Collections.singletonList(clientId))
            .build();
    }

    public GoogleIdToken.Payload verify(String idToken) throws Exception {
        GoogleIdToken token = verifier.verify(idToken);
        if (token == null) {
            throw new RuntimeException("Token inválido");
        }
        return token.getPayload();
    }
}
```

### 10. **Diferenças entre Signup e Signin**

**No OAuth2, Signup e Signin são o MESMO endpoint!**

```
Mobile                 Backend
  │                      │
  │  POST /api/auth/google
  │  { idToken: "..." }  │
  │─────────────────────>│
  │                      │
  │                      ├─ Email existe? NÃO → Cria conta (SIGNUP)
  │                      ├─ Email existe? SIM → Faz login (SIGNIN)
  │                      │
  │  JWT + User Info     │
  │<─────────────────────│
```

**No mobile, você pode:**

- Usar APENAS um botão "Continue with Google" nas duas telas
- Backend decide se cria ou autentica automaticamente

### 11. **Segurança**

✅ **Boas Práticas:**

- Backend SEMPRE valida o token com Google/Facebook
- Nunca confie cegamente no idToken enviado pelo mobile
- Use HTTPS em produção
- Tokens JWT devem ter expiração (configurado no backend)

❌ **NÃO faça:**

- Enviar dados do usuário sem validar token
- Aceitar tokens expirados
- Usar HTTP em produção

### 12. **Exemplo Completo - Fluxo Signup**

```typescript
// SignUpScreen.tsx
export function SignUpScreen() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [name, setName] = useState('');

  // Signup tradicional
  const signUpWithEmail = async () => {
    try {
      const response = await api.post('/api/signup', {
        email,
        password,
        name,
        googleAccount: false
      });

      await saveUserData(response.data);
      navigation.navigate('Home');
    } catch (error) {
      Alert.alert('Erro', error.response.data.error);
    }
  };

  // Signup com Google (serve também como signin!)
  const signUpWithGoogle = async () => {
    try {
      const { idToken } = await GoogleSignin.signIn();

      const response = await api.post('/api/auth/google', { idToken });

      await saveUserData(response.data);
      navigation.navigate('Home');
    } catch (error) {
      Alert.alert('Erro', error.response?.data?.error || 'Erro ao fazer login');
    }
  };

  const saveUserData = async (data) => {
    await AsyncStorage.setItem('accessToken', data.accessToken);
    await AsyncStorage.setItem('user', JSON.stringify({
      id: data.accountId,
      name: data.name,
      email: data.email
    }));
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Create Account</Text>

      {/* Signup tradicional */}
      <TextInput
        placeholder="Name"
        value={name}
        onChangeText={setName}
      />
      <TextInput
        placeholder="Email"
        value={email}
        onChangeText={setEmail}
        keyboardType="email-address"
      />
      <TextInput
        placeholder="Password"
        value={password}
        onChangeText={setPassword}
        secureTextEntry
      />
      <Button title="Sign Up" onPress={signUpWithEmail} />

      <Text style={styles.divider}>or continue with</Text>

      {/* OAuth2 */}
      <TouchableOpacity style={styles.googleButton} onPress={signUpWithGoogle}>
        <Image source={googleIcon} />
        <Text>Continue with Google</Text>
      </TouchableOpacity>

      <Text style={styles.footer}>
        Already have an account?{' '}
        <Text onPress={() => navigation.navigate('SignIn')}>Sign In</Text>
      </Text>
    </View>
  );
}
```

## 🚀 Próximos Passos

1. ✅ Signup/Signin tradicional já funciona
2. ⏳ Implementar `GoogleAuthController` completo com validação de token
3. ⏳ Adicionar rota `/api/auth/google` no `AuthRoutes`
4. ⏳ Configurar Google Cloud Console
5. ⏳ Testar integração mobile → backend

## 📚 Recursos

- [Google Sign-In for Android](https://developers.google.com/identity/sign-in/android/start-integrating)
- [Google Sign-In for iOS](https://developers.google.com/identity/sign-in/ios/start-integrating)
- [React Native Google Sign In](https://github.com/react-native-google-signin/google-signin)
- [Facebook Login for React Native](https://github.com/facebook/react-native-fbsdk-next)
