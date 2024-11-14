package com.example.prepics.services.api;

import com.example.prepics.entity.User;
import com.example.prepics.services.entity.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserApiService {

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    public Map<String, Object> loginUserWithGoogle(Authentication authentication)
            throws ChangeSetPersister.NotFoundException {

        User userDecode = (User) authentication.getPrincipal();
        Optional<User> resultUser = userService.findByEmail(User.class, userDecode.getEmail());


        if (resultUser.isEmpty()) {
            Optional<User> UserCreated = userService.create(userDecode);

            Map<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "Success");
            response.put("payload", UserCreated);

            return response;
        }

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "Success");
        response.put("payload", resultUser.get());

        return response;
    }

    public Map<String, Object> findAll(Authentication authentication)
            throws ChangeSetPersister.NotFoundException {

        User userDecode = (User) authentication.getPrincipal();
        Optional<User> resultUser = userService.findByEmail(User.class, userDecode.getEmail());
        if (resultUser.isEmpty() || !resultUser.get().getIsAdmin()) {
            throw new ChangeSetPersister.NotFoundException();
        }

        Optional<List<User>> result = userService.findAll(User.class);

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "Success");
        response.put("payload", result);

        return response;
    }

    public Map<String, Object> findById(String id) throws ChangeSetPersister.NotFoundException {
        Optional<User> result = userService.findById(User.class, id);

        if (result.isEmpty()) {
            throw new ChangeSetPersister.NotFoundException();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "Success");
        response.put("payload", result);

        return response;
    }

    public Map<String, Object> update(Authentication authentication , User entity)
            throws ChangeSetPersister.NotFoundException {

        User userDecode = (User) authentication.getPrincipal();
        Optional<User> result = userService.findByEmail(User.class, userDecode.getEmail());
        if (result.isEmpty() || !result.get().getId().equals(entity.getId())) {
            throw new ChangeSetPersister.NotFoundException();
        }
        modelMapper.map(entity, result.get());
        userService.update(result.get());

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "Success");
        response.put("payload", result);

        return response;
    }




}
