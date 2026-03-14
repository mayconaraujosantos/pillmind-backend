package com.pillmind.infra.db.postgres;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pillmind.data.protocols.db.UserImageRepository;
import com.pillmind.domain.models.ImageKind;
import com.pillmind.domain.models.ImageUploadStatus;
import com.pillmind.domain.models.UserImage;

public class UserImagePostgresRepository extends PostgresRepository implements UserImageRepository {
    private static final Logger logger = LoggerFactory.getLogger(UserImagePostgresRepository.class);

    public UserImagePostgresRepository(java.sql.Connection connection) {
        super(connection);
    }

    @Override
    public UserImage save(UserImage userImage) {
        String sql = """
                INSERT INTO user_images (id, user_id, image_id, kind, status, delivery_url, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT (image_id) DO UPDATE SET
                    user_id = EXCLUDED.user_id,
                    kind = EXCLUDED.kind,
                    status = EXCLUDED.status,
                    delivery_url = EXCLUDED.delivery_url,
                    updated_at = EXCLUDED.updated_at
                """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userImage.id());
            stmt.setString(2, userImage.userId());
            stmt.setString(3, userImage.imageId());
            stmt.setString(4, userImage.kind().name());
            stmt.setString(5, userImage.status().name());
            stmt.setString(6, userImage.deliveryUrl());
            stmt.setTimestamp(7, Timestamp.valueOf(userImage.createdAt()));
            stmt.setTimestamp(8, Timestamp.valueOf(userImage.updatedAt()));
            stmt.executeUpdate();
            return userImage;
        } catch (SQLException e) {
            logger.error("Error saving user image {}: {}", userImage.imageId(), e.getMessage(), e);
            throw new RuntimeException("Erro interno do servidor ao salvar imagem do usuário", e);
        }
    }

    @Override
    public Optional<UserImage> findByImageId(String imageId) {
        String sql = "SELECT id, user_id, image_id, kind, status, delivery_url, created_at, updated_at FROM user_images WHERE image_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, imageId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                return Optional.of(mapToUserImage(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding user image {}: {}", imageId, e.getMessage(), e);
            throw new RuntimeException("Erro interno do servidor ao buscar imagem do usuário", e);
        }
    }

    private UserImage mapToUserImage(ResultSet rs) throws SQLException {
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
