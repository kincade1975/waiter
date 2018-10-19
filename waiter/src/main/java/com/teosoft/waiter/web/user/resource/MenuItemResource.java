package com.teosoft.waiter.web.user.resource;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.teosoft.waiter.web.AbstractResource;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@JsonInclude(Include.NON_NULL)
public class MenuItemResource extends AbstractResource {

	@JsonProperty("id")
	private Integer id;

	@JsonProperty("position")
	private Integer position;

	@JsonProperty("name")
	private String name;

	@JsonProperty("unit")
	private String unit;

	@JsonProperty("description")
	private String description;

	@JsonProperty("price")
	private Double price;

}
