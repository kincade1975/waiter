package com.teosoft.waiter.security;

import org.apache.catalina.util.SessionConfig;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

import com.hazelcast.config.SecurityConfig;

/**
 * Required for the HttpSession with Hazelcast.
 * For more info see https://docs.spring.io/spring-session/docs/current/reference/html5/#httpsession-hazelcast.
 */
public class SecurityInitializer extends AbstractSecurityWebApplicationInitializer {

	public SecurityInitializer() {
		super(SecurityConfig.class, SessionConfig.class);
	}

}
