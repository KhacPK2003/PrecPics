package com.example.prepics.apis;

import com.example.prepics.entity.User;
import com.example.prepics.services.entity.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/public/api/v1/users")
public class UserApi {
    @Autowired
    private UserService userService;

    @GetMapping("/user")
    public ResponseEntity<?> getUser(@RequestBody Map<String, String> value) throws ChangeSetPersister.NotFoundException {
        String userId  = value.get("userId");
        Optional<User> result = Optional.ofNullable(userService.findById(User.class, userId));
        return ResponseEntity.ok(result);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUserWithGoogle(Authentication authentication)
            throws ChangeSetPersister.NotFoundException {

        User userDecode = (User) authentication.getPrincipal();
        User resultUser = userService.findByEmail(User.class, userDecode.getEmail());


        if (resultUser == null) {
            User UserCreated = userService.create(userDecode);

            log.info("loginUserWithAuthId - 200 - Success");
            Map<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "Success");
            response.put("payload", UserCreated);

            return ResponseEntity.status(200).body(response);
        }

        User UserUpdated = userService.update(resultUser);

        log.info("loginUserWithAuthId - 200 - Success");
        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "Success");
        response.put("payload", UserUpdated);

        return ResponseEntity.status(200).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUserWithEmail(Authentication authentication, @RequestBody Map<String, String> value)
            throws ChangeSetPersister.NotFoundException {
        User userDecode = (User) authentication.getPrincipal();
        User resultUser = userService.findByEmail(User.class, userDecode.getEmail());


        if (resultUser == null) {
            User UserCreated = userService.create(userDecode);

            log.info("loginUserWithAuthId - 200 - Success");
            Map<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "Success");
            response.put("payload", UserCreated);

            return ResponseEntity.status(200).body(response);
        }

        User UserUpdated = userService.update(resultUser);

        log.info("loginUserWithAuthId - 200 - Success");
        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "Success");
        response.put("payload", UserUpdated);

        return ResponseEntity.status(200).body(response);
    }


}
