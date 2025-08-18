// ProposalValidator.java
package com.example.demo.validation;

import com.example.demo.dto.route.CreateRouteProposalRequest;
import com.example.demo.entity.User;
import com.example.demo.exception.ProposalException;
import com.example.demo.repository.RouteProposalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ProposalValidator {
    
    @Autowired
    private RouteProposalRepository proposalRepository;
    
    @Autowired
    private RouteValidator routeValidator;
    
    private static final int MAX_PENDING_PROPOSALS_PER_USER = 5;
    private static final int MAX_TITLE_LENGTH = 100;
    private static final int MAX_DESCRIPTION_LENGTH = 1000;
    
    public void validateCreateProposalRequest(CreateRouteProposalRequest request, User proposer) {
        validateProposerLimits(proposer);
        validateProposalContent(request);
        validateProposalCoordinates(request);
    }
    
    private void validateProposerLimits(User proposer) {
        // Verificar límite de propuestas pendientes
        long pendingCount = proposalRepository.countByProposedByAndStatus(
            proposer, com.example.demo.entity.ProposalStatus.PENDING);
        
        if (pendingCount >= MAX_PENDING_PROPOSALS_PER_USER) {
            throw ProposalException.tooManyProposals(MAX_PENDING_PROPOSALS_PER_USER);
        }
    }
    
    private void validateProposalContent(CreateRouteProposalRequest request) {
        // Validar título
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new ProposalException("El título es obligatorio");
        }
        
        if (request.getTitle().length() > MAX_TITLE_LENGTH) {
            throw new ProposalException("El título no puede exceder " + MAX_TITLE_LENGTH + " caracteres");
        }
        
        // Validar descripción
        if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            throw new ProposalException("La descripción es obligatoria");
        }
        
        if (request.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
            throw new ProposalException("La descripción no puede exceder " + MAX_DESCRIPTION_LENGTH + " caracteres");
        }
        
        // Validar que la descripción sea lo suficientemente detallada
        if (request.getDescription().trim().length() < 20) {
            throw new ProposalException("La descripción debe tener al menos 20 caracteres para ser útil");
        }
    }
    
    private void validateProposalCoordinates(CreateRouteProposalRequest request) {
        try {
            routeValidator.validateCoordinates(request.getFromLatitude(), request.getFromLongitude());
        } catch (Exception e) {
            throw new ProposalException("Coordenadas inválidas: " + e.getMessage());
        }
    }
}