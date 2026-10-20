package com.lta.backend.services;


import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ResponseListener {

    private static final String QUERY_RESPONSE_PREFIX = "QUERY_RESPONSE|";

    private final StringProducerService producerService;

    public ResponseListener(StringProducerService producerService) {
        this.producerService = producerService;
    }

    public static String response = null;

    @KafkaListener(topics = "str-topic-response", groupId = "group-1")
    public void listen(String message){
        if (message.startsWith(QUERY_RESPONSE_PREFIX)) {
            String[] parts = message.split("\\|", 3);
            if (parts.length == 3) {
                producerService.completeQuery(parts[1], parts[2]);
            }
            return;
        }

        response = message;
    }
}