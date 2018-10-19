package com.teosoft.waiter.web.session.resource;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class SessionResource {

	@JsonProperty("id")
	private String id;

	@JsonProperty("username")
	private String username;

	@JsonProperty("expired")
	private boolean expired;

	@JsonProperty("maxInactiveIntervalInSeconds")
	private int maxInactiveIntervalInSeconds;

	@JsonProperty("creationDate")
	private Date creationDate;

	@JsonProperty("lastModifiedDate")
	private Date lastModifiedDate;

	@JsonProperty("duration")
	private String duration;

}
