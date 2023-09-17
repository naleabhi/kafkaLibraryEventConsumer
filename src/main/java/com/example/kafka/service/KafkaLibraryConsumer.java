package com.example.kafka.service;

import com.example.kafka.domain.LibraryEvent;
import com.example.kafka.repository.LibraryEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.errors.OutOfOrderSequenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.lang.invoke.SwitchPoint;
import java.util.Optional;

@Service
public class KafkaLibraryConsumer {
	Logger logger=LoggerFactory.getLogger(KafkaLibraryConsumer.class.getName());
	@Autowired
	private LibraryEventRepository libraryEventRepository;

	@Autowired
	ObjectMapper mapper;

	@KafkaListener(topics = "library-events", containerFactory = "kafkaListenerLibraryFactory", groupId = "group-id")
	public void consumerLibraryEventMessage(String consumerEvent) throws IllegalArgumentException,OutOfOrderSequenceException,JsonProcessingException {
		LibraryEvent event= mapper.readValue(consumerEvent, LibraryEvent.class);
		logger.info("Message consumed from the :"+" library-events Topic: msg-> "+event);
		switch(event.getEventType())
		{
			case NEW: save(event);
			break;
			case UPDATE: validate(event); save(event);
			break;
			default: logger.info("No case matched");
		}


	}

	private void validate(LibraryEvent event) {
		if(event.getLibraryEventId()>999)
		{
			throw new OutOfOrderSequenceException("The given eventId is wrong");
		}
		Optional<LibraryEvent> libraryEventOptional=libraryEventRepository.findById(event.getLibraryEventId());
		if(!libraryEventOptional.isPresent())
		{
			throw new IllegalArgumentException("The value is not found for given eventId");
		}
		logger.info("The validation is successfully completed");
	}

	private void save(LibraryEvent event) {
		event.getBook().setEvent(event);

		libraryEventRepository.save(event);
		logger.info("Message saved-> "+event);
	}
}
