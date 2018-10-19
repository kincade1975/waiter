package com.teosoft.waiter.web.order.resource;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.teosoft.waiter.jpa.domain.order.Order.OrderStatus;
import com.teosoft.waiter.jpa.domain.order.Order.OrderType;
import com.teosoft.waiter.web.AbstractResource;
import com.teosoft.waiter.web.user.resource.TableResource;
import com.teosoft.waiter.web.user.resource.UserResource;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(Include.NON_NULL)
public class OrderResource extends AbstractResource {

	@JsonProperty("id")
	private Integer id;

	@JsonProperty("type")
	private OrderType type;

	@JsonProperty("status")
	private OrderStatus status;

	@JsonProperty("user")
	private UserResource user;

	@JsonProperty("table")
	private TableResource table;

	@JsonProperty("senderUuid")
	private String senderUuid;

	@JsonProperty("senderSerial")
	private String senderSerial;

	@JsonProperty("senderPlatform")
	private String senderPlatform;

	@JsonProperty("items")
	private List<OrderItemResource> items;

	@JsonProperty("totalAmount")
	private Double totalAmount;

}
