// ProposalException.java
package com.example.demo.exception;

public class ProposalException extends BadRequestException {
    
    public ProposalException(String message) {
        super("Error en propuesta: " + message);
    }
    
    public static ProposalException proposalNotPending(String proposalId) {
        return new ProposalException("La propuesta " + proposalId + " no está en estado pendiente");
    }
    
    public static ProposalException proposalAlreadyReviewed() {
        return new ProposalException("Esta propuesta ya fue revisada anteriormente");
    }
    
    public static ProposalException tooManyProposals(int maxProposals) {
        return new ProposalException("Has alcanzado el límite máximo de " + maxProposals + " propuestas pendientes");
    }
}