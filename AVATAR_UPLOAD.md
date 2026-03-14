# Sistema de Upload de Avatar

Sistema completo de upload de imagem de avatar para usuários.

## 📋 Funcionalidades

- ✅ Upload via câmera (tirar foto)
- ✅ Upload via galeria (selecionar foto)
- ✅ Validação de tamanho (máx 5MB)
- ✅ Validação de formato (jpg, jpeg, png, gif, webp)
- ✅ Nomes únicos de arquivos (UUID)
- ✅ URL pública retornada
- ✅ Atualização automática do perfil

## 🔧 Backend

### Endpoint

**POST** `/api/profile/avatar`

**Headers:**

```
Authorization: Bearer <token>
Content-Type: multipart/form-data
```

**Body (form-data):**

```
avatar: <file>
```

**Response (200 OK):**

```json
{
  "pictureUrl": "http://localhost:8080/uploads/avatars/550e8400-e29b-41d4-a716-446655440000.jpg"
}
```

**Erros:**

- `400` - Arquivo inválido ou muito grande
- `401` - Token ausente ou inválido
- `500` - Erro ao salvar arquivo

### Validações

- **Tamanho máximo:** 5 MB
- **Formatos aceitos:** .jpg, .jpeg, .png, .gif, .webp
- **Content-Type:** deve começar com `image/`

### Armazenamento

- **Diretório:** `uploads/avatars/`
- **Nome do arquivo:** UUID + extensão original
- **Acesso:** Público via `/uploads/avatars/{filename}`

### Configuração

Variáveis de ambiente (opcional):

```env
BASE_URL=http://localhost:8080
```

## 📱 Frontend

### Fluxo de Upload

1. Usuário clica no botão da câmera
2. Escolhe entre câmera ou galeria
3. Seleciona/captura imagem
4. Imagem é exibida no avatar (preview local)
5. Ao salvar perfil:
   - Upload da imagem (se houver)
   - Atualização dos dados do perfil
   - Atualização do contexto do usuário

### Código

```typescript
// Upload avatar
const uploadResponse = await profileService.uploadAvatar(authContext.token, avatarUri);

// Update profile with new avatar URL
const response = await profileService.updateProfile(authContext.token, updateData);
```

### Permissões Necessárias

**iOS:** `Info.plist`

```xml
<key>NSCameraUsageDescription</key>
<string>Precisamos de acesso à câmera para tirar fotos do seu perfil</string>
<key>NSPhotoLibraryUsageDescription</key>
<string>Precisamos de acesso à galeria para selecionar fotos do seu perfil</string>
```

**Android:** `AndroidManifest.xml`

```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
```

## 🧪 Testando

### Backend (cURL)

```bash
# Upload de avatar
curl -X POST http://localhost:8080/api/profile/avatar \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -F "avatar=@/path/to/image.jpg"
```

### Frontend

1. Faça login no app
2. Vá para editar perfil
3. Clique no ícone da câmera
4. Escolha "Take Photo" ou "Choose from Gallery"
5. Selecione/capture foto
6. Clique em "Save"

## 📂 Estrutura de Arquivos

```
pillmind-backend/
├── uploads/
│   └── avatars/
│       ├── .gitkeep
│       └── 550e8400-e29b-41d4-a716-446655440000.jpg
└── src/main/java/com/pillmind/
    └── presentation/controllers/
        └── UploadAvatarController.java

pillmind/
└── src/features/account/
    ├── domain/
    │   └── services/
    │       └── profile.service.ts (uploadAvatar method)
    └── presentation/
        └── screens/
            └── EditProfileScreen.tsx
```

## 🔒 Segurança

- ✅ Autenticação obrigatória (JWT token)
- ✅ Validação de tipo de arquivo
- ✅ Limite de tamanho de arquivo (5MB)
- ✅ Nomes de arquivo únicos (UUID)
- ✅ Content-Type validation
- ⚠️ **TODO:** Adicionar rate limiting
- ⚠️ **TODO:** Scan de vírus/malware

## 🚀 Melhorias Futuras

- [ ] Integração com AWS S3 / Google Cloud Storage
- [ ] Redimensionamento automático de imagens
- [ ] WebP conversion para otimização
- [ ] CDN para servir imagens
- [ ] Thumbnail generation
- [ ] Rate limiting por usuário
- [ ] Scan de vírus em uploads
