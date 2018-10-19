package com.teosoft.waiter.web.user.resource.ext;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor @AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class UserResourceExt {

	@JsonProperty("name")
	private String name;

	@JsonProperty("address")
	private String address;

	@JsonProperty("city")
	private String city;

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

	@JsonProperty("menuNotes")
	private String menuNotes;

	@JsonProperty("categories")
	private List<MenuCategoryResourceExt> categories = new ArrayList<>();

}
