package com.example.prepics.config;

import java.io.IOException;
import java.io.InputStream;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@Configuration
public class FirebaseConfig {

    @Autowired
    SecurityProperties secProps;

    @Bean
    public FirebaseMessaging firebaseMessaging() throws FirebaseAuthException, IOException {
        InputStream inputStream = new ClassPathResource("firebase_authentication_config.json").getInputStream();

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(inputStream)).build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }

        System.out.println("Firebase initialized and FirebaseMessaging configured");
        return FirebaseMessaging.getInstance();
    }
}
