package com.luandeoliveira.orchestrator_service.configs;

import com.luandeoliveira.orchestrator_service.enums.Topics;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

    @Value("${spring.data.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${spring.data.consumer.group-id}")
    private String groupId;
    @Value("${spring.data.consumer.auto-offset-reset}")
    private String autoOffReset;

    private static final Integer PARTITIONS_QUANTITY = 1;
    private static final Integer REPLICAS_QUANTITY = 1;

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerProps());
    }

    private Map<String, Object> consumerProps() {
        var props = new HashMap<String, Object>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffReset);
        return props;
    }

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerProps());
    }

    private Map<String, Object> producerProps() {
        var props = new HashMap<String, Object>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return props;
    }

    @Bean
    KafkaTemplate<String, String> kafkaTemplate(ProducerFactory<String, String> producerFactory){
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    private NewTopic buildTopic(String name){
        return TopicBuilder
                .name(name)
                .replicas(REPLICAS_QUANTITY)
                .partitions(PARTITIONS_QUANTITY)
                .build();
    }

    @Bean
    private NewTopic startSaga(){
        return buildTopic(Topics.START_SAGA.getTopic());
    }
    @Bean
    private NewTopic notifyEnding(){
        return buildTopic(Topics.NOTIFY_ENDING.getTopic());
    }
    @Bean
    private NewTopic orchestrator(){
        return buildTopic(Topics.BASE_ORCHESTRATOR.getTopic());
    }
    @Bean
    private NewTopic finishSuccess(){
        return buildTopic(Topics.FINISH_SUCCESS.getTopic());
    }
    @Bean
    private NewTopic finishFail(){
        return buildTopic(Topics.FINISH_FAIL.getTopic());
    }
    @Bean
    private NewTopic productValidationSuccess(){
        return buildTopic(Topics.PRODUCT_VALIDATION_SUCCESS.getTopic());
    }
    @Bean
    private NewTopic productValidationFail(){
        return buildTopic(Topics.PRODUCT_VALIDATION_FAIL.getTopic());
    }
    @Bean
    private NewTopic paymentSuccess(){
        return buildTopic(Topics.PAYMENT_SUCCESS.getTopic());
    }
    @Bean
    private NewTopic paymentFail(){
        return buildTopic(Topics.PAYMENT_FAIL.getTopic());
    }
    @Bean
    private NewTopic inventorySuccess(){
        return buildTopic(Topics.INVENTORY_SUCCESS.getTopic());
    }
    @Bean
    private NewTopic inventoryFail(){
        return buildTopic(Topics.INVENTORY_FAIL.getTopic());
    }
}