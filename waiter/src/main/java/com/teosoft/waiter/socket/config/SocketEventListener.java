package com.teosoft.waiter.socket.config;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.map.PassiveExpiringMap;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SocketEventListener {

	private static PassiveExpiringMap<String, String> sessions;

	static {
		sessions = new PassiveExpiringMap<>(30, TimeUnit.MINUTES);
	}

	public Boolean isSocketOpened(String username) {
		return sessions.containsKey(username);
	}

	@EventListener
	public void handleWebSocketConnectListener(SessionConnectedEvent event) {
		String username = event.getUser().getName();
		String sessionId = getSessionId(StompHeaderAccessor.wrap(event.getMessage()));

		sessions.put(username, sessionId);

		log.info("Web socket connected for user [{}] and session [{}]", username, sessionId);
	}

	@EventListener
	public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
		String username = event.getUser().getName();
		String sessionId = getSessionId(StompHeaderAccessor.wrap(event.getMessage()));

		sessions.remove(username);

		log.info("Web socket disconnected for user [{}] and session [{}]", username, sessionId);
	}

	@SuppressWarnings("unchecked")
	private String getSessionId(StompHeaderAccessor headerAccessor) {
		String sessionId = null;

		try {
			for (Entry<String, Object> entry : headerAccessor.getMessageHeaders().entrySet()) {
				if (entry.getKey().equals(SimpMessageHeaderAccessor.CONNECT_MESSAGE_HEADER)) {
					GenericMessage<?> message = (GenericMessage<?>) entry.getValue();
					Map<String, Object> simpSessionAttributes = (Map<String, Object>) message.getHeaders().get("simpSessionAttributes");
					sessionId = simpSessionAttributes.get("SPRING.SESSION.ID").toString();
				} else if (entry.getKey().equals(SimpMessageHeaderAccessor.SESSION_ATTRIBUTES)) {
					Map<String, Object> simpSessionAttributes = (Map<String, Object>) entry.getValue();
					sessionId = simpSessionAttributes.get("SPRING.SESSION.ID").toString();
				}
			}
		} catch (Exception e) {
			log.error("Getting session ID failed", e);
		}

		return sessionId;
	}

}
