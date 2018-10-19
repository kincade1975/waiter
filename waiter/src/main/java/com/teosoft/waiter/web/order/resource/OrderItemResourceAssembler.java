package com.teosoft.waiter.web.order.resource;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.teosoft.waiter.jpa.domain.order.OrderItem;
import com.teosoft.waiter.web.user.resource.MenuItemResourceAssembler;

@Component
public class OrderItemResourceAssembler {

	@Autowired
	private MenuItemResourceAssembler menuItemResourceAssembler;

	public OrderItemResource toResource(OrderItem entity, boolean light) {
		OrderItemResource resource = new OrderItemResource();
		resource.setId(entity.getId());
		resource.setItem(menuItemResourceAssembler.toResource(entity.getItem()));
		resource.setQuantity(entity.getQuantity());
		resource.setCreationDate(entity.getCreationDate().toDate());
		resource.setLastModifiedDate(entity.getLastModifiedDate().toDate());
		return resource;
	}

	public List<OrderItemResource> toResources(Iterable<OrderItem> entities, boolean light) {
		List<OrderItemResource> resources = new ArrayList<>();
		for (OrderItem entity : entities) {
			resources.add(toResource(entity, light));
		}
		return resources;
	}

}
