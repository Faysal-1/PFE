package org.baeldung.spring;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {
    
    private static final Logger logger = LoggerFactory.getLogger(StaticResourceConfig.class);

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        try {
            // Configuration pour servir les fichiers depuis le dossier /uploads/
            Path uploadDir = Paths.get("uploads").toAbsolutePath().normalize();
            String uploadPath = uploadDir.toString();
            
            // Assurez-vous que le chemin se termine par un séparateur
            if (!uploadPath.endsWith(System.getProperty("file.separator"))) {
                uploadPath = uploadPath + System.getProperty("file.separator");
            }
            
            // Convertir les backslashes en forward slashes pour les URLs
            String uploadPathForUrl = uploadPath.replace("\\", "/");
            
            logger.info("Configuration des ressources statiques - Chemin d'upload: " + uploadPathForUrl);
            
            registry.addResourceHandler("/uploads/**")
                    .addResourceLocations("file:/" + uploadPathForUrl)
                    .setCachePeriod(0) // Pas de cache pendant le développement
                    .resourceChain(true);
            
            logger.info("Handler de ressources configuré pour: /uploads/** -> file:/" + uploadPathForUrl);
                    
            // Assurez-vous que les autres ressources statiques sont toujours servies
            registry.addResourceHandler("/assets/**")
                    .addResourceLocations("classpath:/static/assets/");
                    
            registry.addResourceHandler("/SuperAdmin/**")
                    .addResourceLocations("classpath:/static/SuperAdmin/");
                    
            logger.info("Configuration des ressources statiques terminée avec succès");
        } catch (Exception e) {
            logger.error("Erreur lors de la configuration des ressources statiques", e);
        }
    }
}
