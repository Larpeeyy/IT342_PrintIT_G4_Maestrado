package com.printit.backend.core.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.SecurityFilterChain;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class SecurityConfig {

    private final OAuth2LoginSuccessHandler successHandler;
    private final OAuth2LoginFailureHandler failureHandler;
    private final ClientRegistrationRepository clientRegistrationRepository;

    @Value("${printit.oauth.public-base-url:}")
    private String oauthPublicBaseUrl;

    public SecurityConfig(
            OAuth2LoginSuccessHandler successHandler,
            OAuth2LoginFailureHandler failureHandler,
            ClientRegistrationRepository clientRegistrationRepository
    ) {
        this.successHandler = successHandler;
        this.failureHandler = failureHandler;
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(httpBasic -> httpBasic.disable())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        .requestMatchers(
                                "/",
                                "/error",
                                "/oauth2/**",
                                "/login/**",

                                "/api/auth/**",
                                "/api/student/**",
                                "/api/profile/**",
                                "/api/admin/**",
                                "/api/staff/**",

                                "/api/notifications/**"
                        ).permitAll()

                        .anyRequest().authenticated()
                )

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"message\":\"Unauthorized access\"}");
                        })

                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"message\":\"Forbidden\"}");
                        })
                )

                .oauth2Login(oauth -> oauth
                        .authorizationEndpoint(endpoint -> endpoint
                                .authorizationRequestResolver(
                                        googleAuthorizationRequestResolver(clientRegistrationRepository)
                                )
                        )
                        .successHandler(successHandler)
                        .failureHandler(failureHandler)
                );

        return http.build();
    }

    private OAuth2AuthorizationRequestResolver googleAuthorizationRequestResolver(
            ClientRegistrationRepository clientRegistrationRepository
    ) {
        DefaultOAuth2AuthorizationRequestResolver defaultResolver =
                new DefaultOAuth2AuthorizationRequestResolver(
                        clientRegistrationRepository,
                        "/oauth2/authorization"
                );

        return new OAuth2AuthorizationRequestResolver() {

            @Override
            public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
                OAuth2AuthorizationRequest authorizationRequest = defaultResolver.resolve(request);
                return customizeGoogleAuthorizationRequest(authorizationRequest);
            }

            @Override
            public OAuth2AuthorizationRequest resolve(
                    HttpServletRequest request,
                    String clientRegistrationId
            ) {
                OAuth2AuthorizationRequest authorizationRequest =
                        defaultResolver.resolve(request, clientRegistrationId);

                return customizeGoogleAuthorizationRequest(authorizationRequest);
            }
        };
    }

    private OAuth2AuthorizationRequest customizeGoogleAuthorizationRequest(
            OAuth2AuthorizationRequest authorizationRequest
    ) {
        if (authorizationRequest == null) {
            return null;
        }

        Map<String, Object> additionalParameters =
                new LinkedHashMap<>(authorizationRequest.getAdditionalParameters());

        additionalParameters.put("prompt", "select_account");

        OAuth2AuthorizationRequest.Builder builder = OAuth2AuthorizationRequest
                .from(authorizationRequest)
                .additionalParameters(additionalParameters);

        String publicBaseUrl = normalizePublicBaseUrl(oauthPublicBaseUrl);

        if (!publicBaseUrl.isBlank()) {
            builder.redirectUri(publicBaseUrl + "/login/oauth2/code/google");
        }

        return builder.build();
    }

    private String normalizePublicBaseUrl(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }

        return value.trim().replaceAll("/+$", "");
    }
}
