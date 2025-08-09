package com.example.demo.entity;

public enum ReportStatus {
    PENDING("Pendiente"),
    IN_PROGRESS("En progreso"),
    RESOLVED("Resuelto"),
    DISMISSED("Desestimado");
    
    private final String displayName;
    
    ReportStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}