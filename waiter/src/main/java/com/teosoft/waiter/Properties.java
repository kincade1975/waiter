package com.teosoft.waiter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "waiter")
@Getter @Setter
public class Properties {

	/** Date format */
	private String dateFormat;

	/** Date-time format */
	private String dateTimeFormat;

	/** API key */
	private String apiKey;

	/** STOMP client end-point */
	private String stompEndpoint;

	private int orderOverflowThreshold;

}