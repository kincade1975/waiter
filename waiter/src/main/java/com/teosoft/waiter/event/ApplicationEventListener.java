package com.teosoft.waiter.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.teosoft.waiter.jpa.domain.event.ApplicationEvent;
import com.teosoft.waiter.jpa.domain.event.ApplicationEventRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ApplicationEventListener {

	@Autowired
	private ApplicationEventRepository applicationEventRepository;

	@Async
	@EventListener
	public void processEvent(ApplicationEvent event) {
		try {
			log.debug("Processing event {}", event);

			// save event to database
			applicationEventRepository.save(event);
		} catch (Exception e) {
			log.error("Processing event failed", e);
		}
	}

}
