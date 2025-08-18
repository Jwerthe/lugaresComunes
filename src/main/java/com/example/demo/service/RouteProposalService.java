// RouteProposalService.java
package com.example.demo.service;

import com.example.demo.dto.route.*;
import com.example.demo.entity.*;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.math.BigDecimal;

@Service
@Transactional
public class RouteProposalService {

    private static final Logger logger = LoggerFactory.getLogger(RouteProposalService.class);

    @Autowired
    private RouteProposalRepository proposalRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private RoutePointRepository routePointRepository;

    @Autowired
    private PlaceService placeService;

    @Autowired
    private AuthService authService;

    // 🔐 ENDPOINTS PROTEGIDOS (cualquier usuario logueado)

    /**
     * POST /api/routes/proposals - Enviar propuesta de nueva ruta
     */
    public RouteProposalDTO createProposal(CreateRouteProposalRequest request) {
        logger.info("💡 Usuario enviando propuesta de ruta: {}", request.getTitle());
        
        User proposer = authService.getCurrentUserEntity();
        Place destination = placeService.getPlaceEntityById(request.getToPlaceId());
        
        validateCreateProposalRequest(request);

        RouteProposal proposal = new RouteProposal();
        proposal.setProposedBy(proposer);
        proposal.setTitle(request.getTitle().trim());
        proposal.setDescription(request.getDescription().trim());
        proposal.setFromLatitude(request.getFromLatitude());
        proposal.setFromLongitude(request.getFromLongitude());
        proposal.setFromDescription(request.getFromDescription());
        proposal.setToPlace(destination);
        proposal.setProposedPoints(request.getProposedPoints());

        RouteProposal savedProposal = proposalRepository.save(proposal);
        
        // Agregar puntos de contribución por enviar propuesta
        proposer.addContributionPoints(10); // 10 puntos por propuesta
        
        logger.info("✅ Propuesta de ruta enviada exitosamente por: {}", proposer.getEmail());
        return RouteProposalDTO.fromEntity(savedProposal);
    }

