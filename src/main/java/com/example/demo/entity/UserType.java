package com.example.demo.entity;

public enum UserType {
    STUDENT("Estudiante"),
    TEACHER("Docente"), 
    ADMIN("Administrador"),
    STAFF("Personal Administrativo"),
    VISITOR("Visitante");
    
    private final String displayName;
    
    UserType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}