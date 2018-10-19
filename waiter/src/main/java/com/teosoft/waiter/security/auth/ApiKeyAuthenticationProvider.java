package com.teosoft.waiter.security.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.teosoft.waiter.Properties;

@Component
public class ApiKeyAuthenticationProvider implements AuthenticationProvider {

	@Autowired
	private Properties properties;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		if (!properties.getApiKey().equals(authentication.getCredentials())) {
			throw new BadCredentialsException("Bad credenentials");
		}

		UserDetails principal = new ApiKeyPrincipal("API", properties.getApiKey());

		return new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}

}
