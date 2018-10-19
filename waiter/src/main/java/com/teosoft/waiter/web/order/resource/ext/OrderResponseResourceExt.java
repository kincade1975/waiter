package com.teosoft.waiter.web.order.resource.ext;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class OrderResponseResourceExt {

	@JsonProperty("status")
	private HttpStatus status;

	@JsonProperty("message")
	private String message;

	@JsonProperty("orderId")
	private Integer orderId;

	public OrderResponseResourceExt(HttpStatus status) {
		this.status = status;
	}

	public OrderResponseResourceExt(HttpStatus status, String message) {
		this.status = status;
		this.message = message;
	}

	public OrderResponseResourceExt(HttpStatus status, Integer orderId) {
		this.status = status;
		this.orderId = orderId;
	}

}