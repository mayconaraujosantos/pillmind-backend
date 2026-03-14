package com.pillmind.presentation.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import com.pillmind.data.protocols.cryptography.Decrypter;
import com.pillmind.domain.usecases.RequestImageUpload;
import com.pillmind.presentation.handlers.ErrorHandlers;

import io.javalin.testtools.JavalinTest;

class RequestImageUploadControllerTest {

    @Test
    void shouldReturn200WithUploadUrlOnSuccess() {
        var requestImageUpload = mock(RequestImageUpload.class);
        var decrypter = mock(Decrypter.class);

        when(decrypter.decrypt("valid-token")).thenReturn("user-123");
        when(requestImageUpload.execute(any(RequestImageUpload.Params.class)))
                .thenReturn(new RequestImageUpload.Result(
                        "cf-image-1",
                        "https://upload.imagedelivery.net/direct_upload"));

        JavalinTest.test((app, client) -> {
            ErrorHandlers.configure(app);
            app.post("/api/uploads/images/request",
                    new RequestImageUploadController(requestImageUpload, decrypter)::handle);

            var response = client.post("/api/uploads/images/request", """
                    {
                      "kind": "PROFILE",
                      "fileName": "avatar.jpg",
                      "contentType": "image/jpeg",
                      "size": 102400
                    }
                    """, request -> request.header("Authorization", "Bearer valid-token"));

            assertEquals(200, response.code());
            assertTrue(response.body().string().contains("upload.imagedelivery.net"));
            verify(requestImageUpload).execute(any(RequestImageUpload.Params.class));
        });
    }

    @Test
    void shouldReturn401WhenTokenIsMissing() {
        var requestImageUpload = mock(RequestImageUpload.class);
        var decrypter = mock(Decrypter.class);

        JavalinTest.test((app, client) -> {
            ErrorHandlers.configure(app);
            app.post("/api/uploads/images/request",
                    new RequestImageUploadController(requestImageUpload, decrypter)::handle);

            try (var response = client.post("/api/uploads/images/request", """
                    {
                      "kind": "PROFILE",
                      "fileName": "avatar.jpg",
                      "contentType": "image/jpeg",
                      "size": 102400
                    }
                    """)) {
                assertEquals(401, response.code());
            }
        });
    }

    @Test
    void shouldReturn400WhenKindIsInvalid() {
        var requestImageUpload = mock(RequestImageUpload.class);
        var decrypter = mock(Decrypter.class);

        when(decrypter.decrypt("valid-token")).thenReturn("user-123");

        JavalinTest.test((app, client) -> {
            ErrorHandlers.configure(app);
            app.post("/api/uploads/images/request",
                    new RequestImageUploadController(requestImageUpload, decrypter)::handle);

            try (var response = client.post("/api/uploads/images/request", """
                    {
                      "kind": "NOT_SUPPORTED",
                      "fileName": "avatar.jpg",
                      "contentType": "image/jpeg",
                      "size": 102400
                    }
                    """, request -> request.header("Authorization", "Bearer valid-token"))) {
                assertEquals(400, response.code());
            }
        });
    }
}
