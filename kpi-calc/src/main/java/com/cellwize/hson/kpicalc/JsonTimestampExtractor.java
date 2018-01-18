package com.cellwize.hson.kpicalc;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;

public class JsonTimestampExtractor implements TimestampExtractor {

    @Override
    public long extract(ConsumerRecord<Object, Object> record, long previousTimestamp) {
        if (record.value() instanceof JsonNode) {
            JsonNode node = ((JsonNode) record.value()).get("end");
            return node != null ? node.longValue() : System.currentTimeMillis();
        }

        throw new IllegalArgumentException("JsonTimestampExtractor cannot recognize the record value " + record.value());
    }
}
