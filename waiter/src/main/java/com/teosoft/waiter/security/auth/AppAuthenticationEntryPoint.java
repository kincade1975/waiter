package com.teosoft.waiter.security.auth;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.stereotype.Component;

@Component
public class AppAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	private final static String REDIRECT_HEADER = "X-REDIRECT";
	private final static String REDIRECT_URL = "/login.html";

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
		String ajaxHeader = request.getHeader("X-Requested-With");
		if (ajaxHeader != null && ajaxHeader.equalsIgnoreCase("XMLHttpRequest")) {
			response.setHeader(REDIRECT_HEADER, REDIRECT_URL);
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "Ajax Request Denied (Session Expired)");
		} else {
			redirectStrategy.sendRedirect(request, response, REDIRECT_URL);
		}
	}

}