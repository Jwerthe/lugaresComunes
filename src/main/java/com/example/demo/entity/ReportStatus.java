// ReportStatus.java - Enum actualizado con displayName
package com.example.demo.entity;

public enum ReportStatus {
    PENDING("Pendiente", "Reporte en espera de revisión"),
    IN_PROGRESS("En Progreso", "Reporte siendo atendido"),
    RESOLVED("Resuelto", "Reporte resuelto exitosamente"),
    DISMISSED("Descartado", "Reporte descartado sin acción");
    
    private final String displayName;
    private final String description;
    
    ReportStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static ReportStatus fromString(String status) {
        for (ReportStatus s : ReportStatus.values()) {
            if (s.displayName.equalsIgnoreCase(status) ||
                s.name().equalsIgnoreCase(status)) {
                return s;
            }
        }
        return PENDING; // Default
    }
    
    public boolean isActive() {
        return this == PENDING || this == IN_PROGRESS;
    }
    
    public boolean isClosed() {
        return this == RESOLVED || this == DISMISSED;
    }
}