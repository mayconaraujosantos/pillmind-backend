package com.pillmind.infra.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.hibernate.cache.spi.support.AbstractReadWriteAccess.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.pillmind.data.protocols.storage.ImageStorageGateway.CreateDirectUploadParams;
import com.pillmind.domain.models.ImageKind;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.errors.ErrorResponseException;
import io.minio.messages.ErrorResponse;
import okhttp3.Headers;

class MinioImageStorageGatewayTest {

    private MinioClient minioClient;
    private MinioImageStorageGateway sut;
    private static final String BUCKET = "test-bucket";
    private static final String ENDPOINT = "http://localhost:9000";

    @BeforeEach
    void setUp() {
        minioClient = mock(MinioClient.class);
        sut = new MinioImageStorageGateway(minioClient, BUCKET, ENDPOINT);
    }

    @Test
    void shouldReturnDirectUploadDataWithPresignedUrl() throws Exception {
        when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class)))
                .thenReturn("http://localhost:9000/test-bucket/profile/user-1/some-id/avatar.jpg?X-Amz-Signature=abc");

        var params = new CreateDirectUploadParams("user-1", ImageKind.PROFILE, "avatar.jpg", "image/jpeg");

        var result = sut.createDirectUploadUrl(params);

        assertNotNull(result.imageId());
        assertFalse(result.imageId().isBlank());
        assertTrue(result.uploadUrl().contains("test-bucket"));
        assertTrue(result.uploadUrl().contains("X-Amz-Signature"));
    }

    @Test
    void shouldGenerateUniqueImageIdOnEachCall() throws Exception {
        when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class)))
                .thenReturn("http://localhost:9000/presigned-url");

        var params = new CreateDirectUploadParams("user-1", ImageKind.PROFILE, "avatar.jpg", "image/jpeg");

        var result1 = sut.createDirectUploadUrl(params);
        var result2 = sut.createDirectUploadUrl(params);

        assertNotNull(result1.imageId());
        assertNotNull(result2.imageId());
        assertFalse(result1.imageId().equals(result2.imageId()));
    }

    @Test
    void shouldThrowWhenPresignedUrlGenerationFails() throws Exception {
        when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class)))
                .thenThrow(new RuntimeException("Connection refused"));

        var params = new CreateDirectUploadParams("user-1", ImageKind.PROFILE, "avatar.jpg", "image/jpeg");

        assertThrows(IllegalStateException.class, () -> sut.createDirectUploadUrl(params));
    }

    @Test
    void shouldReturnUploadedTrueWhenObjectExists() throws Exception {
        var headers = new Headers.Builder()
                .add("X-Amz-Meta-userid", "user-1")
                .add("X-Amz-Meta-kind", "PROFILE")
                .build();

        var stat = new StatObjectResponse(headers, BUCKET, "", "profile/user-1/img-1/avatar.jpg");

        when(minioClient.statObject(any(StatObjectArgs.class))).thenReturn(stat);

        // Mock listObjects to find the object by imageId
        var item = mock(Item.class);
        when(item.objectName()).thenReturn("profile/user-1/img-1/avatar.jpg");
        var resultItem = mock(Result.class);
        when(resultItem.get()).thenReturn(item);

        @SuppressWarnings("unchecked")
        Iterable<Result<Item>> iterable = () -> Collections.singletonList(resultItem).iterator();
        when(minioClient.listObjects(any(ListObjectsArgs.class))).thenReturn(iterable);

        var result = sut.getImageDetails("img-1");

        assertEquals("img-1", result.imageId());
        assertTrue(result.uploaded());
        assertEquals("user-1", result.metadataUserId());
        assertEquals("PROFILE", result.metadataKind());
    }

    @Test
    void shouldReturnUploadedFalseWhenObjectNotFound() throws Exception {
        // Mock listObjects to find the key
        var item = mock(Item.class);
        when(item.objectName()).thenReturn("profile/user-1/img-1/avatar.jpg");
        var resultItem = mock(Result.class);
        when(resultItem.get()).thenReturn(item);

        @SuppressWarnings("unchecked")
        Iterable<Result<Item>> iterable = () -> Collections.singletonList(resultItem).iterator();
        when(minioClient.listObjects(any(ListObjectsArgs.class))).thenReturn(iterable);

        var errorResponse = new ErrorResponse("NoSuchKey", "Object not found", BUCKET,
                "profile/user-1/img-1/avatar.jpg", "", "", "");
        when(minioClient.statObject(any(StatObjectArgs.class)))
                .thenThrow(new ErrorResponseException(errorResponse, null, ""));

        var result = sut.getImageDetails("img-1");

        assertEquals("img-1", result.imageId());
        assertFalse(result.uploaded());
    }

    @Test
    void shouldBuildDeliveryUrlWithBucketAndObjectKey() {
        // Mock listObjects
        var item = mock(Item.class);
        when(item.objectName()).thenReturn("profile/user-1/img-1/avatar.jpg");
        var resultItem = mock(Result.class);
        try {
            when(resultItem.get()).thenReturn(item);
        } catch (Exception ignored) {
        }

        @SuppressWarnings("unchecked")
        Iterable<Result<Item>> iterable = () -> Collections.singletonList(resultItem).iterator();
        when(minioClient.listObjects(any(ListObjectsArgs.class))).thenReturn(iterable);

        var url = sut.buildDeliveryUrl("img-1", "public");

        assertEquals("http://localhost:9000/test-bucket/profile/user-1/img-1/avatar.jpg", url);
    }
}
