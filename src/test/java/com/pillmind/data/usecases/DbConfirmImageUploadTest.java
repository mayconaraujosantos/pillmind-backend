package com.pillmind.data.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.pillmind.data.protocols.db.UserImageRepository;
import com.pillmind.data.protocols.storage.ImageStorageGateway;
import com.pillmind.data.protocols.storage.ImageStorageGateway.StoredImageData;
import com.pillmind.domain.errors.UnauthorizedException;
import com.pillmind.domain.errors.ValidationException;
import com.pillmind.domain.models.ImageKind;
import com.pillmind.domain.models.ImageUploadStatus;
import com.pillmind.domain.models.UserImage;
import com.pillmind.domain.usecases.ConfirmImageUpload;

class DbConfirmImageUploadTest {

    private ImageStorageGateway imageStorageGateway;
    private UserImageRepository userImageRepository;
    private DbConfirmImageUpload sut;

    @BeforeEach
    void setUp() {
        imageStorageGateway = mock(ImageStorageGateway.class);
        userImageRepository = mock(UserImageRepository.class);
        sut = new DbConfirmImageUpload(imageStorageGateway, userImageRepository);
    }

    private ConfirmImageUpload.Params makeValidParams() {
        return new ConfirmImageUpload.Params("user-123", ImageKind.PROFILE, "cf-img-1");
    }

    private StoredImageData makeUploadedImageDetails() {
        return new StoredImageData("cf-img-1", true, "user-123", "PROFILE");
    }

