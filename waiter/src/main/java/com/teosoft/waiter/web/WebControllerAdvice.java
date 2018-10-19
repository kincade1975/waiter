package com.teosoft.waiter.web;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice(basePackages = {
		"com.teosoft.waiter.web.user.controller",
		"com.teosoft.waiter.web.city.controller"
})
public class WebControllerAdvice extends ResponseEntityExceptionHandler {

	@ExceptionHandler({ HttpClientErrorException.class })
	public ResponseEntity<Object> handleAccessDeniedException(HttpClientErrorException exception, WebRequest request) {
		return new ResponseEntity<Object>(new WebExceptionResource(exception.getStatusText()), new HttpHeaders(), exception.getStatusCode());
	}

}
