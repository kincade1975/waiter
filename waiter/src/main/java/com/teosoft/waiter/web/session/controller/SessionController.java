package com.teosoft.waiter.web.session.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.teosoft.waiter.web.session.resource.SessionResource;
import com.teosoft.waiter.web.session.service.SessionService;

@RestController
@RequestMapping(value = { "/api/v1/sessions" })
public class SessionController {

	@Autowired
	private SessionService sessionService;

	@RequestMapping(method = RequestMethod.GET)
	public List<SessionResource> findActiveSessions() {
		return sessionService.findActiveSessions();
	}

}
