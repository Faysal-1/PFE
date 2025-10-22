package org.baeldung.persistence.model.hebergementModel;

/**
 * Enumération représentant les différents statuts de validation d'un hébergement
 */
public enum ValidationStatus {
    EN_ATTENTE("En attente"),
    ACCEPTE("Accepté"),
    REFUSE("Refusé");
    
    private final String displayName;
    
    ValidationStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
