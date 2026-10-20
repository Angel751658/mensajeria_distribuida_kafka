package com.lta.backend.listeners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lta.backend.services.CatalogQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class QueryListener {

    private static final String QUERY_PREFIX = "QUERY|";

    private final CatalogQueryService catalogQueryService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public QueryListener(
            CatalogQueryService catalogQueryService,
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper
    ) {
        this.catalogQueryService = catalogQueryService;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
            groupId = "query-group",
            topics = "str-topic",
            containerFactory = "validMessageContainerFactory"
    )
    public void listen(String message) throws JsonProcessingException {
        if (!message.startsWith(QUERY_PREFIX)) {
            return;
        }

        String[] parts = message.split("\\|", 3);
        if (parts.length != 3) {
            log.warn("Solicitud de consulta Kafka inválida: {}", message);
            return;
        }

        try {
            String result = objectMapper.writeValueAsString(catalogQueryService.find(parts[2]));
            kafkaTemplate.send("str-topic-response", "QUERY_RESPONSE|" + parts[1] + "|" + result);
            log.info("Consulta {} procesada para {}", parts[1], parts[2]);
        } catch (IllegalArgumentException exception) {
            log.warn("Tipo de consulta Kafka inválido: {}", parts[2]);
        }
    }
}
