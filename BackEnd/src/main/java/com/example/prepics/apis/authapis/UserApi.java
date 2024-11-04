package com.example.prepics.apis.authapis;

import com.example.prepics.services.api.UserApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserApi {
    @Autowired
    private UserApiService userApiService;

    @GetMapping
    public ResponseEntity<?> getAllUser(Authentication authentication)
            throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(userApiService.findAll(authentication));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUserWithGoogle(Authentication authentication)
            throws ChangeSetPersister.NotFoundException {
        Map<String, Object> response = userApiService.loginUserWithGoogle(authentication);
        return ResponseEntity.ok(response);
    }
}
