// PlaceType.java - Enum actualizado con displayName
package com.example.demo.entity;

public enum PlaceType {
    CLASSROOM("Aula", "Salón de clases"),
    LABORATORY("Laboratorio", "Laboratorio académico"),
    LIBRARY("Biblioteca", "Biblioteca universitaria"),
    CAFETERIA("Cafetería", "Área de comidas"),
    OFFICE("Oficina", "Oficina administrativa o académica"),
    AUDITORIUM("Auditorio", "Auditorio o sala de conferencias"),
    SERVICE("Servicio", "Servicios generales (baños, cajeros, etc.)"),
    PARKING("Estacionamiento", "Área de estacionamiento"),
    RECREATION("Recreación", "Área recreativa o deportiva"),
    ENTRANCE("Entrada", "Entrada principal o secundaria");
    
    private final String displayName;
    private final String description;
    
    PlaceType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static PlaceType fromString(String placeType) {
        for (PlaceType type : PlaceType.values()) {
            if (type.displayName.equalsIgnoreCase(placeType) ||
                type.name().equalsIgnoreCase(placeType)) {
                return type;
            }
        }
        return SERVICE; // Default
    }
    
    public boolean isAcademic() {
        return this == CLASSROOM || this == LABORATORY || this == LIBRARY;
    }
    
    public boolean isPublic() {
        return this == CAFETERIA || this == SERVICE || this == RECREATION || this == ENTRANCE;
    }
    
    public boolean isRouteDestination() {
        // Todos los tipos pueden ser destinos de rutas por defecto
        return true;
    }
}