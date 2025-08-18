// RouteProposalController.java
package com.example.demo.controller;

import com.example.demo.dto.route.*;
import com.example.demo.service.RouteProposalService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/routes/proposals")
@CrossOrigin(origins = "*", maxAge = 3600)
public class RouteProposalController {

    private static final Logger logger = LoggerFactory.getLogger(RouteProposalController.class);

    @Autowired
    private RouteProposalService proposalService;

    // 🔐 ENDPOINTS PROTEGIDOS (cualquier usuario logueado)

    /**
     * POST /api/routes/proposals - Enviar propuesta de nueva ruta
     */
    @PostMapping("")
    public ResponseEntity<?> createProposal(@Valid @RequestBody CreateRouteProposalRequest request) {
        try {
            RouteProposalDTO proposal = proposalService.createProposal(request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Propuesta de ruta enviada exitosamente");
            response.put("data", proposal);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            logger.error("Error creando propuesta de ruta: {}", e.getMessage());
            return createErrorResponse(e.getMessage(), determineHttpStatus(e));
        }
    }

    /**
     * GET /api/routes/proposals/my - Ver mis propuestas enviadas
     */
    @GetMapping("/my")
    public ResponseEntity<?> getMyProposals() {
        try {
            List<RouteProposalDTO> proposals = proposalService.getMyProposals();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Mis propuestas obtenidas exitosamente");
            response.put("count", proposals.size());
            response.put("data", proposals);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error obteniendo mis propuestas: {}", e.getMessage());
            return createErrorResponse("Error obteniendo propuestas", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 🛡️ ENDPOINTS SOLO ADMIN

    /**
     * GET /api/routes/proposals/pending - Ver propuestas pendientes
     */
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getPendingProposals() {
        try {
            List<RouteProposalDTO> proposals = proposalService.getPendingProposals();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Propuestas pendientes obtenidas exitosamente");
            response.put("count", proposals.size());
            response.put("data", proposals);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error obteniendo propuestas pendientes: {}", e.getMessage());
            return createErrorResponse("Error obteniendo propuestas pendientes", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * PUT /api/routes/proposals/{proposalId}/approve - Aprobar propuesta y crear ruta
     */
    @PutMapping("/{proposalId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> approveProposal(@PathVariable UUID proposalId, 
                                           @RequestParam(required = false) String notes) {
        try {
            RouteDTO route = proposalService.approveProposal(proposalId, notes);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Propuesta aprobada y ruta creada exitosamente");
            response.put("proposalId", proposalId);
            response.put("data", route);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error aprobando propuesta {}: {}", proposalId, e.getMessage());
            return createErrorResponse(e.getMessage(), determineHttpStatus(e));
        }
    }

    /**
     * PUT /api/routes/proposals/{proposalId}/reject - Rechazar propuesta
     */
    @PutMapping("/{proposalId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> rejectProposal(@PathVariable UUID proposalId, 
                                          @RequestParam(required = false) String notes) {
        try {
            RouteProposalDTO proposal = proposalService.rejectProposal(proposalId, notes);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Propuesta rechazada exitosamente");
            response.put("data", proposal);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error rechazando propuesta {}: {}", proposalId, e.getMessage());
            return createErrorResponse(e.getMessage(), determineHttpStatus(e));
        }
    }

    // 🔧 MÉTODOS AUXILIARES

    private HttpStatus determineHttpStatus(Exception e) {
        String message = e.getMessage().toLowerCase();
        
        if (message.contains("not found") || message.contains("no encontrado")) {
            return HttpStatus.NOT_FOUND;
        } else if (message.contains("bad request") || message.contains("obligatorio")) {
            return HttpStatus.BAD_REQUEST;
        } else {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    private ResponseEntity<?> createErrorResponse(String message, HttpStatus status) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", message);
        errorResponse.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.status(status).body(errorResponse);
    }

    // Health check endpoint
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Route proposals service is healthy");
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
}