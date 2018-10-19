package com.teosoft.waiter.web.user.controller.ext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.teosoft.waiter.web.user.service.ext.UserServiceExt;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = { "/e/api/v1/users" })
public class UserControllerExt {

	@Autowired
	private UserServiceExt userServiceExt;

	@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.OPTIONS } )
	@RequestMapping(method = RequestMethod.GET, value="/{code}")
	public ResponseEntity<?> find(@PathVariable String code) {
		long start = System.currentTimeMillis();
		try {
			log.debug("Findind data for table code [{}]", code);
			return userServiceExt.find(code);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			log.debug("Findind data for table code [{}] finished in {} ms.", code, System.currentTimeMillis() - start);
		}
	}

}
