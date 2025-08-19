package com.example.demo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtils jwtUtils, UserDetailsService userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    // 🔓 Rutas públicas EXACTAS (SIN el prefijo de contexto /api)
    private static final Set<String> PUBLIC_EXACT_PATHS = Set.of(
        // Auth públicos
        "/auth/login",
        "/auth/register",
        "/auth/health",
        "/auth/validate-email",
        "/auth/validate-student-id",

        // Places - todas las lecturas son públicas
        "/places",
        "/places/search",
        "/places/nearby",
        "/places/type",
        "/places/available",
        "/places/building",
        "/places/what3words",

        // Routes - SOLO estas rutas específicas son públicas
        "/routes/destinations",
        "/routes/nearest",
        "/routes/health",
        "/routes/proposals/health",

        // Navigation & Users health
        "/navigation/health",
        "/users/health",

        // General health
        "/health"
    );

    // 🔓 Patrones de rutas públicas específicas
    private static final List<String> PUBLIC_PATH_PATTERNS = List.of(
        // Rutas de lugares específicos (GET) - formato UUID
        "^/places/[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}$",
        
        // Rutas públicas específicas de routes
        "^/routes/to/[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}$",
        "^/routes/[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}/points$",
        "^/routes/[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}/details$"
    );

    // 🔐 Patrones de rutas EXPLÍCITAMENTE PROTEGIDAS (requieren JWT)
    private static final List<String> PROTECTED_PATH_PATTERNS = List.of(
        // Rutas que requieren autenticación
        "^/routes/[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}/rating$",
        "^/routes/[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}/my-rating$"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // Permitir preflight CORS
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            logger.debug("⏭️ Skipping JWT filter for CORS preflight OPTIONS");
            return true;
        }

        // Obtener la ruta correctamente
        String path = request.getRequestURI();
        String contextPath = request.getContextPath();
        
        // Remover context path si existe (normalmente /api)
        if (StringUtils.hasText(contextPath) && path.startsWith(contextPath)) {
            path = path.substring(contextPath.length());
        }
        
        final String normalizedPath = path;
        
        // PRIMERO: Verificar si es una ruta EXPLÍCITAMENTE PROTEGIDA
        for (String pattern : PROTECTED_PATH_PATTERNS) {
            if (normalizedPath.matches(pattern)) {
                logger.debug("🔐 JWT required for protected path: {}", normalizedPath);
                return false; // NO saltar el filtro - requiere JWT
            }
        }
        
        // SEGUNDO: Verificar rutas públicas exactas
        if (PUBLIC_EXACT_PATHS.contains(normalizedPath)) {
            logger.debug("⏭️ Skipping JWT filter for public path: {}", normalizedPath);
            return true; // Saltar el filtro - es pública
        }
        
        // TERCERO: Verificar patrones de rutas públicas
        for (String pattern : PUBLIC_PATH_PATTERNS) {
            if (normalizedPath.matches(pattern)) {
                logger.debug("⏭️ Skipping JWT filter for public path: {}", normalizedPath);
                return true; // Saltar el filtro - es pública
            }
        }
        
        // Por defecto, todas las demás rutas requieren autenticación
        logger.debug("🔐 JWT required for protected path: {}", normalizedPath);
        return false; // NO saltar el filtro - requiere JWT
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        logger.debug("🔍 Processing JWT for path: {}", request.getRequestURI());

        try {
            // Si ya hay autenticación establecida, continúa
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                String jwt = parseJwt(request);
                if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                    String username = jwtUtils.getUserNameFromJwtToken(jwt);

                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    logger.debug("✅ User authenticated: {}", username);
                } else {
                    logger.debug("❌ No valid JWT token found for protected path: {}", request.getRequestURI());
                }
            } else {
                logger.debug("🔄 Authentication already exists in context");
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            String token = headerAuth.substring(7);
            logger.debug("🔑 JWT token found in request");
            return token;
        }
        logger.debug("❌ No JWT token in Authorization header");
        return null;
    }
}