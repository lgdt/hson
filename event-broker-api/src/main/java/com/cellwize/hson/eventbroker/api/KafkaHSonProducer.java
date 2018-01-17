package com.cellwize.hson.eventbroker.api;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.stereotype.Component;

@Component
public class KafkaHSonProducer extends KafkaProducer<String, JsonNode> {

    public KafkaHSonProducer(KafkaProperties properties) {
        super(properties);
    }
}
