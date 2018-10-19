package com.teosoft.waiter.security.auth;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor @AllArgsConstructor @Getter @Setter @ToString
public class AppUser implements Serializable {

	private static final long serialVersionUID = 2620392450322959747L;

	private Integer id;

	private boolean superadmin;

	private boolean admin;

	private String username;

	private String password;

	private boolean expired;

	private boolean locked;

	private boolean enabled;

	private boolean credentialsExpired;

}
