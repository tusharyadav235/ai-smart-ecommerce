package com.example.ai_smart_ecommerce.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    // ─────────────────────────────────────────────────────────────
    //  CORS — allow ALL localhost ports (3000, 3001, 5500, 5173…)
    // ─────────────────────────────────────────────────────────────
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // ✅ Allow every localhost port your frontend might run on
        config.setAllowedOriginPatterns(List.of(
                "http://localhost:*",
                "http://127.0.0.1:*",
                "null"           // needed when opening the HTML file directly
        ));

        // ✅ Allow all standard HTTP methods
        config.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));

        // ✅ Allow all headers (including Authorization for JWT)
        config.setAllowedHeaders(List.of("*"));

        // ✅ Expose Authorization header so frontend can read the JWT token
        config.setExposedHeaders(List.of("Authorization"));

        // ✅ Allow cookies / credentials
        config.setAllowCredentials(true);

        // ✅ Cache preflight response for 1 hour (reduces OPTIONS requests)
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    // ─────────────────────────────────────────────────────────────
    //  SECURITY FILTER CHAIN
    // ─────────────────────────────────────────────────────────────
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // ✅ Disable CSRF (not needed for stateless JWT APIs)
                .csrf(csrf -> csrf.disable())

                // ✅ Wire the CORS bean defined above
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // ✅ Stateless session — JWT handles auth, no server sessions
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // ✅ Endpoint access rules
                .authorizeHttpRequests(auth -> auth

                        // ── Completely public (no token needed) ──────────────
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/products/**").permitAll()
                        .requestMatchers("/api/categories/**").permitAll()
                        .requestMatchers("/api/search/**").permitAll()

                        // ── Admin only ───────────────────────────────────────
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // ── Logged-in users only ─────────────────────────────
                        .requestMatchers("/api/user/**").hasRole("USER")
                        .requestMatchers("/api/cart/**").authenticated()
                        .requestMatchers("/api/orders/**").authenticated()
                        .requestMatchers("/api/wishlist/**").authenticated()
                        .requestMatchers("/api/ai/**").authenticated()

                        // ── Everything else needs a valid JWT ────────────────
                        .anyRequest().authenticated()
                )

                // ✅ Run JWT filter before Spring's default username/password filter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}