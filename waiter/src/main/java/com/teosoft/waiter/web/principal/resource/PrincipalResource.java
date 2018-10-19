package com.teosoft.waiter.web.principal.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor @AllArgsConstructor
@JsonInclude(Include.ALWAYS)
public class PrincipalResource {

	@JsonProperty("username")
	private String username;

	@JsonProperty("permissions")
	private List<String> permissions = new ArrayList<>();

	@JsonProperty("superadmin")
	private Boolean superadmin = Boolean.FALSE;

	@JsonProperty("admin")
	private Boolean admin = Boolean.FALSE;

	@JsonProperty("properties")
	private Map<String, String> properties = new HashMap<>();

}
