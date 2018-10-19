package com.teosoft.waiter.hazelcast;

import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.MapSession;
import org.springframework.session.hazelcast.HazelcastSessionRepository;
import org.springframework.session.hazelcast.PrincipalNameExtractor;
import org.springframework.session.hazelcast.config.annotation.web.http.EnableHazelcastHttpSession;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapAttributeConfig;
import com.hazelcast.config.MapIndexConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.spring.cache.HazelcastCacheManager;

@EnableHazelcastHttpSession
@Configuration
public class HazelcastHttpSessionConfig {

	@Bean
	public HazelcastInstance hazelcastInstance() {
		MapAttributeConfig attributeConfig = new MapAttributeConfig()
				.setName(HazelcastSessionRepository.PRINCIPAL_NAME_ATTRIBUTE)
				.setExtractor(PrincipalNameExtractor.class.getName());

		Config config = new Config();

		config.getMapConfig("spring:session:sessions")
		.addMapAttributeConfig(attributeConfig)
		.addMapIndexConfig(new MapIndexConfig(HazelcastSessionRepository.PRINCIPAL_NAME_ATTRIBUTE, false));

		return Hazelcast.newHazelcastInstance(config);
	}

	@Bean
	public CacheManager cacheManager() {
		return new HazelcastCacheManager(hazelcastInstance());
	}

	/**
	 * Required for the Spring Security Concurrent Session Control (see SecurityConfiguration class).
	 * @return
	 */
	@Bean
	public FindByIndexNameSessionRepository<?> sessionRepository() {
		IMap<String, MapSession> sessions = hazelcastInstance().getMap("spring:session:sessions");
		HazelcastSessionRepository repository = new HazelcastSessionRepository(sessions);
		repository.setDefaultMaxInactiveInterval(60); // server session timeout is set to 60 seconds
		return repository;
	}

	/**
	 * Required for the Spring Security Session Management (see SecurityConfiguration class).
	 * This is essential to make sure that the Spring Security session registry is notified when the session is destroyed.
	 * @return
	 */
	@Bean
	public HttpSessionEventPublisher httpSessionEventPublisher() {
		return new HttpSessionEventPublisher();
	}

}
