// PromoteUserRequest.java
package com.example.demo.dto.user;

import com.example.demo.entity.UserType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PromoteUserRequest {
    
    @NotNull(message = "Nuevo tipo de usuario es obligatorio")
    private UserType toUserType;
    
    @NotBlank(message = "Razón de la promoción es obligatoria")
    private String reason;
    
    // Constructors, getters and setters...
    public PromoteUserRequest() {}
    
    public UserType getToUserType() { return toUserType; }
    public void setToUserType(UserType toUserType) { this.toUserType = toUserType; }
    
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}