package com.teosoft.waiter.web.order.resource;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.teosoft.waiter.jpa.domain.order.Order;
import com.teosoft.waiter.jpa.domain.order.Order.OrderType;
import com.teosoft.waiter.jpa.domain.order.OrderItem;
import com.teosoft.waiter.web.user.resource.TableResourceAssembler;
import com.teosoft.waiter.web.user.resource.UserResourceAssembler;

@Component
public class OrderResourceAssembler {

	@Autowired
	private UserResourceAssembler userResourceAssembler;

	@Autowired
	private TableResourceAssembler tableResourceAssembler;

	@Autowired
	private OrderItemResourceAssembler orderItemResourceAssembler;

	public OrderResource toResource(Order entity, boolean light) {
		OrderResource resource = new OrderResource();
		resource.setId(entity.getId());
		resource.setType(entity.getType());
		resource.setStatus(entity.getStatus());
		resource.setUser(userResourceAssembler.toResource(entity.getUser(), light));
		resource.setTable(tableResourceAssembler.toResource(entity.getTable(), light));
		resource.setSenderUuid(entity.getSenderUuid());
		resource.setSenderSerial(entity.getSenderSerial());
		resource.setSenderPlatform(entity.getSenderPlatform());
		resource.setItems((entity.getItems() != null) ? orderItemResourceAssembler.toResources(entity.getItems(), light) : null);
		if (entity.getType() == OrderType.ORDER) {
			resource.setTotalAmount(calculateTotalAmount(entity));
		}
		resource.setCreationDate(entity.getCreationDate().toDate());
		resource.setLastModifiedDate(entity.getLastModifiedDate().toDate());
		return resource;
	}

	public List<OrderResource> toResources(Iterable<Order> entities, boolean light) {
		List<OrderResource> resources = new ArrayList<>();
		for (Order entity : entities) {
			resources.add(toResource(entity, light));
		}
		return resources;
	}

	private double calculateTotalAmount(Order entity) {
		double total = 0;
		if (entity.getItems() != null) {
			for (OrderItem orderItem : entity.getItems()) {
				total = total + (orderItem.getQuantity() * orderItem.getItem().getPrice());
			}
		}
		return total;
	}

}
