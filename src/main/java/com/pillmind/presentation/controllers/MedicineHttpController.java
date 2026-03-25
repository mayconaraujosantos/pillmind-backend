package com.pillmind.presentation.controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pillmind.data.protocols.cryptography.Decrypter;
import com.pillmind.domain.errors.UnauthorizedException;
import com.pillmind.domain.errors.ValidationException;
import com.pillmind.domain.models.Medicine;
import com.pillmind.domain.usecases.CreateMedicineForUser;
import com.pillmind.domain.usecases.DeleteMedicineForUser;
import com.pillmind.domain.usecases.GetMedicineForUser;
import com.pillmind.domain.usecases.ListMedicinesForUser;
import com.pillmind.domain.usecases.UpdateMedicineForUser;
import com.pillmind.presentation.helpers.AccessTokenExtractor;
import com.pillmind.presentation.helpers.HttpHelper;

import io.javalin.http.Context;
import io.javalin.openapi.HttpMethod;
import io.javalin.openapi.OpenApi;
import io.javalin.openapi.OpenApiContent;
import io.javalin.openapi.OpenApiParam;
import io.javalin.openapi.OpenApiRequestBody;
import io.javalin.openapi.OpenApiResponse;
import io.javalin.openapi.OpenApiSecurity;

/**
 * CRUD HTTP de medicamentos do usuário autenticado.
 */
public class MedicineHttpController {

    private final Decrypter decrypter;
    private final ObjectMapper objectMapper;
    private final ListMedicinesForUser listMedicinesForUser;
    private final GetMedicineForUser getMedicineForUser;
    private final CreateMedicineForUser createMedicineForUser;
    private final UpdateMedicineForUser updateMedicineForUser;
    private final DeleteMedicineForUser deleteMedicineForUser;

