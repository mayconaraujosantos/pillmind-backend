package com.pillmind.infra.db.mariadb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pillmind.data.protocols.db.UserImageRepository;
import com.pillmind.domain.errors.DatabaseException;
import com.pillmind.domain.models.ImageKind;
import com.pillmind.domain.models.ImageUploadStatus;
import com.pillmind.domain.models.UserImage;

@SuppressWarnings("java:S2139")
public class UserImageMariaDbRepository extends MariaDbRepository implements UserImageRepository {
    private static final Logger logger = LoggerFactory.getLogger(UserImageMariaDbRepository.class);

    public UserImageMariaDbRepository(Connection connection) {
        super(connection);
    }

    @Override
    public UserImage save(UserImage userImage) {
        // MariaDB usa ON DUPLICATE KEY UPDATE com VALUES() em vez de EXCLUDED
        String sql = """
                INSERT INTO user_images (id, user_id, image_id, kind, status, delivery_url, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                    user_id = VALUES(user_id),
                    kind = VALUES(kind),
                    status = VALUES(status),
                    delivery_url = VALUES(delivery_url),
                    updated_at = VALUES(updated_at)
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
            logger.debug("✓ UserImage saved with imageId: {}", userImage.imageId());
            return userImage;
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao salvar imagem do usuário: " + userImage.imageId(), e);
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
            throw new DatabaseException("Erro ao buscar imagem do usuário por imageId: " + imageId, e);
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
