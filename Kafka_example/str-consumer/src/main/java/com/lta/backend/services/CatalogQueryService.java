package com.lta.backend.services;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CatalogQueryService {

    public List<Map<String, String>> find(String type) {
        return switch (type) {
            case "clients" -> List.of(
                    Map.of("id", "1", "name", "Ana García", "email", "ana@example.com"),
                    Map.of("id", "2", "name", "Luis Pérez", "email", "luis@example.com")
            );
            case "products" -> List.of(
                    Map.of("id", "1", "name", "Teclado", "price", "29.99"),
                    Map.of("id", "2", "name", "Mouse", "price", "19.99"),
                    Map.of("id", "3", "name", "Monitor", "price", "159.99")
            );
            default -> throw new IllegalArgumentException("Tipo de consulta no válido: " + type);
        };
    }
}