    public MedicineHttpController(
            Decrypter decrypter,
            ListMedicinesForUser listMedicinesForUser,
            GetMedicineForUser getMedicineForUser,
            CreateMedicineForUser createMedicineForUser,
            UpdateMedicineForUser updateMedicineForUser,
            DeleteMedicineForUser deleteMedicineForUser) {
        this.decrypter = decrypter;
        this.listMedicinesForUser = listMedicinesForUser;
        this.getMedicineForUser = getMedicineForUser;
        this.createMedicineForUser = createMedicineForUser;
        this.updateMedicineForUser = updateMedicineForUser;
        this.deleteMedicineForUser = deleteMedicineForUser;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @OpenApi(
            path = "/api/medicines",
            methods = HttpMethod.GET,
            summary = "Listar medicamentos",
            description = "Lista medicamentos do usuário autenticado (Bearer ou x-access-token).",
            tags = { "Medicines" },
            operationId = "listMedicines",
            security = { @OpenApiSecurity(name = "bearerAuth") },
            responses = {
                    @OpenApiResponse(status = "200", content = {
                            @OpenApiContent(from = MedicineJsonResponse[].class) }),
                    @OpenApiResponse(status = "401")
            })
    public void list(Context ctx) {
        String userId = resolveUserId(ctx);
        var items = listMedicinesForUser.execute(new ListMedicinesForUser.Params(userId));
        HttpHelper.ok(ctx, items.stream().map(MedicineJsonResponse::from).toList());
    }

    @OpenApi(
            path = "/api/medicines/{id}",
            methods = HttpMethod.GET,
            summary = "Obter medicamento por id",
            tags = { "Medicines" },
            operationId = "getMedicineById",
            pathParams = { @OpenApiParam(name = "id", required = true, description = "ID do medicamento") },
            security = { @OpenApiSecurity(name = "bearerAuth") },
            responses = {
                    @OpenApiResponse(status = "200", content = {
                            @OpenApiContent(from = MedicineJsonResponse.class) }),
                    @OpenApiResponse(status = "401"),
                    @OpenApiResponse(status = "404")
            })
    public void getOne(Context ctx) {
        String userId = resolveUserId(ctx);
        String id = ctx.pathParam("id");
        var m = getMedicineForUser.execute(new GetMedicineForUser.Params(userId, id));
        HttpHelper.ok(ctx, MedicineJsonResponse.from(m));
    }

    @OpenApi(
            path = "/api/medicines",
            methods = HttpMethod.POST,
            summary = "Criar medicamento",
            tags = { "Medicines" },
            operationId = "createMedicine",
            security = { @OpenApiSecurity(name = "bearerAuth") },
            requestBody = @OpenApiRequestBody(
                    required = true,
                    content = { @OpenApiContent(from = MedicineWriteRequest.class) }),
            responses = {
                    @OpenApiResponse(status = "201", content = {
                            @OpenApiContent(from = MedicineJsonResponse.class) }),
                    @OpenApiResponse(status = "400"),
                    @OpenApiResponse(status = "401")
            })
    public void create(Context ctx) {
        String userId = resolveUserId(ctx);
        MedicineWriteRequest body = readBody(ctx);
        validateWrite(body);
        var ex = deriveWriteExtras(body);
        var m = createMedicineForUser.execute(new CreateMedicineForUser.Params(
                userId,
                body.name().trim(),
                body.dosage().trim(),
                body.frequency().trim(),
                body.times() != null ? body.times() : List.of(),
                body.startDate(),
                body.endDate(),
                blankToNull(body.notes()),
                blankToNull(body.imageUrl()),
                ex.medicineType(),
                ex.prescribedFor(),
                ex.quantity(),
                ex.reminderOnEmpty()));
        HttpHelper.created(ctx, MedicineJsonResponse.from(m));
    }

    @OpenApi(
            path = "/api/medicines/{id}",
            methods = HttpMethod.PUT,
            summary = "Atualizar medicamento",
            tags = { "Medicines" },
            operationId = "updateMedicine",
            pathParams = { @OpenApiParam(name = "id", required = true, description = "ID do medicamento") },
            security = { @OpenApiSecurity(name = "bearerAuth") },
            requestBody = @OpenApiRequestBody(
                    required = true,
                    content = { @OpenApiContent(from = MedicineWriteRequest.class) }),
            responses = {
                    @OpenApiResponse(status = "200", content = {
                            @OpenApiContent(from = MedicineJsonResponse.class) }),
                    @OpenApiResponse(status = "400"),
                    @OpenApiResponse(status = "401"),
                    @OpenApiResponse(status = "404")
            })
    public void update(Context ctx) {
        String userId = resolveUserId(ctx);
        String id = ctx.pathParam("id");
        MedicineWriteRequest body = readBody(ctx);
        validateWrite(body);
        var ex = deriveWriteExtras(body);
        var m = updateMedicineForUser.execute(new UpdateMedicineForUser.Params(
                userId,
                id,
                body.name().trim(),
                body.dosage().trim(),
                body.frequency().trim(),
                body.times() != null ? body.times() : List.of(),
                body.startDate(),
                body.endDate(),
                blankToNull(body.notes()),
                blankToNull(body.imageUrl()),
                ex.medicineType(),
                ex.prescribedFor(),
                ex.quantity(),
                ex.reminderOnEmpty()));
        HttpHelper.ok(ctx, MedicineJsonResponse.from(m));
    }

    @OpenApi(
            path = "/api/medicines/{id}",
            methods = HttpMethod.DELETE,
            summary = "Excluir medicamento",
            tags = { "Medicines" },
            operationId = "deleteMedicine",
            pathParams = { @OpenApiParam(name = "id", required = true, description = "ID do medicamento") },
            security = { @OpenApiSecurity(name = "bearerAuth") },
            responses = {
                    @OpenApiResponse(status = "204"),
                    @OpenApiResponse(status = "401"),
                    @OpenApiResponse(status = "404")
            })
    public void delete(Context ctx) {
        String userId = resolveUserId(ctx);
        String id = ctx.pathParam("id");
        deleteMedicineForUser.execute(new DeleteMedicineForUser.Params(userId, id));
        HttpHelper.noContent(ctx);
    }

    private String resolveUserId(Context ctx) {
        String token = AccessTokenExtractor.requireAccessToken(ctx);
        try {
            return decrypter.decrypt(token);
        } catch (Exception e) {
            throw new UnauthorizedException("Token inválido", e);
        }
    }

    private MedicineWriteRequest readBody(Context ctx) {
        try {
            return objectMapper.readValue(ctx.body(), MedicineWriteRequest.class);
        } catch (JsonProcessingException e) {
            throw new ValidationException("Formato JSON inválido na requisição", e);
        }
    }

    private static void validateWrite(MedicineWriteRequest body) {
        if (body.name() == null || body.name().isBlank()) {
            throw new ValidationException("Nome é obrigatório");
        }
        if (body.dosage() == null || body.dosage().isBlank()) {
            throw new ValidationException("Dosagem é obrigatória");
        }
        if (body.frequency() == null || body.frequency().isBlank()) {
            throw new ValidationException("Frequência é obrigatória");
        }
        if (body.startDate() == null) {
            throw new ValidationException("Data de início é obrigatória");
        }
    }

    private static String blankToNull(String s) {
        if (s == null || s.isBlank()) {
            return null;
        }
        return s;
    }

    private record WriteExtras(
            String medicineType,
            String prescribedFor,
            int quantity,
            boolean reminderOnEmpty) {
    }

    private static WriteExtras deriveWriteExtras(MedicineWriteRequest body) {
        String type = body.medicineType() != null && !body.medicineType().isBlank()
                ? body.medicineType().trim()
                : "capsule";
        int qty = body.quantity() != null && body.quantity() > 0 ? body.quantity() : 1;
        boolean remind = body.reminderOnEmpty() == null || body.reminderOnEmpty();
        return new WriteExtras(type, blankToNull(body.prescribedFor()), qty, remind);
    }

    public record MedicineWriteRequest(
            String name,
            String dosage,
            String frequency,
            List<String> times,
            @JsonFormat(pattern = "yyyy-MM-dd")
            LocalDate startDate,
            @JsonFormat(pattern = "yyyy-MM-dd")
            LocalDate endDate,
            String notes,
            String imageUrl,
            String medicineType,
            String prescribedFor,
            Integer quantity,
            Boolean reminderOnEmpty) {
    }

    public record MedicineJsonResponse(
            String id,
            String userId,
            String name,
            String dosage,
            String frequency,
            List<String> times,
            @JsonFormat(pattern = "yyyy-MM-dd")
            LocalDate startDate,
            @JsonFormat(pattern = "yyyy-MM-dd")
            LocalDate endDate,
            String notes,
            String imageUrl,
            String medicineType,
            String prescribedFor,
            int quantity,
            boolean reminderOnEmpty,
            @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
            LocalDateTime createdAt,
            @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
            LocalDateTime updatedAt) {

        static MedicineJsonResponse from(Medicine m) {
            return new MedicineJsonResponse(
                    m.id(),
                    m.userId(),
                    m.name(),
                    m.dosage(),
                    m.frequency(),
                    m.times(),
                    m.startDate(),
                    m.endDate(),
                    m.notes(),
                    m.imageUrl(),
                    m.medicineType(),
                    m.prescribedFor(),
                    m.quantity(),
                    m.reminderOnEmpty(),
                    m.createdAt(),
                    m.updatedAt());
        }
    }
}
