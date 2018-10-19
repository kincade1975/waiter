package com.teosoft.waiter.web.order.resource.ext;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class OrderRequestResourceExt {

	@JsonProperty("senderUuid")
	private String senderUuid;

	@JsonProperty("senderSerial")
	private String senderSerial;

	@JsonProperty("senderPlatform")
	private String senderPlatform;

	@JsonProperty("tableCode")
	private String tableCode;

	@JsonProperty("items")
	private List<OrderItemResourceExt> items;

}
