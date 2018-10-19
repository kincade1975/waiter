package com.teosoft.waiter.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.session.ExpiringSession;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;
import org.springframework.web.cors.CorsUtils;

import com.teosoft.waiter.security.auth.ApiKeyAuthenticationProvider;
import com.teosoft.waiter.security.auth.AppAuthenticationEntryPoint;
import com.teosoft.waiter.security.auth.AppAuthenticationResultHandler;
import com.teosoft.waiter.security.auth.AppUserDetailsService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfiguration {

	@Configuration
	@Order(1)
	public static class ApiConfigurationAdapter extends WebSecurityConfigurerAdapter {

		@Autowired
		private ApiKeyAuthenticationProvider apiKeyAuthenticationProvider;

		@Override
		protected void configure(AuthenticationManagerBuilder auth) throws Exception {
			auth.authenticationProvider(apiKeyAuthenticationProvider);
		}

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			log.info("Configuring iAPI security...");
			http.csrf().disable();
			http.antMatcher("/e/api/**").authorizeRequests().requestMatchers(CorsUtils::isPreFlightRequest).permitAll().anyRequest().authenticated();
			http.httpBasic();
			http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		}

	}

	@Configuration
	@Order(2)
	public static class WebConfigurationAdapter extends WebSecurityConfigurerAdapter {

		@Autowired
		private FindByIndexNameSessionRepository<ExpiringSession> sessionRepository;

		@Autowired
		private AppUserDetailsService userDetailsService;

		@Autowired
		private AppAuthenticationEntryPoint authenticationEntryPoint;

		@Autowired
		private AppAuthenticationResultHandler authenticationResultHandler;

		@Autowired
		public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
			auth.userDetailsService(userDetailsService);
		}

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			log.info("Configuring WEB security...");

			http.authorizeRequests().antMatchers("/webjars/**", "/login**", "/info**").permitAll();
			http.authorizeRequests().anyRequest().fullyAuthenticated();

			// form login
			http
			.formLogin()
			.permitAll()
			.loginPage("/login.html")
			.successHandler(authenticationResultHandler)
			.failureHandler(authenticationResultHandler);

			// logout
			http
			.logout()
			.permitAll()
			.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
			.logoutSuccessUrl("/login.html");

			// exception handling
			http
			.exceptionHandling()
			.authenticationEntryPoint(authenticationEntryPoint);

			// session management & concurrency control
			http
			.sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
			.maximumSessions(1).maxSessionsPreventsLogin(true).sessionRegistry(sessionRegistry());

			// CSRF
			http.csrf().disable();
		}

		@Bean
		SpringSessionBackedSessionRegistry sessionRegistry() {
			return new SpringSessionBackedSessionRegistry(sessionRepository);
		}

	}

}
