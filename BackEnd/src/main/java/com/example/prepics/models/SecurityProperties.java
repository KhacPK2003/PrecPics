package com.example.prepics.models;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("security")
@Data
public class SecurityProperties {

  CookieProperties cookieProps;
  FirebaseProperties firebaseProps;
  boolean allowCredentials;
  List<String> allowedOrigins;
  List<String> allowedHeaders;
  List<String> exposedHeaders;
  List<String> allowedMethods;
  List<String> allowedPublicApis;

}