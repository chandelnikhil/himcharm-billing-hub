package org.himcharm.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.himcharm.jwt.JwtAuthenticationFilter;
import org.himcharm.jwt.UserDetailService;

import java.util.List;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfig {

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserDetailService userDetailService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final String frontendUrl;

    public SecurityConfig(
            BCryptPasswordEncoder passwordEncoder,
            UserDetailService userDetailService,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            @Value("${security.domain.frontend-url}") String frontendUrl
    ) {
        this.passwordEncoder = passwordEncoder;
        this.userDetailService = userDetailService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.frontendUrl = frontendUrl;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Enable CORS so the browser accepts responses from a different origin (frontend on :5173/:5174).
                // Uses the CorsConfigurationSource bean defined below.
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // CSRF protection is disabled because this is a stateless JWT API (no session cookies to protect).
                .csrf(csrf -> csrf.disable())
                // STATELESS: no HTTP session is created; every request must authenticate via its JWT token.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))// Disable only if appropriate for your API architecture
                .authorizeHttpRequests(auth -> auth
                        // Let CORS preflight requests through WITHOUT authentication.
                        // Browsers send an automatic OPTIONS request before the real call to ask
                        // "am I allowed?" — this preflight carries NO JWT token. If we required auth
                        // here, Spring would reject it with 401, the browser would see the preflight
                        // fail, and it would block the real request (surfacing as a CORS error).
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Login endpoint is public — you can't have a token before you log in.
                        .requestMatchers("/auth/**").permitAll()
                        // Meta must be able to verify and deliver WhatsApp webhooks without a JWT.
                        .requestMatchers("/webhooks/whatsapp/**").permitAll()
                        // Health checks must be accessible without a JWT for uptime monitors.
                        .requestMatchers(HttpMethod.GET, "/health").permitAll()
                        // Everything else requires a valid JWT.
                        .anyRequest().authenticated()
                )
                // Run our JWT filter before Spring's username/password filter so the token is
                // validated and the user is authenticated on every (non-OPTIONS) request.
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Explicit list of frontend origins allowed to call this API.
        // We CANNOT use "*" here because allowCredentials(true) below forbids the wildcard —
        // the browser requires an exact origin match when credentials are involved.
        System.out.println("setting allowed origins: " + frontendUrl);
        configuration.setAllowedOrigins(List.of(
                frontendUrl, "http://localhost:5173", "http://localhost:5174"
        ));
        // HTTP methods the browser is permitted to use. OPTIONS is included so the
        // CORS preflight request itself is allowed (populates Access-Control-Allow-Methods).
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        // Allow any request header (e.g. Authorization, Content-Type, ngrok-skip-browser-warning) on cross-origin calls.
        configuration.setAllowedHeaders(List.of("*"));
        // Allow the browser to send/accept credentials (Authorization header, cookies) cross-origin.
        // This is what makes the wildcard origin above illegal, hence the explicit list.
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Apply this CORS policy to all endpoints
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailService);
        authProvider.setPasswordEncoder(passwordEncoder);

        return authProvider;
    }

}
