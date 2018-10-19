package com.teosoft.waiter.web;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public abstract class AbstractResource {

	@JsonProperty("creationDate")
	private Date creationDate;

	@JsonProperty("lastModifiedDate")
	private Date lastModifiedDate;

}
