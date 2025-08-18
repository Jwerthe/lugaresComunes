
// RouteDifficulty.java
package com.example.demo.entity;

public enum RouteDifficulty {
    EASY("Fácil", "Ruta simple y directa"),
    MEDIUM("Medio", "Ruta con algunas complicaciones"),
    HARD("Difícil", "Ruta compleja o con obstáculos");
    
    private final String displayName;
    private final String description;
    
    RouteDifficulty(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static RouteDifficulty fromString(String difficulty) {
        for (RouteDifficulty d : RouteDifficulty.values()) {
            if (d.displayName.equalsIgnoreCase(difficulty) ||
                d.name().equalsIgnoreCase(difficulty)) {
                return d;
            }
        }
        return EASY; // Default
    }
}