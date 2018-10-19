package com.teosoft.waiter.security.auth;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class AppAuthenticationResultHandler implements AuthenticationSuccessHandler, AuthenticationFailureHandler {

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		setResponse(Boolean.TRUE, response, null);
	}

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
		setResponse(Boolean.FALSE, response, exception);
	}

	private void setResponse(Boolean authenticationSuccess, HttpServletResponse response, AuthenticationException exception) throws JsonGenerationException, JsonMappingException, IOException {
		Map<String, Object> data = new HashMap<>();
		data.put("result", authenticationSuccess);
		if (exception != null) {
			data.put("message", exception.getMessage());
		}
		objectMapper.writeValue(response.getOutputStream(), data);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
	}

}