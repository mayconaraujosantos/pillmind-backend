package com.pillmind.data.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.pillmind.data.protocols.db.UserImageRepository;
import com.pillmind.data.protocols.storage.ImageStorageGateway;
import com.pillmind.data.protocols.storage.ImageStorageGateway.CreateDirectUploadParams;
import com.pillmind.data.protocols.storage.ImageStorageGateway.DirectUploadData;
import com.pillmind.domain.errors.ValidationException;
import com.pillmind.domain.models.ImageKind;
import com.pillmind.domain.models.ImageUploadStatus;
import com.pillmind.domain.models.UserImage;
import com.pillmind.domain.usecases.RequestImageUpload;

class DbRequestImageUploadTest {

    private ImageStorageGateway imageStorageGateway;
    private UserImageRepository userImageRepository;
    private DbRequestImageUpload sut;

    @BeforeEach
    void setUp() {
        imageStorageGateway = mock(ImageStorageGateway.class);
        userImageRepository = mock(UserImageRepository.class);
        sut = new DbRequestImageUpload(imageStorageGateway, userImageRepository);
    }

    private RequestImageUpload.Params makeValidParams() {
        return new RequestImageUpload.Params(
                "user-123",
                ImageKind.PROFILE,
                "avatar.jpg",
                "image/jpeg",
                102400);
    }

    @Test
    void shouldCallImageStorageGatewayWithCorrectParams() {
        when(imageStorageGateway.createDirectUploadUrl(any()))
                .thenReturn(new DirectUploadData("cf-img-1", "https://upload.example.com"));
        when(userImageRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        sut.execute(makeValidParams());

        var captor = ArgumentCaptor.forClass(CreateDirectUploadParams.class);
        verify(imageStorageGateway).createDirectUploadUrl(captor.capture());

        var captured = captor.getValue();
        assertEquals("user-123", captured.userId());
        assertEquals(ImageKind.PROFILE, captured.kind());
        assertEquals("avatar.jpg", captured.fileName());
        assertEquals("image/jpeg", captured.contentType());
    }

    @Test
    void shouldSaveUserImageWithStatusRequested() {
        when(imageStorageGateway.createDirectUploadUrl(any()))
                .thenReturn(new DirectUploadData("cf-img-1", "https://upload.example.com"));
        when(userImageRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        sut.execute(makeValidParams());

        var captor = ArgumentCaptor.forClass(UserImage.class);
        verify(userImageRepository).save(captor.capture());

        var saved = captor.getValue();
        assertNotNull(saved.id());
        assertEquals("user-123", saved.userId());
        assertEquals("cf-img-1", saved.imageId());
        assertEquals(ImageKind.PROFILE, saved.kind());
        assertEquals(ImageUploadStatus.REQUESTED, saved.status());
    }

    @Test
    void shouldReturnImageIdAndUploadUrl() {
        when(imageStorageGateway.createDirectUploadUrl(any()))
                .thenReturn(new DirectUploadData("cf-img-1", "https://upload.example.com"));
        when(userImageRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var result = sut.execute(makeValidParams());

        assertEquals("cf-img-1", result.imageId());
        assertEquals("https://upload.example.com", result.uploadUrl());
    }

    @Test
    void shouldThrowWhenKindIsNull() {
        var params = new RequestImageUpload.Params("user-123", null, "file.jpg", "image/jpeg", 1024);

        assertThrows(ValidationException.class, () -> sut.execute(params));
        verify(imageStorageGateway, never()).createDirectUploadUrl(any());
    }

    @Test
    void shouldThrowWhenFileNameIsBlank() {
        var params = new RequestImageUpload.Params("user-123", ImageKind.PROFILE, "", "image/jpeg", 1024);

        assertThrows(ValidationException.class, () -> sut.execute(params));
        verify(imageStorageGateway, never()).createDirectUploadUrl(any());
    }

    @Test
    void shouldThrowWhenContentTypeIsNotImage() {
        var params = new RequestImageUpload.Params("user-123", ImageKind.PROFILE, "file.txt", "text/plain", 1024);

        assertThrows(ValidationException.class, () -> sut.execute(params));
        verify(imageStorageGateway, never()).createDirectUploadUrl(any());
    }

    @Test
    void shouldThrowWhenSizeIsZero() {
        var params = new RequestImageUpload.Params("user-123", ImageKind.PROFILE, "file.jpg", "image/jpeg", 0);

        assertThrows(ValidationException.class, () -> sut.execute(params));
        verify(imageStorageGateway, never()).createDirectUploadUrl(any());
    }

    @Test
    void shouldThrowWhenSizeExceedsMaxForProfile() {
        long oversized = 4L * 1024 * 1024; // 4MB > 3MB limit for PROFILE
        var params = new RequestImageUpload.Params("user-123", ImageKind.PROFILE, "big.jpg", "image/jpeg", oversized);

        assertThrows(ValidationException.class, () -> sut.execute(params));
        verify(imageStorageGateway, never()).createDirectUploadUrl(any());
    }

    @Test
    void shouldThrowWhenSizeExceedsMaxForMedication() {
        long oversized = 6L * 1024 * 1024; // 6MB > 5MB limit for MEDICATION
        var params = new RequestImageUpload.Params("user-123", ImageKind.MEDICATION, "med.jpg", "image/jpeg",
                oversized);

        assertThrows(ValidationException.class, () -> sut.execute(params));
        verify(imageStorageGateway, never()).createDirectUploadUrl(any());
    }

    @Test
    void shouldAcceptMedicationImageWithinSizeLimit() {
        long validSize = 4L * 1024 * 1024; // 4MB < 5MB limit for MEDICATION
        var params = new RequestImageUpload.Params("user-123", ImageKind.MEDICATION, "med.jpg", "image/jpeg",
                validSize);

        when(imageStorageGateway.createDirectUploadUrl(any()))
                .thenReturn(new DirectUploadData("cf-img-med", "https://upload.example.com/med"));
        when(userImageRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var result = sut.execute(params);

        assertEquals("cf-img-med", result.imageId());
        verify(userImageRepository).save(any());
    }
}
