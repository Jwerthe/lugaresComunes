// ReportType.java - Enum actualizado con displayName
package com.example.demo.entity;

public enum ReportType {
    INCORRECT_INFO("Información Incorrecta", "La información del lugar es incorrecta"),
    MAINTENANCE_NEEDED("Mantenimiento Requerido", "El lugar necesita mantenimiento"),
    UNAVAILABLE("No Disponible", "El lugar no está disponible temporalmente"),
    ACCESSIBILITY_ISSUE("Problema de Accesibilidad", "Problemas de accesibilidad"),
    ROUTE_ISSUE("Problema de Ruta", "Problema con las rutas hacia este lugar"),
    OTHER("Otro", "Otro tipo de problema");
    
    private final String displayName;
    private final String description;
    
    ReportType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static ReportType fromString(String reportType) {
        for (ReportType type : ReportType.values()) {
            if (type.displayName.equalsIgnoreCase(reportType) ||
                type.name().equalsIgnoreCase(reportType)) {
                return type;
            }
        }
        return OTHER; // Default
    }
}
