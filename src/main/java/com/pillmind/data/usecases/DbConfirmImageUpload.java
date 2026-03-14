package com.pillmind.data.usecases;

import java.util.UUID;

import com.pillmind.data.protocols.db.UserImageRepository;
import com.pillmind.data.protocols.storage.ImageStorageGateway;
import com.pillmind.domain.errors.UnauthorizedException;
import com.pillmind.domain.errors.ValidationException;
import com.pillmind.domain.models.ImageUploadStatus;
import com.pillmind.domain.models.UserImage;
import com.pillmind.domain.usecases.ConfirmImageUpload;

public class DbConfirmImageUpload extends DbUseCase implements ConfirmImageUpload {
    private final ImageStorageGateway imageStorageGateway;
    private final UserImageRepository userImageRepository;

    public DbConfirmImageUpload(ImageStorageGateway imageStorageGateway, UserImageRepository userImageRepository) {
        this.imageStorageGateway = imageStorageGateway;
        this.userImageRepository = userImageRepository;
    }

    @Override
    public Result execute(Params params) {
        if (params.imageId() == null || params.imageId().isBlank()) {
            throw new ValidationException("imageId é obrigatório");
        }

        var imageDetails = imageStorageGateway.getImageDetails(params.imageId());
        if (!imageDetails.uploaded()) {
            throw new ValidationException("Imagem ainda não foi enviada para o storage");
        }

        if (imageDetails.metadataUserId() == null || !params.userId().equals(imageDetails.metadataUserId())) {
            throw new UnauthorizedException("Você não tem permissão para confirmar esta imagem");
        }

        if (imageDetails.metadataKind() == null
                || !params.kind().name().equalsIgnoreCase(imageDetails.metadataKind())) {
            throw new ValidationException("Tipo da imagem não corresponde ao upload solicitado");
        }

        String deliveryUrl = imageStorageGateway.buildDeliveryUrl(params.imageId(), params.kind().defaultVariant());

        var existing = userImageRepository.findByImageId(params.imageId());
        if (existing.isPresent()) {
            userImageRepository.save(existing.get().withConfirmedDeliveryUrl(deliveryUrl));
        } else {
            userImageRepository.save(new UserImage(
                    UUID.randomUUID().toString(),
                    params.userId(),
                    params.imageId(),
                    params.kind(),
                    deliveryUrl,
                    ImageUploadStatus.CONFIRMED));
        }

        return new Result(params.imageId(), deliveryUrl);
    }
}
