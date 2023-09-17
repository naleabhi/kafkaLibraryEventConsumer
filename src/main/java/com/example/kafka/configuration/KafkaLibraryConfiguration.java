package com.example.kafka.configuration;

import com.fasterxml.jackson.databind.JsonSerializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.CooperativeStickyAssignor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.OutOfOrderSequenceException;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaLibraryConfiguration {

	@Autowired
	KafkaTemplate retryTemplate;

	@Value("${spring.retry.topic}")
	public String retryTopic;

	@Value("${spring.deadLetter.topic}")
	public String deadLetterTopic;

	Logger log= LoggerFactory.getLogger(KafkaLibraryConfiguration.class);
	@Bean
	public ConsumerFactory<String, String> consumerfactory()
	{
		Map<String, Object>prop= new HashMap<>();
		
		prop.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		prop.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
		prop.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		prop.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
		prop.put(ConsumerConfig.GROUP_ID_CONFIG, "group-id");
		prop.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
		prop.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, CooperativeStickyAssignor.class.getName());

		 return new DefaultKafkaConsumerFactory<>(prop, new StringDeserializer(), new StringDeserializer());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerLibraryFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerfactory());
        factory.setBatchListener(true);
		factory.setCommonErrorHandler(defaultErrorHandler());
        return factory;
    }
	/*
	* Error handler it will retry for the 2 times in interval of the 1-Second
	* @return DefaultErrorHandler
	* */
	@Bean
	public DefaultErrorHandler defaultErrorHandler()
	{
		FixedBackOff backOff= new FixedBackOff(1000l, 1);
		DefaultErrorHandler errorHandler= new DefaultErrorHandler( publishRecover(), backOff);
		errorHandler.setRetryListeners((consumerRecord, e, i) ->{
		log.info("The exception is occurred while retrying {} and retry attempt {}", e.getMessage(), i);
		} );
		return errorHandler;
	}

	public DeadLetterPublishingRecoverer publishRecover()
	{
		return new DeadLetterPublishingRecoverer(retryTemplate,
				(r, e) -> {
					if (e.getCause() instanceof OutOfOrderSequenceException) {
						log.info("Sending message into the Retry Topic");
						return new TopicPartition(retryTopic,  r.partition());
					}
					else {
						log.info("Sending message into the DeadLetter Topic");
						return new TopicPartition(deadLetterTopic , r.partition());
					}
				});
	}


}
