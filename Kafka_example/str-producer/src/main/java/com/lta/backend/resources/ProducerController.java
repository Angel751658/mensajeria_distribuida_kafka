package com.lta.backend.resources;

import com.lta.backend.services.ResponseListener;
import com.lta.backend.services.StringProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/producer")
public class ProducerController {

    private static final Set<String> QUERY_TYPES = Set.of("clients", "products");

    @Autowired
    private StringProducerService producer;

    @GetMapping("/menu")
    public String sendMenu(@RequestParam String message) throws InterruptedException {
        return sendAndWait(message);
    }

    @GetMapping("/test")
    public String sendTest(@RequestParam String message) throws InterruptedException {
        return sendAndWait(message);
    }

    @GetMapping("/query")
    public ResponseEntity<String> query(@RequestParam String type)
            throws InterruptedException {
        if (!QUERY_TYPES.contains(type)) {
            return ResponseEntity.badRequest().body("El tipo debe ser clients o products.");
        }

        CompletableFuture<String> queryResponse = producer.query(type);
        try {
            String result = queryResponse.get(10, TimeUnit.SECONDS);
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(result);
        } catch (TimeoutException exception) {
            queryResponse.cancel(false);
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                    .body("La consulta no recibió respuesta de Kafka a tiempo.");
        } catch (ExecutionException exception) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body("No fue posible procesar la consulta en Kafka.");
        } catch (InterruptedException exception) {
            queryResponse.cancel(false);
            throw exception;
        }
    }

    // MÉTODO REUTILIZABLE
    private String sendAndWait(String message) throws InterruptedException {

        ResponseListener.response = null;

        producer.sendMessage(message);

        int attempts = 0;

        while(ResponseListener.response == null && attempts < 10){
            Thread.sleep(500);
            attempts++;
        }

        return ResponseListener.response != null
                ? ResponseListener.response
                : "No response (posiblemente fue a partición 0)";
    }
}