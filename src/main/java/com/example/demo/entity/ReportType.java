package com.example.demo.entity;

public enum ReportType {
    INCORRECT_INFO("Información incorrecta"),
    MAINTENANCE_NEEDED("Necesita mantenimiento"),
    UNAVAILABLE("No disponible"),
    ACCESSIBILITY_ISSUE("Problema de accesibilidad"),
    OTHER("Otro");
    
    private final String displayName;
    
    ReportType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
