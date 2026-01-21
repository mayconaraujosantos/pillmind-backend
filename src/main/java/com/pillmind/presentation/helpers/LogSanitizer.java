package com.pillmind.presentation.helpers;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Utilitário para sanitizar dados sensíveis em logs
 * Remove/mascara campos como senha, tokens, etc.
 */
public class LogSanitizer {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Lista de campos sensíveis que devem ser mascarados
    private static final List<String> SENSITIVE_FIELDS = Arrays.asList(
            "password",
            "token",
            "accessToken",
            "refreshToken",
            "secret",
            "apiKey",
            "authorization");

    /**
     * Sanitiza o body de uma requisição removendo campos sensíveis
     * 
     * @param body JSON string do body da requisição
     * @return JSON string sanitizado ou mensagem de erro
     */
    public static String sanitizeRequestBody(String body) {
        if (body == null || body.isBlank()) {
            return "[body vazio]";
        }

        try {
            JsonNode rootNode = objectMapper.readTree(body);

            if (rootNode.isObject()) {
                ObjectNode sanitized = sanitizeObject((ObjectNode) rootNode.deepCopy());
                return objectMapper.writeValueAsString(sanitized);
            }

            return body;
        } catch (Exception e) {
            // Se não conseguir parsear, retorna mensagem segura
            return "[body não pode ser parseado]";
        }
    }

    /**
     * Cria um log DTO seguro para signup
     */
    public static String sanitizeSignUpLog(String name, String email) {
        return String.format("{\"name\":\"%s\",\"email\":\"%s\",\"password\":\"***\"}",
                sanitizeString(name), sanitizeString(email));
    }

    /**
     * Cria um log DTO seguro para signin
     */
    public static String sanitizeSignInLog(String email) {
        return String.format("{\"email\":\"%s\",\"password\":\"***\"}", sanitizeString(email));
    }

    /**
     * Sanitiza um objeto JSON recursivamente
     */
    private static ObjectNode sanitizeObject(ObjectNode node) {
        node.fieldNames().forEachRemaining(fieldName -> {
            if (SENSITIVE_FIELDS.stream().anyMatch(sf -> fieldName.toLowerCase().contains(sf.toLowerCase()))) {
                node.put(fieldName, "***");
            } else if (node.get(fieldName).isObject()) {
                node.set(fieldName, sanitizeObject((ObjectNode) node.get(fieldName)));
            }
        });
        return node;
    }

    /**
     * Sanitiza uma string para evitar injection em logs
     */
    private static String sanitizeString(String value) {
        if (value == null) {
            return "";
        }
        // Remove caracteres de controle que podem quebrar logs
        return value.replaceAll("[\n\r\t]", " ").replaceAll("\"", "\\\\\"");
    }
}
