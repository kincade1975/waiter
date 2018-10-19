package com.teosoft.waiter.web.city.controller;

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
import com.teosoft.waiter.web.city.resource.CityResource;
import com.teosoft.waiter.web.city.service.CityService;
import com.teosoft.waiter.web.city.validator.CityValidation;

@RestController
@RequestMapping(value = { "/api/v1/cities" })
public class CityController {

	@Autowired
	private CityService cityService;

	@Autowired
	private CityValidation cityValidation;

	@RequestMapping(method = RequestMethod.GET)
	public List<CityResource> findAll() {
		return cityService.findAll();
	}

	@RequestMapping(method = RequestMethod.GET, value="/{id}")
	public CityResource find(@PathVariable Integer id) {
		return cityService.find(id);
	}

	@RequestMapping(method = RequestMethod.POST)
	public CityResource save(@RequestBody CityResource resource) {
		cityValidation.validate(resource);
		return cityService.save(resource);
	}

	@RequestMapping(method = RequestMethod.DELETE, value="/{id}")
	public void delete(Principal principal, @PathVariable Integer id, HttpServletRequest request) {
		cityService.delete(id);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/page")
	public PageableResource<CityResource> page(@RequestBody UiGridResource resource) {
		return cityService.page(resource);
	}

}
