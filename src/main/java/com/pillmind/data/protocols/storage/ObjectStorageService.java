package com.pillmind.data.protocols.storage;

import java.io.InputStream;

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
   * Envia imagem de perfil e devolve a URL pública ({@link com.pillmind.main.config.Env#MINIO_PUBLIC_BASE_URL}).
   */
  StoredObject putProfileImage(
      InputStream data,
      long sizeBytes,
      String contentType,
      String userId);
}
