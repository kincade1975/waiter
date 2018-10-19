package com.teosoft.waiter.socket.resource;

import lombok.Data;

@Data
public abstract class AbstractMessage {

	public enum MessageType { ORDER, CANCEL, CALL_WAITER, REQUEST_BILL };

	private MessageType type;

	private String content;

	private long timestamp;

}
