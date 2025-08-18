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

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtils jwtUtils, UserDetailsService userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    // 🔓 Rutas públicas (SIN el prefijo de contexto /api)
    private static final List<String> PUBLIC_PATHS = List.of(
        // Auth
        "/auth/login",
        "/auth/register",
        "/auth/health",
        "/auth/validate-email",
        "/auth/validate-student-id",

        // Places
        "/places",
        "/places/search",
        "/places/nearby",
        "/places/type",
        "/places/available",
        "/places/building",
        "/places/what3words",

        // Routes
        "/routes/destinations",
        "/routes/to",              // p.ej. /routes/to/{dest}
        "/routes/nearest",
        "/routes/health",
        "/routes/proposals/health",
        "/routes/",                // para cubrir /routes/*/points y /routes/*/details

        // Navigation & Users health
        "/navigation/health",
        "/users/health",

        // General health
        "/health"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // Permitir preflight CORS
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            logger.debug("⏭️ Skipping JWT filter for CORS preflight OPTIONS");
            return true;
        }

        // Quitar context path (p.ej. /api) antes de comparar
        String path = request.getRequestURI();      // e.g. /api/auth/health
        String ctx  = request.getContextPath();     // e.g. /api
        if (StringUtils.hasText(ctx) && path.startsWith(ctx)) {
            path = path.substring(ctx.length());    // e.g. /auth/health
        }

        final String normalized = path;

        boolean isPublic = PUBLIC_PATHS.stream().anyMatch(publicPath ->
            normalized.equals(publicPath) || normalized.startsWith(publicPath)
        );

        if (isPublic) {
            logger.debug("⏭️ Skipping JWT filter for public path: {}", normalized);
        }
        return isPublic;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        logger.debug("🔐 Processing JWT for path: {}", request.getRequestURI());

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
                    logger.debug("❌ No valid JWT token found");
                }
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}
