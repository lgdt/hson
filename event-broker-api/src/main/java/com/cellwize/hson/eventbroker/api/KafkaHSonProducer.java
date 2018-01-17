package com.cellwize.hson.eventbroker.api;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.stereotype.Component;

@Component
public class KafkaHSonProducer extends KafkaProducer {

    public KafkaHSonProducer(KafkaProperties properties) {
        super(properties);
    }
}
