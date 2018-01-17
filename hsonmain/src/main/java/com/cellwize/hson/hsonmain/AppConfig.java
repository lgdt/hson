package com.cellwize.hson.hsonmain;

import com.cellwize.hson.eventbroker.api.KafkaHSonProducer;
import com.cellwize.hson.eventbroker.api.KafkaMeasEventPublisher;
import com.cellwize.hson.eventbroker.api.KafkaProperties;
import com.cellwize.hson.filewatcher.PathWatcher;
import com.cellwize.hson.parsers.nokiaxml.NokiaPMXmlParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean(name="kafkaMeasEventPublisher")
    public KafkaMeasEventPublisher getKafkaMeasEventPublisher(){
        return new KafkaMeasEventPublisher();
    }

    @Bean(name="kafkaHSonProducer")
    public KafkaHSonProducer getKafkaHSonProducer(){
        return new KafkaHSonProducer(new KafkaProperties());
    }

    @Bean(name = "pathWatcher")
    public PathWatcher getPathWatcher() {
        return new PathWatcher();
    }
}
