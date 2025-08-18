// ProposalStatus.java
package com.example.demo.entity;

public enum ProposalStatus {
    PENDING("Pendiente", "Propuesta en espera de revisión"),
    APPROVED("Aprobada", "Propuesta aprobada y convertida en ruta oficial"),
    REJECTED("Rechazada", "Propuesta rechazada por el administrador");
    
    private final String displayName;
    private final String description;
    
    ProposalStatus(String displayName, String description) {
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