package com.pillmind.infra.db.postgres;

import java.sql.Timestamp;
import java.util.Optional;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.JdbiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pillmind.data.protocols.db.UserImageRepository;
import com.pillmind.domain.errors.DatabaseException;
import com.pillmind.domain.models.ImageKind;
import com.pillmind.domain.models.ImageUploadStatus;
import com.pillmind.domain.models.UserImage;

public class UserImagePostgresRepository extends PostgresRepository implements UserImageRepository {
    private static final Logger logger = LoggerFactory.getLogger(UserImagePostgresRepository.class);

    public UserImagePostgresRepository(Jdbi jdbi) {
        super(jdbi);
    }

    @Override
    public UserImage save(UserImage userImage) {
        try {
            jdbi.useHandle(h -> h.createUpdate(
                    "INSERT INTO user_images (id, user_id, image_id, kind, status, delivery_url, created_at, updated_at) " +
                    "VALUES (:id, :userId, :imageId, :kind, :status, :deliveryUrl, :createdAt, :updatedAt) " +
                    "ON CONFLICT (image_id) DO UPDATE SET " +
                    "user_id = EXCLUDED.user_id, kind = EXCLUDED.kind, status = EXCLUDED.status, " +
                    "delivery_url = EXCLUDED.delivery_url, updated_at = EXCLUDED.updated_at")
                .bind("id", userImage.id())
                .bind("userId", userImage.userId())
                .bind("imageId", userImage.imageId())
                .bind("kind", userImage.kind().name())
                .bind("status", userImage.status().name())
                .bind("deliveryUrl", userImage.deliveryUrl())
                .bind("createdAt", Timestamp.valueOf(userImage.createdAt()))
                .bind("updatedAt", Timestamp.valueOf(userImage.updatedAt()))
                .execute());
            logger.debug("✓ UserImage saved with imageId: {}", userImage.imageId());
            return userImage;
        } catch (JdbiException e) {
            throw new DatabaseException("Erro ao salvar imagem do usuário: " + userImage.imageId(), e);
        }
    }

    @Override
    public Optional<UserImage> findByImageId(String imageId) {
        try {
            return jdbi.withHandle(h -> h.createQuery(
                    "SELECT id, user_id, image_id, kind, status, delivery_url, created_at, updated_at " +
                    "FROM user_images WHERE image_id = :imageId")
                .bind("imageId", imageId)
                .map((rs, ctx) -> mapRow(rs))
                .findFirst());
        } catch (JdbiException e) {
            throw new DatabaseException("Erro ao buscar imagem do usuário por imageId: " + imageId, e);
        }
    }

    private UserImage mapRow(java.sql.ResultSet rs) throws java.sql.SQLException {
        return new UserImage(
                rs.getString("id"),
                rs.getString("user_id"),
                rs.getString("image_id"),
                ImageKind.fromString(rs.getString("kind")),
                rs.getString("delivery_url"),
                ImageUploadStatus.valueOf(rs.getString("status")),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getTimestamp("updated_at").toLocalDateTime());
    }
}
