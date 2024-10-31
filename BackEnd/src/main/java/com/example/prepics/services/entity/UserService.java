package com.example.prepics.services.entity;

import com.example.prepics.entity.User;
import com.example.prepics.interfaces.CRUDInterface;
import org.springframework.stereotype.Service;

public interface UserService extends CRUDInterface<User, String> {
    User findByEmail(Class<User> clazz, String email);
}
