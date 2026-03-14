package com.pillmind.infra.storage;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pillmind.data.protocols.storage.ImageStorageGateway;
import com.pillmind.main.config.Env;

public class CloudflareImagesGateway implements ImageStorageGateway {
    private static final String API_BASE = "https://api.cloudflare.com/client/v4/accounts/";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public CloudflareImagesGateway() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public DirectUploadData createDirectUploadUrl(CreateDirectUploadParams params) {
        ensureConfigured();

        String endpoint = API_BASE + Env.CLOUDFLARE_ACCOUNT_ID + "/images/v2/direct_upload";

        String body;
        try {
            body = objectMapper.writeValueAsString(Map.of(
                    "requireSignedURLs", false,
                    "metadata", Map.of(
                            "userId", params.userId(),
                            "kind", params.kind().name(),
                            "fileName", params.fileName(),
                            "contentType", params.contentType())));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Erro ao serializar payload para Cloudflare", e);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Authorization", "Bearer " + Env.CLOUDFLARE_IMAGES_API_TOKEN)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        JsonNode result = executeRequest(request);
        String imageId = readRequired(result, "id");
        String uploadUrl = readRequired(result, "uploadURL");
        return new DirectUploadData(imageId, uploadUrl);
    }

    @Override
    public StoredImageData getImageDetails(String imageId) {
        ensureConfigured();

        String endpoint = API_BASE + Env.CLOUDFLARE_ACCOUNT_ID + "/images/v1/" + imageId;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Authorization", "Bearer " + Env.CLOUDFLARE_IMAGES_API_TOKEN)
                .GET()
                .build();

        JsonNode result = executeRequest(request);

        boolean uploaded = !result.path("draft").asBoolean(false);
        JsonNode metadata = result.path("meta");

        return new StoredImageData(
                readRequired(result, "id"),
                uploaded,
                metadata.path("userId").asText(null),
                metadata.path("kind").asText(null));
    }

    @Override
    public String buildDeliveryUrl(String imageId, String variant) {
        ensureConfigured();
        return Env.CLOUDFLARE_IMAGES_DELIVERY_BASE_URL + "/" + imageId + "/" + variant;
    }

    private JsonNode executeRequest(HttpRequest request) {
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode root = objectMapper.readTree(response.body());

            if (response.statusCode() < 200 || response.statusCode() >= 300 || !root.path("success").asBoolean(false)) {
                throw new IllegalStateException("Cloudflare Images request failed: " + response.body());
            }

            return root.path("result");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Falha ao comunicar com Cloudflare Images", e);
        } catch (IOException e) {
            throw new IllegalStateException("Falha ao comunicar com Cloudflare Images", e);
        }
    }

    private String readRequired(JsonNode node, String fieldName) {
        String value = node.path(fieldName).asText(null);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Resposta inválida do Cloudflare: campo ausente " + fieldName);
        }
        return value;
    }

    private void ensureConfigured() {
        if (Env.CLOUDFLARE_ACCOUNT_ID.isBlank() || Env.CLOUDFLARE_IMAGES_API_TOKEN.isBlank()
                || Env.CLOUDFLARE_IMAGES_DELIVERY_BASE_URL.isBlank()) {
            throw new IllegalStateException(
                    "Cloudflare Images não configurado. Defina CLOUDFLARE_ACCOUNT_ID, CLOUDFLARE_IMAGES_API_TOKEN e CLOUDFLARE_IMAGES_DELIVERY_BASE_URL");
        }
    }
}
