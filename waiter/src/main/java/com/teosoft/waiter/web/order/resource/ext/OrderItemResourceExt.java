package com.teosoft.waiter.web.order.resource.ext;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class OrderItemResourceExt {

	@JsonProperty("id")
	private Integer id;

	@JsonProperty("quantity")
	private Integer quantity;

}
