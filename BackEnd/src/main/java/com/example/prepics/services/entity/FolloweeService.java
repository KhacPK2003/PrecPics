package com.example.prepics.services.entity;

import com.example.prepics.entity.Followees;
import com.example.prepics.interfaces.CRUDInterface;
import org.springframework.data.crossstore.ChangeSetPersister;

import java.util.Optional;

public interface FolloweeService extends CRUDInterface<Followees, Long> {
    Optional<Followees> findByUserIdAndFolloweeId(Class<Followees> clazz, String userId, String followeeId)
            throws ChangeSetPersister.NotFoundException;
}
