package com.teosoft.waiter.security.auth;

import java.util.Collection;
import java.util.HashSet;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class AppUserDetails implements UserDetails {

	private static final long serialVersionUID = -2933814061822620978L;

	private final AppUser user;

	public AppUserDetails(AppUser user) {
		super();
		this.user = user;
	}

	public AppUser getUser() {
		return user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return new HashSet<>();
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return !user.isExpired();
	}

	@Override
	public boolean isAccountNonLocked() {
		return !user.isLocked();
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return !user.isCredentialsExpired();
	}

	@Override
	public boolean isEnabled() {
		return user.isEnabled();
	}

}