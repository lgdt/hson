package com.cellwize.hson.kpicalc;

import com.cellwize.hson.util.JsonPOJODeserializer;
import com.cellwize.hson.util.JsonPOJOSerializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.connect.json.JsonDeserializer;
import org.apache.kafka.connect.json.JsonSerializer;
import org.apache.kafka.streams.Consumed;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.processor.AbstractProcessor;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class MlbKpiTriggeringProcessor {

    public static void main(String[] args) {
        Thread.currentThread().setContextClassLoader(null);

        final Serializer<JsonNode> jsonSerializer = new JsonSerializer();
        final Deserializer<JsonNode> jsonDeserializer = new JsonDeserializer();

        final Serde<JsonNode> jsonSerde = Serdes.serdeFrom(jsonSerializer, jsonDeserializer);

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "hson-streams-kpi");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.132.163:9092");

        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, JsonTimestampExtractor.class);

        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());


        StreamsBuilder builder = new StreamsBuilder();

        Map<String, Object> serdeProps = new HashMap<>();

        final Serializer<CellKpi> pageViewSerializer = new JsonPOJOSerializer<>();
        serdeProps.put("JsonPOJOClass", CellKpi.class);
        pageViewSerializer.configure(serdeProps, false);

        final Deserializer<CellKpi> pageViewDeserializer = new JsonPOJODeserializer<>();
        serdeProps.put("JsonPOJOClass", CellKpi.class);
        pageViewDeserializer.configure(serdeProps, false);

        final Serde<CellKpi> cellKpiSerde = Serdes.serdeFrom(pageViewSerializer, pageViewDeserializer);

        final Consumed<String, JsonNode> consumed = Consumed.with(Serdes.String(), jsonSerde);
        KStream<String, JsonNode> source = builder.stream("counters", consumed);
        source
                .selectKey((key, value) -> value.get("measObjLdn").textValue())
                .groupByKey(Serialized.with(Serdes.String(), jsonSerde))
                .aggregate(
                        new Initializer<CellKpi>() {
                            @Override
                            public CellKpi apply() {
                                return new CellKpi();
                            }
                        },
                        new Aggregator<String, JsonNode, CellKpi>() {
                            @Override
                            public CellKpi apply(String key, JsonNode value, CellKpi aggregate) {
                                aggregate.setCellId(key);

                                String counterName = value.get("counterName").textValue();
                                Float meas = Float.valueOf(value.get("meas").textValue());

                                KpiVal val = aggregate.getKpiVal();
                                if (val == null) {
                                    val = new KpiVal(KpiType.KPI_DL_CODE_R99_UTIL);
                                }
                                val.setCounterValue(counterName, meas);

                                aggregate.setKpiVal(val);

                                return aggregate;
                            }
                        }, Materialized.with(Serdes.String(), cellKpiSerde))
                .filter((key, value) -> value.getKpiVal().isValid())
                .toStream().to("hson-streams-output-kpi", Produced.with(Serdes.String(), cellKpiSerde));

        final KafkaStreams streams = new KafkaStreams(builder.build(), props);
        final CountDownLatch latch = new CountDownLatch(1);

        // attach shutdown handler to catch control-c
        Runtime.getRuntime().addShutdownHook(new Thread("hson-streams-kpi-shutdown-hook") {
            @Override
            public void run() {
                streams.close();
                latch.countDown();
            }
        });

        try {
            streams.start();
            latch.await();
        } catch (Throwable e) {
            System.exit(1);
        }
        System.exit(0);
    }
}
