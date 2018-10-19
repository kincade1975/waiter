package com.teosoft.waiter.socket.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.teosoft.waiter.socket.resource.AbstractMessage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class SocketController {

	@MessageMapping("/receive")
	public void receive(@Payload AbstractMessage message, StompHeaderAccessor headerAccessor) throws Exception {
		String username = headerAccessor.getUser().getName();
		String sessionId = headerAccessor.getSessionAttributes().get("SPRING.SESSION.ID").toString();
		log.debug("Message received from user [{}] - session [{}]", username, sessionId);
	}

}