    @Test
    void shouldCallGetImageDetailsWithCorrectImageId() {
        when(imageStorageGateway.getImageDetails("cf-img-1")).thenReturn(makeUploadedImageDetails());
        when(imageStorageGateway.buildDeliveryUrl(eq("cf-img-1"), any()))
                .thenReturn("https://delivery.example.com/cf-img-1/profile");
        when(userImageRepository.findByImageId("cf-img-1")).thenReturn(Optional.empty());
        when(userImageRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        sut.execute(makeValidParams());

        verify(imageStorageGateway).getImageDetails("cf-img-1");
    }

    @Test
    void shouldThrowWhenImageIdIsBlank() {
        var params = new ConfirmImageUpload.Params("user-123", ImageKind.PROFILE, "");

        assertThrows(ValidationException.class, () -> sut.execute(params));
        verify(imageStorageGateway, never()).getImageDetails(any());
    }

    @Test
    void shouldThrowWhenImageIdIsNull() {
        var params = new ConfirmImageUpload.Params("user-123", ImageKind.PROFILE, null);

        assertThrows(ValidationException.class, () -> sut.execute(params));
        verify(imageStorageGateway, never()).getImageDetails(any());
    }

    @Test
    void shouldThrowWhenImageIsNotYetUploaded() {
        var notUploaded = new StoredImageData("cf-img-1", false, "user-123", "PROFILE");
        when(imageStorageGateway.getImageDetails("cf-img-1")).thenReturn(notUploaded);

        assertThrows(ValidationException.class, () -> sut.execute(makeValidParams()));
        verify(imageStorageGateway, never()).buildDeliveryUrl(any(), any());
    }

    @Test
    void shouldThrowWhenUserIdDoesNotMatchMetadata() {
        var differentUser = new StoredImageData("cf-img-1", true, "other-user", "PROFILE");
        when(imageStorageGateway.getImageDetails("cf-img-1")).thenReturn(differentUser);

        assertThrows(UnauthorizedException.class, () -> sut.execute(makeValidParams()));
    }

    @Test
    void shouldThrowWhenKindDoesNotMatchMetadata() {
        var wrongKind = new StoredImageData("cf-img-1", true, "user-123", "MEDICATION");
        when(imageStorageGateway.getImageDetails("cf-img-1")).thenReturn(wrongKind);

        assertThrows(ValidationException.class, () -> sut.execute(makeValidParams()));
    }

    @Test
    void shouldBuildDeliveryUrlWithCorrectVariant() {
        when(imageStorageGateway.getImageDetails("cf-img-1")).thenReturn(makeUploadedImageDetails());
        when(imageStorageGateway.buildDeliveryUrl("cf-img-1", "profile"))
                .thenReturn("https://delivery.example.com/cf-img-1/profile");
        when(userImageRepository.findByImageId("cf-img-1")).thenReturn(Optional.empty());
        when(userImageRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        sut.execute(makeValidParams());

        verify(imageStorageGateway).buildDeliveryUrl("cf-img-1", "profile");
    }

    @Test
    void shouldReturnImageIdAndDeliveryUrl() {
        when(imageStorageGateway.getImageDetails("cf-img-1")).thenReturn(makeUploadedImageDetails());
        when(imageStorageGateway.buildDeliveryUrl("cf-img-1", "profile"))
                .thenReturn("https://delivery.example.com/cf-img-1/profile");
        when(userImageRepository.findByImageId("cf-img-1")).thenReturn(Optional.empty());
        when(userImageRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var result = sut.execute(makeValidParams());

        assertEquals("cf-img-1", result.imageId());
        assertEquals("https://delivery.example.com/cf-img-1/profile", result.deliveryUrl());
    }

    @Test
    void shouldSaveNewUserImageWhenNoneExistsInRepository() {
        when(imageStorageGateway.getImageDetails("cf-img-1")).thenReturn(makeUploadedImageDetails());
        when(imageStorageGateway.buildDeliveryUrl("cf-img-1", "profile"))
                .thenReturn("https://delivery.example.com/cf-img-1/profile");
        when(userImageRepository.findByImageId("cf-img-1")).thenReturn(Optional.empty());
        when(userImageRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        sut.execute(makeValidParams());

        var captor = ArgumentCaptor.forClass(UserImage.class);
        verify(userImageRepository).save(captor.capture());

        var saved = captor.getValue();
        assertEquals("user-123", saved.userId());
        assertEquals("cf-img-1", saved.imageId());
        assertEquals(ImageKind.PROFILE, saved.kind());
        assertEquals(ImageUploadStatus.CONFIRMED, saved.status());
        assertEquals("https://delivery.example.com/cf-img-1/profile", saved.deliveryUrl());
    }

    @Test
    void shouldUpdateExistingUserImageWhenFoundInRepository() {
        var existing = new UserImage(
                "existing-id",
                "user-123",
                "cf-img-1",
                ImageKind.PROFILE,
                null,
                ImageUploadStatus.REQUESTED);

        when(imageStorageGateway.getImageDetails("cf-img-1")).thenReturn(makeUploadedImageDetails());
        when(imageStorageGateway.buildDeliveryUrl("cf-img-1", "profile"))
                .thenReturn("https://delivery.example.com/cf-img-1/profile");
        when(userImageRepository.findByImageId("cf-img-1")).thenReturn(Optional.of(existing));
        when(userImageRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        sut.execute(makeValidParams());

        var captor = ArgumentCaptor.forClass(UserImage.class);
        verify(userImageRepository).save(captor.capture());

        var saved = captor.getValue();
        assertEquals("existing-id", saved.id());
        assertEquals(ImageUploadStatus.CONFIRMED, saved.status());
        assertEquals("https://delivery.example.com/cf-img-1/profile", saved.deliveryUrl());
    }

    @Test
    void shouldUseMedicationDefaultVariant() {
        var params = new ConfirmImageUpload.Params("user-123", ImageKind.MEDICATION, "cf-img-med");
        var details = new StoredImageData("cf-img-med", true, "user-123", "MEDICATION");

        when(imageStorageGateway.getImageDetails("cf-img-med")).thenReturn(details);
        when(imageStorageGateway.buildDeliveryUrl("cf-img-med", "public"))
                .thenReturn("https://delivery.example.com/cf-img-med/public");
        when(userImageRepository.findByImageId("cf-img-med")).thenReturn(Optional.empty());
        when(userImageRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var result = sut.execute(params);

        verify(imageStorageGateway).buildDeliveryUrl("cf-img-med", "public");
        assertEquals("https://delivery.example.com/cf-img-med/public", result.deliveryUrl());
    }

    @Test
    void shouldThrowWhenMetadataUserIdIsNull() {
        var nullUserId = new StoredImageData("cf-img-1", true, null, "PROFILE");
        when(imageStorageGateway.getImageDetails("cf-img-1")).thenReturn(nullUserId);

        assertThrows(UnauthorizedException.class, () -> sut.execute(makeValidParams()));
    }

    @Test
    void shouldThrowWhenMetadataKindIsNull() {
        var nullKind = new StoredImageData("cf-img-1", true, "user-123", null);
        when(imageStorageGateway.getImageDetails("cf-img-1")).thenReturn(nullKind);

        assertThrows(ValidationException.class, () -> sut.execute(makeValidParams()));
    }
}
