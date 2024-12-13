package com.example.prepics.services.secrurities;

import com.example.prepics.entity.User;
import com.example.prepics.services.entity.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class AuthenticationAspect {

  @Autowired
  private UserService userService;

  @Pointcut("@annotation(com.example.prepics.annotations.User)")
  public void userCheck() {
  }

  @Pointcut("@annotation(com.example.prepics.annotations.Admin)")
  public void adminCheck() {
  }

  @Around("userCheck()")
  public Object validateUser(ProceedingJoinPoint joinPoint) throws Throwable {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !(authentication.getPrincipal() instanceof User userDecode)) {
      throw new RuntimeException("Unauthorized");
    }

    User user = userService.findByEmail(User.class, userDecode.getEmail())
        .orElseThrow(ChangeSetPersister.NotFoundException::new);

    return joinPoint.proceed();
  }

  @Around("adminCheck()")
  public Object validateAdmin(ProceedingJoinPoint joinPoint) throws Throwable {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !(authentication.getPrincipal() instanceof User userDecode)) {
      throw new RuntimeException("Unauthorized");
    }

    User user = userService.findByEmail(User.class, userDecode.getEmail())
        .orElseThrow(ChangeSetPersister.NotFoundException::new);

    if (!user.getIsAdmin()) {
      throw new RuntimeException("Unauthorized access");
    }

    return joinPoint.proceed();
  }
}