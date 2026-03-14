package com.pillmind.data.protocols.db;

import java.util.Optional;

import com.pillmind.domain.models.UserImage;

public interface UserImageRepository {
    UserImage save(UserImage userImage);

    Optional<UserImage> findByImageId(String imageId);
}
