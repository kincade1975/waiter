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
public class TableResource extends AbstractResource {

	@JsonProperty("id")
	private Integer id;

	@JsonProperty("position")
	private Integer position;

	@JsonProperty("identifier")
	private String identifier;

	@JsonProperty("description")
	private String description;

	@JsonProperty("code")
	private String code;

	@JsonProperty("qrCode")
	private String qrCode;

}
