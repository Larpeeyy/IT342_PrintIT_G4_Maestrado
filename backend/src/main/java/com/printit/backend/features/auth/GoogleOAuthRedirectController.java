package com.printit.backend.features.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GoogleOAuthRedirectController {

    private static final String OAUTH_SOURCE_SESSION_KEY = "PRINTIT_OAUTH_SOURCE";

    @GetMapping("/api/auth/google/web")
    public String startGoogleLoginForWeb(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        session.setAttribute(OAUTH_SOURCE_SESSION_KEY, "web");

        return "redirect:/oauth2/authorization/google";
    }

    @GetMapping("/api/auth/google/mobile")
    public String startGoogleLoginForMobile(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        session.setAttribute(OAUTH_SOURCE_SESSION_KEY, "mobile");

        return "redirect:/oauth2/authorization/google";
    }
}