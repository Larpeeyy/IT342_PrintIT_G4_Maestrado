package com.printit.backend.core.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class OAuth2LoginFailureHandler implements AuthenticationFailureHandler {

    private static final String WEB_FAILURE_REDIRECT_URL = "http://localhost:3000/login";
    private static final String MOBILE_FAILURE_REDIRECT_URL = "printit://oauth-error";
    private static final String OAUTH_SOURCE_SESSION_KEY = "PRINTIT_OAUTH_SOURCE";

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException, ServletException {

        exception.printStackTrace();

        String errorMessage = exception.getMessage() != null
                ? exception.getMessage()
                : "OAuth login failed";

        String source = getOAuthSource(request);
        String baseRedirectUrl = "mobile".equalsIgnoreCase(source)
                ? MOBILE_FAILURE_REDIRECT_URL
                : WEB_FAILURE_REDIRECT_URL;

        clearOAuthSource(request);

        String separator = baseRedirectUrl.contains("?") ? "&" : "?";
        String redirectUrl = baseRedirectUrl
                + separator
                + "error="
                + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);

        response.sendRedirect(redirectUrl);
    }

    private String getOAuthSource(HttpServletRequest request) {
        var session = request.getSession(false);

        if (session == null) {
            return "web";
        }

        Object source = session.getAttribute(OAUTH_SOURCE_SESSION_KEY);

        if (source == null) {
            return "web";
        }

        return source.toString();
    }

    private void clearOAuthSource(HttpServletRequest request) {
        var session = request.getSession(false);

        if (session != null) {
            session.removeAttribute(OAUTH_SOURCE_SESSION_KEY);
        }
    }
}
