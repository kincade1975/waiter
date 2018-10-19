package com.teosoft.waiter.security.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.teosoft.waiter.jpa.domain.user.User;
import com.teosoft.waiter.jpa.domain.user.User.UserRole;
import com.teosoft.waiter.jpa.domain.user.User.UserStatus;
import com.teosoft.waiter.jpa.domain.user.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AppUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username);

		if (user == null) {
			log.warn("Access denied as user with username [{}] not found", username);
			throw new UsernameNotFoundException(String.format("User with username [%s] not found", username));
		}

		return new AppUserDetails(new AppUser(user.getId(), (user.getRole() == UserRole.SUPERADMIN) ? true : false, (user.getRole() == UserRole.ADMIN) ? true : false, user.getUsername(), user.getPassword(), false, false, (user.getStatus() == UserStatus.ACTIVE) ? true : false, false));
	}

	public AppUser getAuthenticatedUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		AppUserDetails appUserDetails = (AppUserDetails) authentication.getPrincipal();
		return appUserDetails.getUser();
	}

}
