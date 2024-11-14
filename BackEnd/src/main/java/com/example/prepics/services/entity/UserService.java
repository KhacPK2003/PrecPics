package com.example.prepics.services.entity;

import com.example.prepics.entity.User;
import com.example.prepics.interfaces.CRUDInterface;
import org.springframework.data.crossstore.ChangeSetPersister;

import java.util.Optional;

public interface UserService extends CRUDInterface<User, String> {
    Optional<User> findByEmail(Class<User> clazz, String email) throws ChangeSetPersister.NotFoundException;
}
