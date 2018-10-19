package com.teosoft.waiter.web.user.resource.ext;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class MenuItemResourceExt {

	@JsonProperty("id")
	private Integer id;

	@JsonProperty("name")
	private String name;

	@JsonProperty("unit")
	private String unit;

	@JsonProperty("description")
	private String description;

	@JsonProperty("price")
	private Double price;

}
