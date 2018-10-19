package com.teosoft.waiter.web.user.resource;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.teosoft.waiter.jpa.domain.user.User.UserRole;
import com.teosoft.waiter.jpa.domain.user.User.UserStatus;
import com.teosoft.waiter.web.AbstractResource;
import com.teosoft.waiter.web.city.resource.CityResource;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@JsonInclude(Include.NON_NULL)
public class UserResource extends AbstractResource {

	@JsonProperty("id")
	private Integer id;

	@JsonProperty("ownerId")
	private Integer ownerId;

	@JsonProperty("username")
	private String username;

	@JsonProperty("password")
	private String password;

	@JsonProperty("confirmPassword")
	private String confirmPassword;

	@JsonProperty("status")
	private UserStatus status;

	@JsonProperty("role")
	private UserRole role;

	@JsonProperty("name")
	private String name;

	@JsonProperty("address")
	private String address;

	@JsonProperty("city")
	private CityResource city;

	@JsonProperty("phone")
	private String phone;

	@JsonProperty("mobile")
	private String mobile;

	@JsonProperty("email")
	private String email;

	@JsonProperty("wifiName")
	private String wifiName;

	@JsonProperty("wifiPassword")
	private String wifiPassword;

	@JsonProperty("notes")
	private String notes;

	@JsonProperty("tables")
	private List<TableResource> tables = new ArrayList<>();

	@JsonProperty("categories")
	private List<MenuCategoryResource> categories = new ArrayList<>();

	@JsonProperty("menuNotes")
	private String menuNotes;

}
