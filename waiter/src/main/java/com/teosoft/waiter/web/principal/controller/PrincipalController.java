package com.teosoft.waiter.web.principal.controller;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.teosoft.waiter.Properties;
import com.teosoft.waiter.jpa.domain.user.User;
import com.teosoft.waiter.jpa.domain.user.User.UserRole;
import com.teosoft.waiter.jpa.domain.user.UserRepository;
import com.teosoft.waiter.security.auth.AppUserDetails;
import com.teosoft.waiter.web.principal.resource.PrincipalResource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class PrincipalController {

	@Autowired
	private Properties properties;

	@Autowired
	private UserRepository userRepository;

	@GetMapping(value = "/principal")
	public PrincipalResource getPrincipal() {
		PrincipalResource resource = null;

		if (SecurityContextHolder.getContext().getAuthentication() != null) {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

			if (authentication.getPrincipal() instanceof AppUserDetails) {
				AppUserDetails principal = (AppUserDetails) authentication.getPrincipal();

				User user = userRepository.findByUsername(principal.getUsername());

				resource = new PrincipalResource();
				resource.setUsername(user.getUsername());
				resource.setSuperadmin((user.getRole() == UserRole.SUPERADMIN) ? Boolean.TRUE : Boolean.FALSE);
				resource.setAdmin((user.getRole() == UserRole.ADMIN) ? Boolean.TRUE : Boolean.FALSE);

				// server-side properties
				resource.getProperties().put("stompEndpoint", properties.getStompEndpoint());

				if (user.getRole() == UserRole.SUPERADMIN || user.getRole() == UserRole.ADMIN) {
					resource.setPermissions(Arrays.asList(
							"dashboard",
							"users.read", "users.create", "users.update", "users.delete",
							"sessions",
							"settings"));
				} else if (user.getRole() == UserRole.CUSTOMER) {
					resource.setPermissions(Arrays.asList(
							"orders"));
				}
			}
		}

		return resource;
	}

	@GetMapping(value = "/ping")
	public String ping() {
		if (SecurityContextHolder.getContext().getAuthentication() != null) {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			log.trace("Ping from [{}]", authentication.getName());
		}
		return HttpStatus.OK.toString();
	}

}
