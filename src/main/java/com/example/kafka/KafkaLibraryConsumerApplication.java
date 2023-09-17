package com.example.kafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@SpringBootApplication
@Component
public class KafkaLibraryConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(KafkaLibraryConsumerApplication.class, args);
	}

}
