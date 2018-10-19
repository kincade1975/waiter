package com.teosoft.waiter.web.order.resource;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.teosoft.waiter.web.AbstractResource;
import com.teosoft.waiter.web.user.resource.MenuItemResource;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(Include.NON_NULL)
public class OrderItemResource extends AbstractResource {

	@JsonProperty("id")
	private Integer id;

	@JsonProperty("item")
	private MenuItemResource item;

	@JsonProperty("quantity")
	private Integer quantity;

}
