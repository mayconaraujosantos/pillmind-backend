package com.pillmind.infra.storage.minio;

import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pillmind.data.protocols.storage.ObjectStorageService;
import com.pillmind.domain.errors.ServiceUnavailableException;
import com.pillmind.main.config.Env;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.SetBucketPolicyArgs;
import io.minio.StatObjectArgs;

/**
 * Cliente MinIO para uploads. Garante bucket e política de leitura pública no objeto.
 */
public class MinioObjectStorageService implements ObjectStorageService {
  private static final Logger logger = LoggerFactory.getLogger(MinioObjectStorageService.class);

  private final MinioClient client;
  private final String bucket;
  private final String publicBaseUrl;
  private volatile boolean bucketPrepared;

  public MinioObjectStorageService() {
    if (!Env.MINIO_ENABLED) {
      this.client = null;
      this.bucket = null;
      this.publicBaseUrl = null;
      return;
    }
    this.bucket = Env.MINIO_BUCKET;
    this.publicBaseUrl = Env.MINIO_PUBLIC_BASE_URL;
    this.client = MinioClient.builder()
        .endpoint(Env.MINIO_ENDPOINT)
        .credentials(Env.MINIO_ACCESS_KEY, Env.MINIO_SECRET_KEY)
        .region(Env.MINIO_REGION)
        .build();
    logger.info("MinIO client configured: endpoint={}, bucket={}", Env.MINIO_ENDPOINT, bucket);
  }

  @Override
  public boolean isConfigured() {
    return Env.MINIO_ENABLED && client != null;
  }

  @Override
  public Optional<ObjectStorageService.StreamedObject> openPublicObject(String objectKey) {
    if (!isConfigured()) {
      return Optional.empty();
    }
    String key = objectKey == null ? "" : objectKey.strip();
    if (!isAllowedPublicObjectKey(key)) {
      return Optional.empty();
    }
    try {
      ensureBucketAndPolicy();
      var stat = client.statObject(
          StatObjectArgs.builder().bucket(bucket).object(key).build());
      var stream = client.getObject(
          GetObjectArgs.builder().bucket(bucket).object(key).build());
      String ct = stat.contentType();
      if (ct == null || ct.isBlank()) {
        ct = "application/octet-stream";
      }
      long len = stat.size();
      return Optional.of(
          new ObjectStorageService.StreamedObject(ct, stream, len >= 0 ? len : 0L));
    } catch (Exception e) {
      logger.debug("MinIO openPublicObject key={}: {}", key, e.getMessage());
      return Optional.empty();
    }
  }

  private static boolean isAllowedPublicObjectKey(String key) {
    if (key.isEmpty() || key.contains("..")) {
      return false;
    }
    return key.startsWith(ObjectStorageService.PROFILE_PREFIX)
        || key.startsWith(ObjectStorageService.MEDICINE_PREFIX);
  }

  @Override
  public StoredObject putProfileImage(
      InputStream data,
      long sizeBytes,
      String contentType,
      String userId) {
    if (!isConfigured()) {
      throw new ServiceUnavailableException(
          "Armazenamento de arquivos não está habilitado (MINIO_ENABLED=false ou não configurado).");
    }
    ensureBucketAndPolicy();
    String ext = extensionForContentType(contentType);
    String objectKey = ObjectStorageService.PROFILE_PREFIX + userId + "/" + UUID.randomUUID() + ext;
    try {
      client.putObject(
          PutObjectArgs.builder()
              .bucket(bucket)
              .object(objectKey)
              .stream(data, sizeBytes, -1)
              .contentType(contentType)
              .build());
    } catch (Exception e) {
      logger.error("MinIO putObject failed: {}", e.getMessage(), e);
      throw new ServiceUnavailableException("Falha ao enviar imagem para o armazenamento.", e);
    }
    String url = publicBaseUrl + "/" + objectKey;
    logger.info("Profile image stored: key={}", objectKey);
    return new StoredObject(objectKey, url);
  }

  @Override
  public StoredObject putMedicineImage(
      InputStream data,
      long sizeBytes,
      String contentType,
      String userId) {
    if (!isConfigured()) {
      throw new ServiceUnavailableException(
          "Armazenamento de arquivos não está habilitado (MINIO_ENABLED=false ou não configurado).");
    }
    ensureBucketAndPolicy();
    String ext = extensionForContentType(contentType);
    String objectKey =
        ObjectStorageService.MEDICINE_PREFIX + userId + "/" + UUID.randomUUID() + ext;
    try {
      client.putObject(
          PutObjectArgs.builder()
              .bucket(bucket)
              .object(objectKey)
              .stream(data, sizeBytes, -1)
              .contentType(contentType)
              .build());
    } catch (Exception e) {
      logger.error("MinIO putMedicineImage failed: {}", e.getMessage(), e);
      throw new ServiceUnavailableException("Falha ao enviar imagem do medicamento.", e);
    }
    String url = publicBaseUrl + "/" + objectKey;
    logger.info("Medicine image stored: key={}", objectKey);
    return new StoredObject(objectKey, url);
  }

  private void ensureBucketAndPolicy() {
    if (bucketPrepared) {
      return;
    }
    synchronized (this) {
      if (bucketPrepared) {
        return;
      }
      try {
        boolean exists = client.bucketExists(
            BucketExistsArgs.builder().bucket(bucket).build());
        if (!exists) {
          client.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
          logger.info("MinIO bucket created: {}", bucket);
        }
        String policy = publicReadPolicy(bucket);
        client.setBucketPolicy(
            SetBucketPolicyArgs.builder().bucket(bucket).config(policy).build());
        logger.debug("MinIO bucket policy set for public read: {}", bucket);
      } catch (Exception e) {
        logger.error("MinIO bucket setup failed: {}", e.getMessage(), e);
        throw new ServiceUnavailableException("Não foi possível preparar o bucket MinIO.", e);
      }
      bucketPrepared = true;
    }
  }

  private static String publicReadPolicy(String bucketName) {
    String resource = "arn:aws:s3:::" + bucketName + "/*";
    return """
        {
          "Version": "2012-10-17",
          "Statement": [
            {
              "Effect": "Allow",
              "Principal": {"AWS": ["*"]},
              "Action": ["s3:GetObject"],
              "Resource": ["%s"]
            }
          ]
        }
        """.formatted(resource);
  }

  private static String extensionForContentType(String contentType) {
    if (contentType == null) {
      return ".bin";
    }
    return switch (contentType.toLowerCase()) {
      case "image/jpeg", "image/jpg" -> ".jpg";
      case "image/png" -> ".png";
      case "image/webp" -> ".webp";
      default -> ".bin";
    };
  }
}
