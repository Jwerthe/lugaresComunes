package com.example.demo.config;

import com.example.demo.security.JwtAuthenticationEntryPoint;
import com.example.demo.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    // AGREGAR después de la declaración de clase:
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                        JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                // 🌐 ENDPOINTS PÚBLICOS DE AUTENTICACIÓN (sin JWT)
                .requestMatchers("/auth/**").permitAll()
                
                // 🌐 ENDPOINTS PÚBLICOS DE LUGARES (sin JWT)
                .requestMatchers(HttpMethod.GET, "/places/**").permitAll()
                
                // 🌐 NUEVOS ENDPOINTS PÚBLICOS DE RUTAS (sin JWT)
                .requestMatchers(HttpMethod.GET, "/routes/destinations").permitAll()
                .requestMatchers(HttpMethod.GET, "/routes/to/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/routes/*/points").permitAll()
                .requestMatchers(HttpMethod.GET, "/routes/nearest").permitAll()
                .requestMatchers(HttpMethod.GET, "/routes/*/details").permitAll()
                .requestMatchers(HttpMethod.GET, "/routes/health").permitAll()
                .requestMatchers(HttpMethod.GET, "/routes/proposals/health").permitAll()
                .requestMatchers(HttpMethod.GET, "/navigation/health").permitAll()
                .requestMatchers(HttpMethod.GET, "/users/health").permitAll()
                
                // 🔐 ENDPOINTS PROTEGIDOS DE RUTAS (requieren JWT - cualquier usuario)
                .requestMatchers(HttpMethod.POST, "/routes/*/rating").authenticated()
                .requestMatchers(HttpMethod.GET, "/routes/*/my-rating").authenticated()
                
                // 🔐 ENDPOINTS PROTEGIDOS DE PROPUESTAS (requieren JWT - cualquier usuario)
                .requestMatchers(HttpMethod.POST, "/routes/proposals").authenticated()
                .requestMatchers(HttpMethod.GET, "/routes/proposals/my").authenticated()
                
                // 🔐 ENDPOINTS PROTEGIDOS DE NAVEGACIÓN (requieren JWT - cualquier usuario)
                .requestMatchers(HttpMethod.POST, "/navigation/start").authenticated()
                .requestMatchers(HttpMethod.POST, "/navigation/complete").authenticated()
                .requestMatchers(HttpMethod.GET, "/navigation/history").authenticated()
                
                // 🔐 ENDPOINTS PROTEGIDOS EXISTENTES (requieren JWT)
                .requestMatchers("/auth/me").authenticated()
                .requestMatchers("/favorites/**").authenticated()
                
                // 🛡️ ENDPOINTS SOLO ADMIN - RUTAS
                .requestMatchers(HttpMethod.POST, "/routes").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/routes/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/routes/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/routes/analytics").hasRole("ADMIN")
                
                // 🛡️ ENDPOINTS SOLO ADMIN - PROPUESTAS
                .requestMatchers(HttpMethod.GET, "/routes/proposals/pending").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/routes/proposals/*/approve").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/routes/proposals/*/reject").hasRole("ADMIN")
                
                // 🛡️ ENDPOINTS SOLO ADMIN - GESTIÓN DE USUARIOS
                .requestMatchers(HttpMethod.PUT, "/users/*/promote").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/users/contributors").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/users/promotions/recent").hasRole("ADMIN")
                
                // 🛡️ ENDPOINTS SOLO ADMIN EXISTENTES
                .requestMatchers(HttpMethod.POST, "/places").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/places/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/places/**").hasRole("ADMIN")
                
                // 🔒 CUALQUIER OTRO ENDPOINT REQUIERE AUTENTICACIÓN
                .anyRequest().authenticated()
            );

        // Agregar el filtro JWT antes del filtro de autenticación por usuario/contraseña
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}