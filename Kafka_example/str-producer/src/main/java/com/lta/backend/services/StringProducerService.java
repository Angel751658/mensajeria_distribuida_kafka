package com.lta.backend.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class StringProducerService {

    private final ConcurrentHashMap<String, CompletableFuture<String>> queryResponses = new ConcurrentHashMap<>();

    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;

    public void sendMessage(String message){

        int partition;

        if(message.contains("menu")){
            partition = 1; // listener2 (procesa y responde)
        } else {
            partition = 0; // listener1 (no responde)
        }


        //kafkaTemplate.send("str-topic", partition, null, message)
        kafkaTemplate.send("str-topic",partition,null, message).whenComplete((result,ex) -> {
            if(ex != null){
                log.error("Error, al enviar el mensaje: {}",ex.getMessage());
            }
            log.info("Mensaje enviado con exito: {}",result.getProducerRecord().value());
            log.info("Particion {}, Offset {}", result.getRecordMetadata().partition(),result.getRecordMetadata().offset());
        });
    }
}

public CompletableFuture<String> query(String type) {
    String requestId = UUID.randomUUID().toString();
    CompletableFuture<String> response = new CompletableFuture<>();
    queryResponses.put(requestId, response);
    response.whenComplete((result, error) -> queryResponses.remove(requestId));

    String message = "QUERY|" + requestId + "|" + type;
    kafkaTemplate.send("str-topic", 0, null, message).whenComplete((result, error) -> {
        if (error != null) {
            response.completeExceptionally(error);
            log.error("Error al enviar la consulta a Kafka", error);
        } else {
            log.info("Consulta enviada a Kafka: {}", requestId);
        }
    });

    return response;
}

public void completeQuery(String requestId, String result) {
    CompletableFuture<String> response = queryResponses.get(requestId);
    if (response != null) {
        response.complete(result);
    }
}














/*
String topic;
        if(message.contains("te")){
            topic = "topic-2";
        }else{
            topic = "str-topic";
        }
*/