    /**
     * GET /api/routes/proposals/my - Ver mis propuestas enviadas
     */
    public List<RouteProposalDTO> getMyProposals() {
        logger.info("📋 Usuario obteniendo sus propuestas");
        
        User currentUser = authService.getCurrentUserEntity();
        List<RouteProposal> proposals = proposalRepository.findByProposedByOrderByCreatedAtDesc(currentUser);
        
        return proposals.stream()
                .map(RouteProposalDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // 🛡️ ENDPOINTS SOLO ADMIN

    /**
     * GET /api/routes/proposals/pending - Ver propuestas pendientes
     */
    public List<RouteProposalDTO> getPendingProposals() {
        logger.info("📋 Admin obteniendo propuestas pendientes");
        
        List<RouteProposal> proposals = proposalRepository.findByStatusOrderByCreatedAtAsc(ProposalStatus.PENDING);
        
        return proposals.stream()
                .map(RouteProposalDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * PUT /api/routes/proposals/{proposalId}/approve - Aprobar propuesta y crear ruta
     */
    public RouteDTO approveProposal(UUID proposalId, String adminNotes) {
        logger.info("✅ Admin aprobando propuesta: {}", proposalId);
        
        User admin = authService.getCurrentUserEntity();
        RouteProposal proposal = getProposalEntityById(proposalId);
        
        if (!proposal.isPending()) {
            throw new BadRequestException("Solo se pueden aprobar propuestas pendientes");
        }

        // Crear ruta oficial basada en la propuesta
        Route route = createRouteFromProposal(proposal, admin);
        Route savedRoute = routeRepository.save(route);
        
        // Marcar propuesta como aprobada
        proposal.approve(admin, adminNotes);
        proposal.setCreatedRoute(savedRoute);
        proposalRepository.save(proposal);
        
        // Recompensar al usuario que propuso
        User proposer = proposal.getProposedBy();
        proposer.addContributionPoints(50); // 50 puntos por propuesta aprobada
        
        // Verificar si es elegible para promoción
        if (proposer.isEligibleForPromotion()) {
            logger.info("🎉 Usuario {} es elegible para promoción", proposer.getEmail());
        }
        
        // Actualizar contador de rutas del destino
        proposal.getToPlace().incrementRouteCount();
        
        logger.info("✅ Propuesta aprobada y ruta creada: {}", savedRoute.getName());
        return RouteDTO.fromEntity(savedRoute);
    }

    /**
     * PUT /api/routes/proposals/{proposalId}/reject - Rechazar propuesta
     */
    public RouteProposalDTO rejectProposal(UUID proposalId, String adminNotes) {
        logger.info("❌ Admin rechazando propuesta: {}", proposalId);
        
        User admin = authService.getCurrentUserEntity();
        RouteProposal proposal = getProposalEntityById(proposalId);
        
        if (!proposal.isPending()) {
            throw new BadRequestException("Solo se pueden rechazar propuestas pendientes");
        }

        proposal.reject(admin, adminNotes);
        RouteProposal updatedProposal = proposalRepository.save(proposal);
        
        logger.info("✅ Propuesta rechazada con comentarios del admin");
        return RouteProposalDTO.fromEntity(updatedProposal);
    }

    // 🔧 MÉTODOS AUXILIARES PRIVADOS

    private RouteProposal getProposalEntityById(UUID proposalId) {
        return proposalRepository.findById(proposalId)
                .orElseThrow(() -> new ResourceNotFoundException("Propuesta", "id", proposalId));
    }

    private void validateCreateProposalRequest(CreateRouteProposalRequest request) {
        if (request.getFromLatitude() == null || request.getFromLongitude() == null) {
            throw new BadRequestException("Coordenadas de inicio son obligatorias");
        }

        // Validar que las coordenadas estén en rangos válidos
        if (request.getFromLatitude().compareTo(new BigDecimal("-90")) < 0 || 
            request.getFromLatitude().compareTo(new BigDecimal("90")) > 0) {
            throw new BadRequestException("Latitud debe estar entre -90 y 90");
        }

        if (request.getFromLongitude().compareTo(new BigDecimal("-180")) < 0 || 
            request.getFromLongitude().compareTo(new BigDecimal("180")) > 0) {
            throw new BadRequestException("Longitud debe estar entre -180 y 180");
        }
    }

    private Route createRouteFromProposal(RouteProposal proposal, User admin) {
        Route route = new Route();
        route.setName(proposal.getGeneratedRouteName());
        route.setDescription(proposal.getDescription());
        route.setFromLatitude(proposal.getFromLatitude());
        route.setFromLongitude(proposal.getFromLongitude());
        route.setFromDescription(proposal.getFromDescription());
        route.setToPlace(proposal.getToPlace());
        route.setCreatedBy(admin); // El admin que aprueba se convierte en creador oficial
        route.setDifficulty(RouteDifficulty.EASY); // Por defecto fácil, el admin puede cambiar después
        route.setIsActive(true);
        
        // TODO: Procesar proposedPoints JSON y crear RoutePoints
        // Por ahora creamos puntos básicos de inicio y fin
        createBasicRoutePointsFromProposal(route, proposal);
        
        return route;
    }

    private void createBasicRoutePointsFromProposal(Route route, RouteProposal proposal) {
        // Crear punto de inicio
        RoutePoint startPoint = new RoutePoint();
        startPoint.setRoute(route);
        startPoint.setLatitude(proposal.getFromLatitude());
        startPoint.setLongitude(proposal.getFromLongitude());
        startPoint.setOrderIndex(0);
        startPoint.setPointType(RoutePointType.START);
        startPoint.setInstruction("Punto de inicio de la ruta propuesta");
        
        // Crear punto final
        RoutePoint endPoint = new RoutePoint();
        endPoint.setRoute(route);
        endPoint.setLatitude(proposal.getToPlace().getLatitude());
        endPoint.setLongitude(proposal.getToPlace().getLongitude());
        endPoint.setOrderIndex(1);
        endPoint.setPointType(RoutePointType.END);
        endPoint.setInstruction("Has llegado a tu destino");
        
        // Guardar puntos
        routePointRepository.save(startPoint);
        routePointRepository.save(endPoint);
        
        route.getRoutePoints().add(startPoint);
        route.getRoutePoints().add(endPoint);
    }
}