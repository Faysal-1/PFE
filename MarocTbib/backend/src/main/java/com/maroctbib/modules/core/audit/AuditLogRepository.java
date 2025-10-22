package com.maroctbib.modules.core.audit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
    
    @Modifying
    @Query("DELETE FROM AuditLog a WHERE a.createdAt < :retentionDate")
    void deleteOlderThan(@Param("retentionDate") LocalDateTime retentionDate);
    
    @Query("SELECT COUNT(a) > 0 FROM AuditLog a WHERE a.principal = :principal AND a.action = :action AND a.createdAt > :since")
    boolean existsByPrincipalAndActionSince(
        @Param("principal") String principal,
        @Param("action") AuditLog.ActionType action,
        @Param("since") LocalDateTime since
    );
}
