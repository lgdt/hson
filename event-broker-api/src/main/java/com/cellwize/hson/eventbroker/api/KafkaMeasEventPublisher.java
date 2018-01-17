package com.cellwize.hson.eventbroker.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class KafkaMeasEventPublisher implements EventPublisher<MeasResults> {
    private static final String TOPIC_NAME = "counters";
    @Autowired
    private KafkaHSonProducer kafkaHSonProducer;

    @Override
    public void publishEvent(MeasResults measResults) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(measResults);
        ProducerRecord<String, JsonNode> rec = new ProducerRecord<>(TOPIC_NAME, jsonNode);
        kafkaHSonProducer.send(rec);
        kafkaHSonProducer.close();
    }
}
