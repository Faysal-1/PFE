package com.maroctbib.modules.core.audit;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Entity
@Table(name = "audit_logs")
public class AuditLog {
    
    public enum ActionType {
        LOGIN, 
        LOGIN_FAILURE, 
        PASSWORD_CHANGE,
        PROFILE_UPDATE,
        APPOINTMENT_CREATE,
        APPOINTMENT_UPDATE,
        APPOINTMENT_CANCEL,
        DOCTOR_VERIFICATION
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ActionType action;
    
    @Column(nullable = false)
    private String principal;
    
    private String resourceId;
    private String resourceType;
    
    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> details;
    
    private String clientIp;
    private String userAgent;
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
