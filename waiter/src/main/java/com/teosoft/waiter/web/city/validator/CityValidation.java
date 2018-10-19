package com.teosoft.waiter.web.city.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import com.teosoft.waiter.jpa.domain.city.City;
import com.teosoft.waiter.jpa.domain.city.CityRepository;
import com.teosoft.waiter.web.city.resource.CityResource;

@Component
public class CityValidation {

	@Autowired
	CityRepository cityRepository;

	public void validate(CityResource user) {
		City existingCity = cityRepository.findByName(user.getName());
		if (existingCity != null && !existingCity.getId().equals(user.getId())) {
			throw new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Name already exists!");
		}
	}

}
