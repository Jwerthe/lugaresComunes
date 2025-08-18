// UserType.java - Enum actualizado con displayName
package com.example.demo.entity;

public enum UserType {
    VISITOR("Visitante", "Usuario visitante con acceso básico"),
    STUDENT("Estudiante", "Estudiante de la universidad"),
    TEACHER("Profesor", "Profesor de la universidad"),
    ADMIN("Administrador", "Administrador del sistema"),
    STAFF("Personal", "Personal administrativo de la universidad");
    
    private final String displayName;
    private final String description;
    
    UserType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static UserType fromString(String userType) {
        for (UserType type : UserType.values()) {
            if (type.displayName.equalsIgnoreCase(userType) ||
                type.name().equalsIgnoreCase(userType)) {
                return type;
            }
        }
        return VISITOR; // Default
    }
    
    public boolean isAdmin() {
        return this == ADMIN;
    }
    
    public boolean canCreateRoutes() {
        return this == ADMIN;
    }
    
    public boolean canPromoteUsers() {
        return this == ADMIN;
    }
    
    public boolean canSubmitProposals() {
        return this != null; // Todos los usuarios autenticados pueden enviar propuestas
    }
}