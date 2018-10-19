package com.teosoft.waiter.socket.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.teosoft.waiter.socket.resource.AbstractMessage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SocketService {

	@Autowired
	private SimpMessagingTemplate template;

	public void sendToUser(AbstractMessage message, String username) {
		log.debug("Sending message [{}] to user [{}]", message, username);
		template.convertAndSendToUser(username, "/queue/send", message);
	}

}
