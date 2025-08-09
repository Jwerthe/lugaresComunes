package com.example.demo.service;


import com.example.demo.dto.place.PlaceDTO;
import com.example.demo.entity.Place;
import com.example.demo.entity.User;
import com.example.demo.entity.UserFavorite;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ConflictException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.UserFavoriteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserFavoriteService {

    private static final Logger logger = LoggerFactory.getLogger(UserFavoriteService.class);

    @Autowired
    private UserFavoriteRepository userFavoriteRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private PlaceService placeService;

    // Obtener todos los favoritos del usuario actual
    public List<PlaceDTO> getCurrentUserFavorites() {
        User currentUser = authService.getCurrentUserEntity();
        
        List<UserFavorite> favorites = userFavoriteRepository.findByUserIdWithPlace(currentUser.getId());
        
        return favorites.stream()
                .map(favorite -> PlaceDTO.fromEntity(favorite.getPlace()))
                .collect(Collectors.toList());
    }

    // Agregar lugar a favoritos del usuario actual
    public PlaceDTO addToFavorites(UUID placeId) {
        if (placeId == null) {
            throw new BadRequestException("ID del lugar es obligatorio");
        }

        User currentUser = authService.getCurrentUserEntity();
        Place place = placeService.getPlaceEntityById(placeId);

        // Verificar si ya está en favoritos
        if (userFavoriteRepository.existsByUserAndPlace(currentUser, place)) {
            throw new ConflictException("El lugar ya está en favoritos");
        }

        try {
            UserFavorite favorite = new UserFavorite(currentUser, place);
            userFavoriteRepository.save(favorite);

            logger.info("Lugar {} agregado a favoritos del usuario {}", 
                       place.getName(), currentUser.getEmail());

            return PlaceDTO.fromEntity(place);
        } catch (Exception e) {
            logger.error("Error agregando lugar a favoritos: {}", e.getMessage());
            throw new RuntimeException("Error interno agregando a favoritos");
        }
    }

    // Remover lugar de favoritos del usuario actual
    public void removeFromFavorites(UUID placeId) {
        if (placeId == null) {
            throw new BadRequestException("ID del lugar es obligatorio");
        }

        User currentUser = authService.getCurrentUserEntity();
        Place place = placeService.getPlaceEntityById(placeId);

        UserFavorite favorite = userFavoriteRepository.findByUserAndPlace(currentUser, place)
                .orElseThrow(() -> new ResourceNotFoundException("El lugar no está en favoritos"));

        try {
            userFavoriteRepository.delete(favorite);

            logger.info("Lugar {} removido de favoritos del usuario {}", 
                       place.getName(), currentUser.getEmail());
        } catch (Exception e) {
            logger.error("Error removiendo lugar de favoritos: {}", e.getMessage());
            throw new RuntimeException("Error interno removiendo de favoritos");
        }
    }

    // Verificar si un lugar está en favoritos del usuario actual
    public boolean isPlaceInFavorites(UUID placeId) {
        if (placeId == null) {
            return false;
        }

        try {
            User currentUser = authService.getCurrentUserEntity();
            Place place = placeService.getPlaceEntityById(placeId);
            
            return userFavoriteRepository.existsByUserAndPlace(currentUser, place);
        } catch (Exception e) {
            logger.warn("Error verificando si lugar está en favoritos: {}", e.getMessage());
            return false;
        }
    }

    // Toggle favorito (agregar si no está, remover si está)
    public FavoriteToggleResult toggleFavorite(UUID placeId) {
        if (placeId == null) {
            throw new BadRequestException("ID del lugar es obligatorio");
        }

        User currentUser = authService.getCurrentUserEntity();
        Place place = placeService.getPlaceEntityById(placeId);

        boolean wasInFavorites = userFavoriteRepository.existsByUserAndPlace(currentUser, place);

        try {
            if (wasInFavorites) {
                UserFavorite favorite = userFavoriteRepository.findByUserAndPlace(currentUser, place)
                        .orElseThrow(() -> new ResourceNotFoundException("Favorito no encontrado"));
                userFavoriteRepository.delete(favorite);
                
                logger.info("Lugar {} removido de favoritos del usuario {}", 
                           place.getName(), currentUser.getEmail());
                
                return new FavoriteToggleResult(false, "Removido de favoritos", PlaceDTO.fromEntity(place));
            } else {
                UserFavorite favorite = new UserFavorite(currentUser, place);
                userFavoriteRepository.save(favorite);
                
                logger.info("Lugar {} agregado a favoritos del usuario {}", 
                           place.getName(), currentUser.getEmail());
                
                return new FavoriteToggleResult(true, "Agregado a favoritos", PlaceDTO.fromEntity(place));
            }
        } catch (Exception e) {
            logger.error("Error en toggle favorito: {}", e.getMessage());
            throw new RuntimeException("Error interno en operación de favoritos");
        }
    }

    // Obtener cantidad de favoritos del usuario actual
    public int getFavoritesCount() {
        User currentUser = authService.getCurrentUserEntity();
        return userFavoriteRepository.findByUserId(currentUser.getId()).size();
    }

    // Limpiar todos los favoritos del usuario actual
    public void clearAllFavorites() {
        User currentUser = authService.getCurrentUserEntity();
        List<UserFavorite> favorites = userFavoriteRepository.findByUserId(currentUser.getId());
        
        if (!favorites.isEmpty()) {
            try {
                userFavoriteRepository.deleteAll(favorites);
                logger.info("Todos los favoritos del usuario {} han sido eliminados", 
                           currentUser.getEmail());
            } catch (Exception e) {
                logger.error("Error limpiando favoritos: {}", e.getMessage());
                throw new RuntimeException("Error interno limpiando favoritos");
            }
        }
    }

    // Clase para el resultado del toggle
    public static class FavoriteToggleResult {
        private boolean isNowFavorite;
        private String message;
        private PlaceDTO place;

        public FavoriteToggleResult(boolean isNowFavorite, String message, PlaceDTO place) {
            this.isNowFavorite = isNowFavorite;
            this.message = message;
            this.place = place;
        }

        // Getters and setters
        public boolean isNowFavorite() {
            return isNowFavorite;
        }

        public void setNowFavorite(boolean nowFavorite) {
            isNowFavorite = nowFavorite;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public PlaceDTO getPlace() {
            return place;
        }

        public void setPlace(PlaceDTO place) {
            this.place = place;
        }
    }
}