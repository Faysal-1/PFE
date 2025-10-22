package com.maroctbib;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class TestController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }
    
    @GetMapping("/test")
    public String test() {
        return "Backend MarocTbib fonctionne !";
    }
    
    @GetMapping("/db-test")
    public String testDatabase() {
        try {
            Connection connection = dataSource.getConnection();
            String dbUrl = connection.getMetaData().getURL();
            connection.close();
            return "✅ Base de données connectée: " + dbUrl;
        } catch (Exception e) {
            return "❌ Erreur de connexion à la base: " + e.getMessage();
        }
    }
    
    @GetMapping("/doctors")
    public ResponseEntity<List<Map<String, Object>>> getDoctors() {
        // Données directes pour les 4 docteurs
        List<Map<String, Object>> doctors = Arrays.asList(
            Map.of(
                "id", "d1",
                "fullName", "Dr. Sara El Amrani",
                "name", "Dr. Sara El Amrani",
                "specialty", "Médecin généraliste",
                "city", "Casablanca",
                "address", "123 Boulevard Mohammed V",
                "phone", "+212 522 123 456",
                "verified", true,
                "availableSlots", Arrays.asList("09:00", "09:30", "10:15", "11:00")
            ),
            Map.of(
                "id", "d2",
                "fullName", "Dr. Yassine Benali",
                "name", "Dr. Yassine Benali",
                "specialty", "Cardiologue",
                "city", "Rabat",
                "address", "456 Avenue Hassan II",
                "phone", "+212 537 789 012",
                "verified", true,
                "availableSlots", Arrays.asList("14:00", "14:30", "15:00")
            ),
            Map.of(
                "id", "d3",
                "fullName", "Dr. Salma Kabbaj",
                "name", "Dr. Salma Kabbaj",
                "specialty", "Dermatologue",
                "city", "Marrakech",
                "address", "789 Rue de la Liberté",
                "phone", "+212 524 345 678",
                "verified", true,
                "availableSlots", Arrays.asList("10:00", "10:30", "16:00")
            ),
            Map.of(
                "id", "d4",
                "fullName", "Dr. Rajaa Idrissi",
                "name", "Dr. Rajaa Idrissi",
                "specialty", "Pédiatre",
                "city", "Fès",
                "address", "321 Avenue des Nations Unies",
                "phone", "+212 535 901 234",
                "verified", true,
                "availableSlots", Arrays.asList("08:00", "09:00", "16:00", "17:00")
            )
        );
        
        return ResponseEntity.ok(doctors);
    }
}
