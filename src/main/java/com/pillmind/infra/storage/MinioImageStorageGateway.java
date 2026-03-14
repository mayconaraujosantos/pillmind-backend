package com.pillmind.infra.storage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.minio.http.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pillmind.data.protocols.storage.ImageStorageGateway;
import com.pillmind.main.config.Env;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.StatObjectArgs;

public class MinioImageStorageGateway implements ImageStorageGateway {
    private static final Logger logger = LoggerFactory.getLogger(MinioImageStorageGateway.class);

    private final MinioClient minioClient;
    private final String bucket;
    private final String publicEndpoint;

    public MinioImageStorageGateway() {
        this(
                MinioClient.builder()
                        .endpoint(Env.MINIO_ENDPOINT)
                        .credentials(Env.MINIO_ACCESS_KEY, Env.MINIO_SECRET_KEY)
                        .build(),
                Env.MINIO_BUCKET,
                Env.MINIO_ENDPOINT);
    }

    public MinioImageStorageGateway(MinioClient minioClient, String bucket, String publicEndpoint) {
        this.minioClient = minioClient;
        this.bucket = bucket;
        this.publicEndpoint = publicEndpoint;
    }

    @Override
    public DirectUploadData createDirectUploadUrl(CreateDirectUploadParams params) {
        String imageId = UUID.randomUUID().toString();
        String objectKey = buildObjectKey(imageId, params);

        try {
            Map<String, String> extraQueryParams = new HashMap<>();
            extraQueryParams.put("X-Amz-Meta-userId", params.userId());
            extraQueryParams.put("X-Amz-Meta-kind", params.kind().name());
            extraQueryParams.put("X-Amz-Meta-fileName", params.fileName());
            extraQueryParams.put("X-Amz-Meta-contentType", params.contentType());

            String uploadUrl = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.PUT)
                            .bucket(bucket)
                            .object(objectKey)
                            .expiry(30, TimeUnit.MINUTES)
                            .extraQueryParams(extraQueryParams)
                            .build());

            logger.info("Created presigned upload URL for image {} in bucket {}", imageId, bucket);
            return new DirectUploadData(imageId, uploadUrl);
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao gerar URL de upload no MinIO", e);
        }
    }

    @Override
    public StoredImageData getImageDetails(String imageId) {
        try {
            var stat = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucket)
                            .object(findObjectKeyByImageId(imageId))
                            .build());

            var userMeta = stat.userMetadata();
            String userId = userMeta.getOrDefault("userid", null);
            String kind = userMeta.getOrDefault("kind", null);

            return new StoredImageData(imageId, true, userId, kind);
        } catch (io.minio.errors.ErrorResponseException e) {
            if ("NoSuchKey".equals(e.errorResponse().code())) {
                return new StoredImageData(imageId, false, null, null);
            }
            throw new IllegalStateException("Falha ao consultar imagem no MinIO", e);
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao consultar imagem no MinIO", e);
        }
    }

    @Override
    public String buildDeliveryUrl(String imageId, String variant) {
        String objectKey = findObjectKeyByImageId(imageId);
        return publicEndpoint + "/" + bucket + "/" + objectKey;
    }

    private String buildObjectKey(String imageId, CreateDirectUploadParams params) {
        String kindFolder = params.kind().name().toLowerCase();
        return kindFolder + "/" + params.userId() + "/" + imageId + "/" + params.fileName();
    }

    private String findObjectKeyByImageId(String imageId) {
        try {
            var results = minioClient.listObjects(
                    io.minio.ListObjectsArgs.builder()
                            .bucket(bucket)
                            .prefix("")
                            .recursive(true)
                            .build());

            for (var result : results) {
                var item = result.get();
                if (item.objectName().contains(imageId)) {
                    return item.objectName();
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao buscar imagem no MinIO", e);
        }

        throw new IllegalStateException("Imagem não encontrada no MinIO: " + imageId);
    }
}
