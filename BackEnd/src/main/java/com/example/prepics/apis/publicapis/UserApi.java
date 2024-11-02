package com.example.prepics.apis.publicapis;

import com.example.prepics.entity.User;
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
@RequestMapping("/public/api/users")
public class UserApi {
    @Autowired
    private UserApiService userApiService;


}
