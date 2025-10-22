package com.maroctbib.modules.core.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.security")
public class SecurityProperties {
    private JwtProperties jwt;
    private PasswordEncoderProperties passwordEncoder;
    private CorsProperties cors;
    private RateLimitProperties rateLimit;
    private AuditLogProperties auditLog;
    private BackupProperties backup;

    @Data
    public static class JwtProperties {
        private String secret;
        private long expirationMs;
        private long refreshExpirationMs;
    }

    @Data
    public static class PasswordEncoderProperties {
        private int strength;
    }

    @Data
    public static class CorsProperties {
        private String[] allowedOrigins;
        private String allowedMethods;
        private String allowedHeaders;
        private String exposedHeaders;
    }

    @Data
    public static class RateLimitProperties {
        private boolean enabled;
        private int capacity;
        private int timeWindow;
    }

    @Data
    public static class AuditLogProperties {
        private boolean enabled;
        private int retentionDays;
    }

    @Data
    public static class BackupProperties {
        private boolean enabled;
        private String schedule;
        private EncryptionProperties encryption;

        @Data
        public static class EncryptionProperties {
            private String algorithm;
            private String key;
        }
    }
}
