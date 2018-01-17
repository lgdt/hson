package com.cellwize.hson.eventbroker.api;

import org.apache.kafka.clients.producer.ProducerConfig;

import java.util.Properties;

public class KafkaProperties extends Properties {

    public KafkaProperties() {
        super();
        put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"192.168.132.163:9092");
        put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.ByteArraySerializer");
        put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.connect.json.JsonSerializer");

    }
}
