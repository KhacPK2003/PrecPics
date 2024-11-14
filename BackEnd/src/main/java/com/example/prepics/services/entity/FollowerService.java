package com.example.prepics.services.entity;

import com.example.prepics.entity.Followers;
import com.example.prepics.interfaces.CRUDInterface;
import org.springframework.data.crossstore.ChangeSetPersister;

import java.util.Optional;

public interface FollowerService extends CRUDInterface<Followers, Long> {
    Optional<Followers> findByUserIdAndFollowerId(Class<Followers> clazz, String userId, String followerId)
            throws ChangeSetPersister.NotFoundException;
}
