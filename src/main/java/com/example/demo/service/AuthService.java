package com.example.demo.service;


import com.example.demo.dto.auth.AuthResponse;
import com.example.demo.dto.auth.LoginRequest;
import com.example.demo.dto.auth.RegisterRequest;
import com.example.demo.dto.user.UserDTO;
import com.example.demo.entity.User;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    public AuthResponse login(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            User user = (User) authentication.getPrincipal();
            UserDTO userDTO = UserDTO.fromEntity(user);

            logger.info("Usuario {} ha iniciado sesión exitosamente", user.getEmail());

            return new AuthResponse(jwt, userDTO);

        } catch (Exception e) {
            logger.error("Error en login para email {}: {}", loginRequest.getEmail(), e.getMessage());
            throw new BadRequestException("Credenciales inválidas");
        }
    }

    public AuthResponse register(RegisterRequest registerRequest) {
        // Verificar si el email ya existe
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BadRequestException("El email ya está registrado: " + registerRequest.getEmail());
        }

        // Verificar si el studentId ya existe (si se proporciona)
        if (registerRequest.getStudentId() != null && 
            !registerRequest.getStudentId().trim().isEmpty()) {
            Optional<User> existingUser = userRepository.findByStudentId(registerRequest.getStudentId());
            if (existingUser.isPresent()) {
                throw new BadRequestException("El ID de estudiante ya está registrado: " + registerRequest.getStudentId());
            }
        }

        try {
            // Crear nuevo usuario
            User user = new User();
            user.setEmail(registerRequest.getEmail().toLowerCase().trim());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            user.setFullName(registerRequest.getFullName().trim());
            user.setUserType(registerRequest.getUserType());
            user.setIsActive(true);

            if (registerRequest.getStudentId() != null && 
                !registerRequest.getStudentId().trim().isEmpty()) {
                user.setStudentId(registerRequest.getStudentId().trim());
            }

            User savedUser = userRepository.save(user);

            // Generar token JWT
            String jwt = jwtUtils.generateTokenFromUsername(savedUser.getEmail());

            UserDTO userDTO = UserDTO.fromEntity(savedUser);

            logger.info("Nuevo usuario registrado: {}", savedUser.getEmail());

            return new AuthResponse(jwt, userDTO);

        } catch (Exception e) {
            logger.error("Error en registro para email {}: {}", registerRequest.getEmail(), e.getMessage());
            throw new RuntimeException("Error interno en el registro del usuario");
        }
    }

    public UserDTO getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadRequestException("No hay usuario autenticado");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        return UserDTO.fromEntity(user);
    }

    public User getCurrentUserEntity() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadRequestException("No hay usuario autenticado");
        }

        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email.toLowerCase().trim());
    }

    public boolean isStudentIdAvailable(String studentId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            return true;
        }
        return !userRepository.findByStudentId(studentId.trim()).isPresent();
    }

    public void validateRegisterRequest(RegisterRequest request) {
        if (!isEmailAvailable(request.getEmail())) {
            throw new BadRequestException("El email ya está registrado");
        }

        if (!isStudentIdAvailable(request.getStudentId())) {
            throw new BadRequestException("El ID de estudiante ya está registrado");
        }

        // Validaciones adicionales
        if (request.getPassword().length() < 6) {
            throw new BadRequestException("La contraseña debe tener al menos 6 caracteres");
        }

        if (request.getFullName() == null || request.getFullName().trim().length() < 2) {
            throw new BadRequestException("El nombre completo es obligatorio");
        }
    }
}