package com.pillmind.data.protocols.storage;

import java.io.InputStream;
import java.util.Optional;

/**
 * Armazenamento de objetos (MinIO / S3). Prefixos:
 * <ul>
 *   <li>{@link #PROFILE_PREFIX} — fotos de perfil</li>
 *   <li>{@link #MEDICINE_PREFIX} — imagens de medicamentos (futuro)</li>
 * </ul>
 */
public interface ObjectStorageService {

  String PROFILE_PREFIX = "profiles/";
  String MEDICINE_PREFIX = "medicines/";

  boolean isConfigured();

  record StoredObject(String objectKey, String publicUrl) {}

  /**
   * Stream de um objeto público (ex.: foto de perfil servida via {@code GET /api/media/...}).
   */
  record StreamedObject(String contentType, InputStream inputStream, long sizeBytes)
      implements AutoCloseable {
    @Override
    public void close() throws java.io.IOException {
      inputStream.close();
    }
  }

  /**
   * Abre leitura de objeto no bucket (só chaves sob prefixos públicos acordados).
   *
   * @return vazio se MinIO inativo, chave inválida ou objeto inexistente
   */
  Optional<StreamedObject> openPublicObject(String objectKey);

  /**
   * Envia imagem de perfil e devolve a URL pública ({@link com.pillmind.main.config.Env#MINIO_PUBLIC_BASE_URL}).
   */
  StoredObject putProfileImage(
      InputStream data,
      long sizeBytes,
      String contentType,
      String userId);

  /**
   * Envia imagem de medicamento ({@code medicines/{userId}/...}) e devolve a URL pública.
   */
  StoredObject putMedicineImage(
      InputStream data,
      long sizeBytes,
      String contentType,
      String userId);
}
