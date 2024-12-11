package com.example.prepics.services.entity;

import com.example.prepics.entity.Followers;
import com.example.prepics.interfaces.CRUDInterface;
import java.util.Optional;
import org.springframework.data.crossstore.ChangeSetPersister;

public interface FollowerService extends CRUDInterface<Followers, Long> {

  Optional<Followers> findByUserIdAndFollowerId(Class<Followers> clazz, String userId,
      String followerId)
      throws ChangeSetPersister.NotFoundException;
}