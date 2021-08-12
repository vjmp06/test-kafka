package com.example.testkafka;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.concurrent.TimeUnit;

import com.example.testkafka.consumer.KafkaConsumer;
import com.example.testkafka.producer.KafkaProducer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;

@EnableKafka
@SpringBootTest // Specify @KafkaListener class if its not the same class, or not loaded with test config
@EmbeddedKafka(
    partitions = 1, 
    controlledShutdown = false)
class EmbeddedKafkaIntegrationTest {

    @Autowired
    public KafkaTemplate<String, String> template;

    @Autowired
    private KafkaConsumer consumer;

    @Autowired
    private KafkaProducer producer;

    @Value("${test.topic}")
    private String topic;

    @Test
    public void givenEmbeddedKafkaBroker_whenSendingtoDefaultTemplate_thenMessageReceived() throws Exception {
        template.send(topic, "Sending with default template");
        consumer.getLatch().await(10000, TimeUnit.MILLISECONDS);
        assertThat(consumer.getLatch().getCount(), equalTo(0L));
        
        assertThat(consumer.getPayload(), containsString("embedded-test-topic"));
    }

    @Test
    public void givenEmbeddedKafkaBroker_whenSendingtoSimpleProducer_thenMessageReceived() throws Exception {
        producer.send(topic, "Sending with our own simple KafkaProducer");
        consumer.getLatch().await(10000, TimeUnit.MILLISECONDS);
        
        assertThat(consumer.getLatch().getCount(), equalTo(0L));
        assertThat(consumer.getPayload(), containsString("embedded-test-topic"));
    }

}
