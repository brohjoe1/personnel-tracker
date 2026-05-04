package com.cgi.tracker.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AssetEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(AssetEventConsumer.class);
    private final ObjectMapper objectMapper;

    public AssetEventConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${app.kafka.topic}",
                   groupId = "${spring.kafka.consumer.group-id}")
    public void consume(String message) {
        try {
            AssetEvent event = objectMapper.readValue(message, AssetEvent.class);
            log.info("KAFKA EVENT RECEIVED >> action={} asset='{}' serial={} personnel='{}' at {}",
                    event.getAction(),
                    event.getAssetName(),
                    event.getSerialNumber(),
                    event.getPersonnelName(),
                    event.getTimestamp());
        } catch (Exception e) {
            log.error("Failed to deserialize asset event: {}", message, e);
        }
    }
}
