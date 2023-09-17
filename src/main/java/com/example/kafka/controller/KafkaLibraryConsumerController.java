package com.example.kafka.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("kafka")
@Slf4j
public class KafkaLibraryConsumerController {
	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public String test()
	{
		return "Tested Ok!";
	}

}
