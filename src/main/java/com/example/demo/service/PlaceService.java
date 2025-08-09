package com.example.demo.service;


import com.example.demo.dto.place.CreatePlaceRequest;
import com.example.demo.dto.place.PlaceDTO;
import com.example.demo.dto.place.UpdatePlaceRequest;
import com.example.demo.entity.Place;
import com.example.demo.entity.PlaceType;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.PlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class PlaceService {

    private static final Logger logger = LoggerFactory.getLogger(PlaceService.class);

    @Autowired
    private PlaceRepository placeRepository;

    // Obtener todos los lugares
    public List<PlaceDTO> getAllPlaces() {
        List<Place> places = placeRepository.findAll();
        return places.stream()
                .map(PlaceDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Obtener lugar por ID
    public PlaceDTO getPlaceById(UUID id) {
        Place place = placeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lugar", "id", id));
        return PlaceDTO.fromEntity(place);
    }

    // Buscar lugares por texto
    public List<PlaceDTO> searchPlaces(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllPlaces();
        }

        List<Place> places = placeRepository.searchPlaces(query.trim());
        return places.stream()
                .map(PlaceDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Obtener lugares por tipo
    public List<PlaceDTO> getPlacesByType(String placeTypeString) {
        try {
            PlaceType placeType = PlaceType.valueOf(placeTypeString.toUpperCase());
            List<Place> places = placeRepository.findByPlaceType(placeType);
            return places.stream()
                    .map(PlaceDTO::fromEntity)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Tipo de lugar inválido: " + placeTypeString);
        }
    }

    // Obtener lugares disponibles
    public List<PlaceDTO> getAvailablePlaces(Boolean isAvailable) {
        List<Place> places = placeRepository.findByIsAvailable(isAvailable);
        return places.stream()
                .map(PlaceDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Obtener lugares por edificio
    public List<PlaceDTO> getPlacesByBuilding(String buildingName) {
        if (buildingName == null || buildingName.trim().isEmpty()) {
            throw new BadRequestException("Nombre del edificio es obligatorio");
        }

        List<Place> places = placeRepository.findByBuildingName(buildingName.trim());
        return places.stream()
                .map(PlaceDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Buscar lugar por código what3words
    public PlaceDTO getPlaceByWhat3words(String what3words) {
        if (what3words == null || what3words.trim().isEmpty()) {
            throw new BadRequestException("Código what3words es obligatorio");
        }

        Place place = placeRepository.findByWhat3words(what3words.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Lugar", "what3words", what3words));
        
        return PlaceDTO.fromEntity(place);
    }

    // Obtener lugares cercanos
    public List<PlaceDTO> getNearbyPlaces(BigDecimal latitude, BigDecimal longitude, BigDecimal radiusKm) {
        validateCoordinates(latitude, longitude);

        if (radiusKm == null || radiusKm.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("El radio debe ser mayor a 0");
        }

        if (radiusKm.compareTo(new BigDecimal("50")) > 0) {
            throw new BadRequestException("El radio máximo es 50km");
        }

        List<Place> places = placeRepository.findNearbyPlaces(latitude, longitude, radiusKm);
        return places.stream()
                .map(PlaceDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Crear nuevo lugar (solo ADMIN)
    public PlaceDTO createPlace(CreatePlaceRequest request) {
        validateCoordinates(request.getLatitude(), request.getLongitude());

        // Verificar que no exista un lugar con el mismo what3words
        if (request.getWhat3words() != null && !request.getWhat3words().trim().isEmpty()) {
            if (placeRepository.findByWhat3words(request.getWhat3words().trim()).isPresent()) {
                throw new BadRequestException("Ya existe un lugar con el código what3words: " + request.getWhat3words());
            }
        }

        Place place = new Place();
        mapRequestToEntity(request, place);

        try {
            Place savedPlace = placeRepository.save(place);
            logger.info("Nuevo lugar creado: {} (ID: {})", savedPlace.getName(), savedPlace.getId());
            return PlaceDTO.fromEntity(savedPlace);
        } catch (Exception e) {
            logger.error("Error creando lugar: {}", e.getMessage());
            throw new RuntimeException("Error interno creando el lugar");
        }
    }

    // Actualizar lugar existente (solo ADMIN)
    public PlaceDTO updatePlace(UUID id, UpdatePlaceRequest request) {
        Place place = placeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lugar", "id", id));

        if (request.getLatitude() != null && request.getLongitude() != null) {
            validateCoordinates(request.getLatitude(), request.getLongitude());
        }

        // Verificar what3words si se está actualizando
        if (request.getWhat3words() != null && !request.getWhat3words().trim().isEmpty()) {
            placeRepository.findByWhat3words(request.getWhat3words().trim())
                    .ifPresent(existingPlace -> {
                        if (!existingPlace.getId().equals(id)) {
                            throw new BadRequestException("Ya existe un lugar con el código what3words: " + request.getWhat3words());
                        }
                    });
        }

        mapUpdateRequestToEntity(request, place);

        try {
            Place updatedPlace = placeRepository.save(place);
            logger.info("Lugar actualizado: {} (ID: {})", updatedPlace.getName(), updatedPlace.getId());
            return PlaceDTO.fromEntity(updatedPlace);
        } catch (Exception e) {
            logger.error("Error actualizando lugar: {}", e.getMessage());
            throw new RuntimeException("Error interno actualizando el lugar");
        }
    }

    // Eliminar lugar (solo ADMIN)
    public void deletePlace(UUID id) {
        Place place = placeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lugar", "id", id));

        try {
            placeRepository.delete(place);
            logger.info("Lugar eliminado: {} (ID: {})", place.getName(), place.getId());
        } catch (Exception e) {
            logger.error("Error eliminando lugar: {}", e.getMessage());
            throw new RuntimeException("Error interno eliminando el lugar");
        }
    }

    // Métodos auxiliares privados
    private void validateCoordinates(BigDecimal latitude, BigDecimal longitude) {
        if (latitude == null || longitude == null) {
            throw new BadRequestException("Latitud y longitud son obligatorias");
        }

        if (latitude.compareTo(new BigDecimal("-90")) < 0 || latitude.compareTo(new BigDecimal("90")) > 0) {
            throw new BadRequestException("Latitud debe estar entre -90 y 90");
        }

        if (longitude.compareTo(new BigDecimal("-180")) < 0 || longitude.compareTo(new BigDecimal("180")) > 0) {
            throw new BadRequestException("Longitud debe estar entre -180 y 180");
        }
    }

    private void mapRequestToEntity(CreatePlaceRequest request, Place place) {
        place.setName(request.getName().trim());
        place.setCategory(request.getCategory().trim());
        place.setDescription(request.getDescription());
        place.setWhat3words(request.getWhat3words());
        place.setLatitude(request.getLatitude());
        place.setLongitude(request.getLongitude());
        place.setIsAvailable(request.getIsAvailable());
        place.setPlaceType(request.getPlaceType());
        place.setCapacity(request.getCapacity());
        place.setSchedule(request.getSchedule());
        place.setImageUrl(request.getImageUrl());
        place.setBuildingName(request.getBuildingName());
        place.setFloorNumber(request.getFloorNumber());
        place.setRoomCode(request.getRoomCode());
        
        if (request.getEquipment() != null) {
            place.setEquipment(request.getEquipment());
        }
        
        if (request.getAccessibilityFeatures() != null) {
            place.setAccessibilityFeatures(request.getAccessibilityFeatures());
        }
    }

    private void mapUpdateRequestToEntity(UpdatePlaceRequest request, Place place) {
        if (request.getName() != null) {
            place.setName(request.getName().trim());
        }
        if (request.getCategory() != null) {
            place.setCategory(request.getCategory().trim());
        }
        if (request.getDescription() != null) {
            place.setDescription(request.getDescription());
        }
        if (request.getWhat3words() != null) {
            place.setWhat3words(request.getWhat3words());
        }
        if (request.getLatitude() != null) {
            place.setLatitude(request.getLatitude());
        }
        if (request.getLongitude() != null) {
            place.setLongitude(request.getLongitude());
        }
        if (request.getIsAvailable() != null) {
            place.setIsAvailable(request.getIsAvailable());
        }
        if (request.getPlaceType() != null) {
            place.setPlaceType(request.getPlaceType());
        }
        if (request.getCapacity() != null) {
            place.setCapacity(request.getCapacity());
        }
        if (request.getSchedule() != null) {
            place.setSchedule(request.getSchedule());
        }
        if (request.getImageUrl() != null) {
            place.setImageUrl(request.getImageUrl());
        }
        if (request.getBuildingName() != null) {
            place.setBuildingName(request.getBuildingName());
        }
        if (request.getFloorNumber() != null) {
            place.setFloorNumber(request.getFloorNumber());
        }
        if (request.getRoomCode() != null) {
            place.setRoomCode(request.getRoomCode());
        }
        if (request.getEquipment() != null) {
            place.setEquipment(request.getEquipment());
        }
        if (request.getAccessibilityFeatures() != null) {
            place.setAccessibilityFeatures(request.getAccessibilityFeatures());
        }
    }

    // Método para obtener lugar por ID como entidad (para uso interno)
    public Place getPlaceEntityById(UUID id) {
        return placeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lugar", "id", id));
    }
}