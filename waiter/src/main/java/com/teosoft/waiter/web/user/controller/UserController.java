package com.teosoft.waiter.web.user.controller;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.teosoft.waiter.web.PageableResource;
import com.teosoft.waiter.web.UiGridResource;
import com.teosoft.waiter.web.user.resource.UserResource;
import com.teosoft.waiter.web.user.service.UserService;
import com.teosoft.waiter.web.user.validator.UserValidation;

@RestController
@RequestMapping(value = { "/api/v1/users" })
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private UserValidation userValidation;

	@RequestMapping(method = RequestMethod.GET)
	public List<UserResource> findAll() {
		return userService.findAll();
	}

	@RequestMapping(method = RequestMethod.GET, value="/{id}")
	public UserResource find(@PathVariable Integer id) {
		return userService.find(id);
	}

	@RequestMapping(method = RequestMethod.POST)
	public UserResource save(@RequestBody UserResource resource) {
		userValidation.validate(resource);
		return userService.save(resource);
	}

	@RequestMapping(method = RequestMethod.GET, value="/{id}/activate")
	public UserResource activate(@PathVariable Integer id) {
		return userService.activate(id);
	}

	@RequestMapping(method = RequestMethod.GET, value="/{id}/deactivate")
	public UserResource deactivate(@PathVariable Integer id) {
		return userService.deactivate(id);
	}

	@RequestMapping(method = RequestMethod.DELETE, value="/{id}")
	public void delete(Principal principal, @PathVariable Integer id, HttpServletRequest request) {
		userService.delete(id);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/page")
	public PageableResource<UserResource> page(@RequestBody UiGridResource resource) {
		return userService.page(resource);
	}

}
