package com.example.prepics.services.secrurities;

import com.example.prepics.entity.User;
import com.example.prepics.models.CredentialFirebase;
import com.example.prepics.models.SecurityProperties;
import com.example.prepics.utils.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


@Service
public class SecurityService {

  @Autowired
  HttpServletRequest httpServletRequest;

  @Autowired
  CookieUtil cookieUtils;

  @Autowired
  SecurityProperties securityProps;

  public User getUser() {
    User userPrincipal = null;
    SecurityContext securityContext = SecurityContextHolder.getContext();
    Object principal = securityContext.getAuthentication().getPrincipal();
    if (principal instanceof User) {
      userPrincipal = ((User) principal);
    }
    return userPrincipal;
  }

  public CredentialFirebase getCredentials() {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    return (CredentialFirebase) securityContext.getAuthentication().getCredentials();
  }

  public boolean isPublic() {
    return securityProps.getAllowedPublicApis().contains(httpServletRequest.getRequestURI());
  }

  public String getBearerToken(HttpServletRequest request) {
    String bearerToken = null;
    String authorization = request.getHeader("Authorization");
    if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
      bearerToken = authorization.substring(7);
    }
    return bearerToken;
  }
}