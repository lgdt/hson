package com.cellwize.hson.eventbroker.api;

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
}
