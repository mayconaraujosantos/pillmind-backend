package com.pillmind.data.usecases;

import java.util.UUID;

import com.pillmind.data.protocols.db.UserImageRepository;
import com.pillmind.data.protocols.storage.ImageStorageGateway;
import com.pillmind.domain.errors.ValidationException;
import com.pillmind.domain.models.ImageUploadStatus;
import com.pillmind.domain.models.UserImage;
import com.pillmind.domain.usecases.RequestImageUpload;

public class DbRequestImageUpload extends DbUseCase implements RequestImageUpload {
    private final ImageStorageGateway imageStorageGateway;
    private final UserImageRepository userImageRepository;

    public DbRequestImageUpload(ImageStorageGateway imageStorageGateway, UserImageRepository userImageRepository) {
        this.imageStorageGateway = imageStorageGateway;
        this.userImageRepository = userImageRepository;
    }

    @Override
    public Result execute(Params params) {
        validateParams(params);

        var uploadData = imageStorageGateway.createDirectUploadUrl(new ImageStorageGateway.CreateDirectUploadParams(
                params.userId(),
                params.kind(),
                params.fileName(),
                params.contentType()));

        var userImage = new UserImage(
                UUID.randomUUID().toString(),
                params.userId(),
                uploadData.imageId(),
                params.kind(),
                null,
                ImageUploadStatus.REQUESTED);

        userImageRepository.save(userImage);

        return new Result(uploadData.imageId(), uploadData.uploadUrl());
    }

    private void validateParams(Params params) {
        if (params.kind() == null) {
            throw new ValidationException("Tipo de imagem é obrigatório");
        }

        if (params.fileName() == null || params.fileName().isBlank()) {
            throw new ValidationException("Nome do arquivo é obrigatório");
        }

        if (params.contentType() == null || !params.contentType().startsWith("image/")) {
            throw new ValidationException("Content-Type inválido. Envie apenas imagens");
        }

        if (params.size() <= 0) {
            throw new ValidationException("Tamanho do arquivo inválido");
        }

        long maxSize = params.kind().maxSizeInBytes();
        if (params.size() > maxSize) {
            throw new ValidationException("Arquivo excede o limite de tamanho para o tipo " + params.kind().name());
        }
    }
}
