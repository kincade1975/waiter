package com.teosoft.waiter.web.user.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import com.teosoft.waiter.jpa.domain.user.User;
import com.teosoft.waiter.jpa.domain.user.UserRepository;
import com.teosoft.waiter.web.user.resource.UserResource;

@Component
public class UserValidation {

	@Autowired
	UserRepository userRepository;

	public void validate(UserResource user) {
		User existingUser = userRepository.findByUsername(user.getUsername());
		if (existingUser != null && !existingUser.getId().equals(user.getId())) {
			throw new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Username already exists!");
		}
	}

}
