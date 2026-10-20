package com.lta.backend.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CatalogQueryServiceTest {

    private final CatalogQueryService service = new CatalogQueryService();

    @Test
    void returnsClients() {
        assertEquals(2, service.find("clients").size());
    }

    @Test
    void returnsProducts() {
        assertEquals(3, service.find("products").size());
    }

    @Test
    void rejectsUnknownQueryType() {
        assertThrows(IllegalArgumentException.class, () -> service.find("unknown"));
    }
}
