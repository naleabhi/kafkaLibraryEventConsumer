package com.example.kafka.repository;

import com.example.kafka.domain.LibraryEvent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LibraryEventRepository extends CrudRepository<LibraryEvent, Integer> {
}
