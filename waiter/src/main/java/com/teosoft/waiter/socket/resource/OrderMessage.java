package com.teosoft.waiter.socket.resource;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=true)
@ToString(callSuper=true)
public class OrderMessage extends AbstractMessage {

	public OrderMessage(MessageType type, String content) throws RuntimeException {
		super();
		super.setType(type);
		super.setTimestamp(System.currentTimeMillis());
		super.setContent(content);
	}

}
