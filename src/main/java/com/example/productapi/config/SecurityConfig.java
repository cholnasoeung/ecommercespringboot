package com.example.productapi.config;

import com.example.productapi.security.JwtAuthFilter;
import com.example.productapi.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsServiceImpl userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults()) // <-- ADDED CORS SUPPORT HERE
                .authorizeHttpRequests(auth -> auth
                        // 1. Swagger UI
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        // 2. Auth (Login / Register)
                        .requestMatchers("/api/auth/**").permitAll()

                        // 3. Products (Anyone can view, logged-in users can create/update/delete)
                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/products/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/products/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**").authenticated()

                        // 4. Categories (Anyone can view, ONLY Admin can create)
                        .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/categories/**").hasRole("ADMIN")

                        // 5. Admin Dashboard
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // 6. Everything else (like /api/cart and /api/orders) requires login
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}