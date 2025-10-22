package com.maroctbib.bootstrap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.io.InputStream;

@Slf4j
@Component
public class SeedDataRunner implements ApplicationRunner {

    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public SeedDataRunner() {
    }

    @Value("${app.seed.enabled:false}")
    private boolean seedEnabled;

    @Value("${app.seed.resource:classpath:data/seed.json}")
    private Resource seedResource;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!seedEnabled) {
            log.info("Seed is disabled. Skipping.");
            return;
        }
        if (seedResource == null || !seedResource.exists()) {
            log.warn("Seed resource not found: {}", seedResource);
            return;
        }
        try (InputStream in = seedResource.getInputStream()) {
            JsonNode root = objectMapper.readTree(in);
            JsonNode seed = root.path("seed");

            // Specialties
            if (seed.has("specialties")) {
                for (JsonNode s : seed.get("specialties")) {
                    log.info("[SEED] Ensure specialty exists: {}", s.asText());
                    // TODO: insert into DB if missing
                }
            }

            // Users
            if (seed.has("users")) {
                for (JsonNode u : seed.get("users")) {
                    String email = u.path("email").asText();
                    String role = u.path("role").asText();
                    log.info("[SEED] Ensure user exists: email={}, role={} details={}", email, role, u);
                    // TODO: create user with role and optional specialty/city
                }
            }

            // Availability
            if (seed.has("availability")) {
                for (JsonNode a : seed.get("availability")) {
                    log.info("[SEED] Ensure availability slot: {}", a);
                    // TODO: create availability slots for doctor if free
                }
            }

            log.info("Seed processing completed.");
        } catch (Exception e) {
            log.error("Failed to process seed data", e);
        }
    }
}
