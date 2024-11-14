	package com.example.prepics.filters;

    import com.example.prepics.entity.User;
    import com.example.prepics.models.CredentialFirebase;
    import com.example.prepics.models.SecurityProperties;
    import com.example.prepics.services.entity.ContentService;
    import com.example.prepics.services.secrurities.SecurityService;
    import com.example.prepics.utils.CookieUtil;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.auth.FirebaseAuthException;
    import com.google.firebase.auth.FirebaseToken;
    import jakarta.servlet.FilterChain;
    import jakarta.servlet.ServletException;
    import jakarta.servlet.http.Cookie;
    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.servlet.http.HttpServletResponse;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.core.annotation.Order;
    import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
    import org.springframework.security.core.context.SecurityContextHolder;
    import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
    import org.springframework.stereotype.Component;
    import org.springframework.web.filter.OncePerRequestFilter;

    import java.io.IOException;

    @Component
    @Slf4j
    @Order(1)
    public class SecurityFilter extends OncePerRequestFilter {

        @Autowired
        SecurityService securityService;

        @Autowired
        SecurityProperties restSecProps;

        @Autowired
        CookieUtil cookieUtils;

        @Autowired
        SecurityProperties securityProps;

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {
            verifyToken(request);
            filterChain.doFilter(request, response);
        }

        private void verifyToken(HttpServletRequest request) {
            String session = null;
            FirebaseToken decodedToken = null;
            CredentialFirebase.CredentialType type = null;
            boolean strictServerSessionEnabled = securityProps.getFirebaseProps().isEnableStrictServerSession();
            Cookie sessionCookie = cookieUtils.getCookie("session");
            String token = securityService.getBearerToken(request);
            try {
                if (sessionCookie != null) {
                    session = sessionCookie.getValue();
                    decodedToken = FirebaseAuth.getInstance().verifySessionCookie(session,
                            securityProps.getFirebaseProps().isEnableCheckSessionRevoked());
                    type = CredentialFirebase.CredentialType.SESSION;
                } else if (!strictServerSessionEnabled) {
                    if (token != null && !token.equalsIgnoreCase("undefined")) {
                        decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
                        type = CredentialFirebase.CredentialType.ID_TOKEN;
                    }
                }
            } catch (FirebaseAuthException e) {
                e.printStackTrace();
                log.error("Firebase Exception:: ", e.getLocalizedMessage());
            }
            User user = firebaseTokenToUserDto(decodedToken);
            if (user != null) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user,
                        new CredentialFirebase(type, decodedToken, token, session), null);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        private User firebaseTokenToUserDto(FirebaseToken decodedToken) {
            User user = null;
            if (decodedToken != null) {
                user = User.builder()
                        .id(decodedToken.getUid())
                        .userName(extractUsername(decodedToken.getEmail()))
                        .fullName(decodedToken.getName())
                        .email(decodedToken.getEmail())
                        .avatarUrl(decodedToken.getPicture())
                        .build();
            }
            return user;
        }

        private String extractUsername(String email) {
            try{
                return email.split("@")[0];
            } catch (Exception e) {
                throw new IllegalArgumentException("Email do not have the extension @example.com");
            }
        }

    }