package com.example.kafka.domain;

import lombok.ToString;

import javax.persistence.*;

@Entity
public class LibraryEvent {
	@Id
	@GeneratedValue
	private Integer libraryEventId;
	@Enumerated(EnumType.STRING)
	private EventType eventType;

	@OneToOne(mappedBy = "event",cascade =CascadeType.ALL )
	@ToString.Exclude
	private Book book;

	public LibraryEvent() {
		super();
		// TODO Auto-generated constructor stub
	}

	public LibraryEvent(Integer libraryEventId, Book book) {
		super();
		this.libraryEventId = libraryEventId;
		this.book = book;
	}


	public Integer getLibraryEventId() {
		return libraryEventId;
	}

	public void setLibraryEventId(Integer libraryEventId) {
		this.libraryEventId = libraryEventId;
	}

	public Book getBook() {
		return book;
	}

	public void setBook(Book book) {
		this.book = book;
	}

	public EventType getEventType() {
		return eventType;
	}

	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}

	@Override
	public String toString() {
		return "LibraryEvent{" +
				"libraryEventId=" + libraryEventId +
				", eventType=" + eventType +
				", book=" + book +
				'}';
	}
}
