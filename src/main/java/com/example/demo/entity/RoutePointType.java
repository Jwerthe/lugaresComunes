// RoutePointType.java
package com.example.demo.entity;

public enum RoutePointType {
    START("Inicio", "Punto de inicio de la ruta"),
    WAYPOINT("Punto intermedio", "Punto de referencia en la ruta"),
    TURN("Giro", "Punto donde se debe girar"),
    LANDMARK("Punto de referencia", "Elemento notable del campus"),
    END("Final", "Destino final de la ruta");
    
    private final String displayName;
    private final String description;
    
    RoutePointType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
}