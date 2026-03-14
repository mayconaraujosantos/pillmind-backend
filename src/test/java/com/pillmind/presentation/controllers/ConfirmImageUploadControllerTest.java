package com.pillmind.presentation.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.pillmind.data.protocols.cryptography.Decrypter;
import com.pillmind.domain.models.Gender;
import com.pillmind.domain.models.User;
import com.pillmind.domain.usecases.ConfirmImageUpload;
import com.pillmind.domain.usecases.LoadUserById;
import com.pillmind.domain.usecases.UpdateUserProfile;
import com.pillmind.presentation.handlers.ErrorHandlers;

import io.javalin.testtools.JavalinTest;

class ConfirmImageUploadControllerTest {

    private User makeUser(String id, String pictureUrl) {
        return new User(
                id,
                "John Doe",
                "john@example.com",
                LocalDate.of(1990, 1, 1),
                Gender.MALE,
                pictureUrl,
                true,
                LocalDateTime.now(),
                LocalDateTime.now());
    }

    @Test
    void shouldReturn200AndUpdateProfilePictureWhenKindIsProfile() {
        var confirmImageUpload = mock(ConfirmImageUpload.class);
        var updateUserProfile = mock(UpdateUserProfile.class);
        var loadUserById = mock(LoadUserById.class);
        var decrypter = mock(Decrypter.class);

        when(decrypter.decrypt("valid-token")).thenReturn("user-123");
        when(confirmImageUpload.execute(any(ConfirmImageUpload.Params.class)))
                .thenReturn(new ConfirmImageUpload.Result(
                        "cf-image-1",
                        "https://imagedelivery.net/hash/cf-image-1/public"));

        var existingUser = makeUser("user-123", "https://old-picture");
        var updatedUser = makeUser("user-123", "https://imagedelivery.net/hash/cf-image-1/public");
        when(loadUserById.execute(any(LoadUserById.Params.class))).thenReturn(existingUser);
        when(updateUserProfile.execute(any(UpdateUserProfile.Params.class))).thenReturn(updatedUser);

        JavalinTest.test((app, client) -> {
            ErrorHandlers.configure(app);
            app.post("/api/uploads/images/confirm",
                    new ConfirmImageUploadController(confirmImageUpload, updateUserProfile, loadUserById,
                            decrypter)::handle);

            var response = client.post("/api/uploads/images/confirm", """
                    {
                      "kind": "PROFILE",
                      "imageId": "cf-image-1"
                    }
                    """, request -> request.header("Authorization", "Bearer valid-token"));

            assertEquals(200, response.code());
            assertTrue(response.body().string().contains("imagedelivery.net"));
            verify(updateUserProfile).execute(any(UpdateUserProfile.Params.class));
        });
    }

    @Test
    void shouldReturn200AndNotUpdateProfileWhenKindIsMedication() {
        var confirmImageUpload = mock(ConfirmImageUpload.class);
        var updateUserProfile = mock(UpdateUserProfile.class);
        var loadUserById = mock(LoadUserById.class);
        var decrypter = mock(Decrypter.class);

        when(decrypter.decrypt("valid-token")).thenReturn("user-123");
        when(confirmImageUpload.execute(any(ConfirmImageUpload.Params.class)))
                .thenReturn(new ConfirmImageUpload.Result(
                        "cf-image-med-1",
                        "https://imagedelivery.net/hash/cf-image-med-1/public"));

        JavalinTest.test((app, client) -> {
            ErrorHandlers.configure(app);
            app.post("/api/uploads/images/confirm",
                    new ConfirmImageUploadController(confirmImageUpload, updateUserProfile, loadUserById,
                            decrypter)::handle);

            try (var response = client.post("/api/uploads/images/confirm", """
                    {
                      "kind": "MEDICATION",
                      "imageId": "cf-image-med-1"
                    }
                    """, request -> request.header("Authorization", "Bearer valid-token"))) {
                assertEquals(200, response.code());
            }

            verify(updateUserProfile, never()).execute(any(UpdateUserProfile.Params.class));
        });
    }
}
