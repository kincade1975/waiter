package com.teosoft.waiter.web.session.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.MapSession;
import org.springframework.session.Session;
import org.springframework.stereotype.Service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.teosoft.waiter.web.session.resource.SessionResource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SessionService {

	private final static String DURATION_FORMAT = "HH:mm:ss.SSS";

	@Autowired
	private HazelcastInstance hazelcastInstance;

	@Autowired
	private FindByIndexNameSessionRepository<?> sessionRepository;

	/**
	 * Method returns session of the authenticated user (principal).
	 * @return
	 */
	public Session findSession() {
		Session session = null;

		try {
			for (Entry<String, ?> entry : sessionRepository.findByIndexNameAndIndexValue(FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME, SecurityContextHolder.getContext().getAuthentication().getName()).entrySet()) {
				if (entry.getValue() instanceof Session) {
					session = (Session) entry.getValue();
					break;
				}
			}
		} catch (Exception e) {
			log.error("Find session failed", e);
		}

		return session;
	}

	public List<SessionResource> findActiveSessions() {
		List<SessionResource> result = new ArrayList<>();

		IMap<String, MapSession> sessions = hazelcastInstance.getMap("spring:session:sessions");
		for (Entry<String, MapSession> entry : sessions.entrySet()) {
			MapSession mapSession = entry.getValue();

			SessionResource session = new SessionResource();
			session.setId(entry.getKey());
			session.setCreationDate(new Date(mapSession.getCreationTime()));
			session.setLastModifiedDate(new Date(mapSession.getLastAccessedTime()));
			session.setExpired(mapSession.isExpired());
			session.setMaxInactiveIntervalInSeconds(mapSession.getMaxInactiveIntervalInSeconds());
			session.setDuration(DurationFormatUtils.formatDuration(System.currentTimeMillis() - mapSession.getCreationTime(), DURATION_FORMAT));

			for (String attrName : mapSession.getAttributeNames()) {
				Object obj = entry.getValue().getAttribute(attrName);
				if (obj instanceof SecurityContextImpl) {
					SecurityContextImpl securityContext = (SecurityContextImpl) obj;
					if (securityContext.getAuthentication() != null) {
						session.setUsername(securityContext.getAuthentication().getName());
						result.add(session);
					}
				}
			}
		}

		return result;
	}

	public void deleteSession(String username) {
		for (SessionResource session : findActiveSessions()) {
			if (session.getUsername().equals(username)) {
				log.debug("Deleting session {}", session);
				sessionRepository.delete(session.getId());
				break;
			}
		}
	}

}
