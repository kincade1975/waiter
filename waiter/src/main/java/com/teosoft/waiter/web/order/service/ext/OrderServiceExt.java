package com.teosoft.waiter.web.order.service.ext;

import java.util.HashSet;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teosoft.waiter.Properties;
import com.teosoft.waiter.jpa.domain.order.Order;
import com.teosoft.waiter.jpa.domain.order.Order.OrderStatus;
import com.teosoft.waiter.jpa.domain.order.Order.OrderType;
import com.teosoft.waiter.jpa.domain.order.OrderItem;
import com.teosoft.waiter.jpa.domain.order.OrderRepository;
import com.teosoft.waiter.jpa.domain.user.MenuItem;
import com.teosoft.waiter.jpa.domain.user.MenuItemRepository;
import com.teosoft.waiter.jpa.domain.user.Table;
import com.teosoft.waiter.jpa.domain.user.TableRepository;
import com.teosoft.waiter.jpa.domain.user.User.UserStatus;
import com.teosoft.waiter.socket.config.SocketEventListener;
import com.teosoft.waiter.socket.resource.AbstractMessage.MessageType;
import com.teosoft.waiter.socket.resource.OrderMessage;
import com.teosoft.waiter.socket.service.SocketService;
import com.teosoft.waiter.web.WebBaseService;
import com.teosoft.waiter.web.order.resource.OrderResourceAssembler;
import com.teosoft.waiter.web.order.resource.ext.OrderItemResourceExt;
import com.teosoft.waiter.web.order.resource.ext.OrderRequestResourceExt;
import com.teosoft.waiter.web.order.resource.ext.OrderResponseResourceExt;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OrderServiceExt extends WebBaseService {

	@Autowired
	private Properties properties;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private SocketService socketService;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private OrderResourceAssembler orderResourceAssembler;

	@Autowired
	private TableRepository tableRepository;

	@Autowired
	private MenuItemRepository menuItemRepository;

	@Autowired
	private SocketEventListener socketEventListener;

	@Transactional
	public ResponseEntity<OrderResponseResourceExt> submitOrder(OrderRequestResourceExt resource) throws Exception {
		// validate sender
		if (StringUtils.isBlank(resource.getSenderUuid())) {
			return new ResponseEntity<>(new OrderResponseResourceExt(HttpStatus.BAD_REQUEST, "Sender not specified"), HttpStatus.BAD_REQUEST);
		}

		// validate table
		if (StringUtils.isBlank(resource.getTableCode())) {
			return new ResponseEntity<>(new OrderResponseResourceExt(HttpStatus.BAD_REQUEST, "Table not specified"), HttpStatus.BAD_REQUEST);
		}

		// validate items
		if (resource.getItems() == null || resource.getItems().isEmpty()) {
			return new ResponseEntity<>(new OrderResponseResourceExt(HttpStatus.BAD_REQUEST, "No items found"), HttpStatus.BAD_REQUEST);
		}

		Table table = tableRepository.findByCode(resource.getTableCode());
		if (table == null) {
			log.warn("Table with code [{}] not found", resource.getTableCode());
			return new ResponseEntity<>(new OrderResponseResourceExt(HttpStatus.NOT_FOUND, String.format("Table with code [%s] not found", resource.getTableCode())), HttpStatus.NOT_FOUND);
		}

		// validate receiver
		if (table.getUser().getStatus() == UserStatus.INACTIVE || !socketEventListener.isSocketOpened(table.getUser().getUsername())) {
			return new ResponseEntity<>(new OrderResponseResourceExt(HttpStatus.SERVICE_UNAVAILABLE, "Service is unavailable"), HttpStatus.SERVICE_UNAVAILABLE);
		}

		Order order = new Order();
		order.setType(OrderType.ORDER);
		order.setStatus(OrderStatus.PENDING);
		order.setUser(table.getUser());
		order.setTable(table);
		order.setSenderUuid(resource.getSenderUuid());
		order.setSenderSerial(resource.getSenderSerial());
		order.setSenderPlatform(resource.getSenderPlatform());
		order.setItems(new HashSet<>());

		for (OrderItemResourceExt itemResource : resource.getItems()) {
			MenuItem menuItem = menuItemRepository.findOne(itemResource.getId());
			if (menuItem == null) {
				log.warn("Menu item with ID [{}] not found", itemResource.getId());
				return new ResponseEntity<>(new OrderResponseResourceExt(HttpStatus.NOT_FOUND, String.format("Menu item with ID [%s] not found", itemResource.getId())), HttpStatus.NOT_FOUND);
			}

			OrderItem orderItem = new OrderItem();
			orderItem.setOrder(order);
			orderItem.setItem(menuItem);
			orderItem.setQuantity(itemResource.getQuantity());

			order.getItems().add(orderItem);
		}

		order.setHashCode(resource.hashCode());

		DateTime dateThreshold = new DateTime().minusSeconds(properties.getOrderOverflowThreshold());
		for (Order o : orderRepository.findByUserAndHashCodeAndType(order.getUser(), order.getHashCode(), OrderType.ORDER)) {
			if (o.getCreationDate().isAfter(dateThreshold)) {
				return new ResponseEntity<>(new OrderResponseResourceExt(HttpStatus.CONFLICT, String.format("Order request with hash code [%s] was already submitted in last %s seconds", order.getHashCode(), properties.getOrderOverflowThreshold())), HttpStatus.CONFLICT);
			}
		}

		order = orderRepository.save(order);

		socketService.sendToUser(
				new OrderMessage(MessageType.ORDER, objectMapper.writeValueAsString(orderResourceAssembler.toResource(order, false))),
				order.getUser().getUsername());

		return new ResponseEntity<>(new OrderResponseResourceExt(HttpStatus.OK, order.getId()), HttpStatus.OK);
	}

	@Transactional
	public ResponseEntity<OrderResponseResourceExt> cancelOrder(String senderUuid, Integer orderId) throws Exception {
		Order order = orderRepository.findOne(orderId);

		if (order == null) {
			log.warn("Order [{}] not found", orderId);
			return new ResponseEntity<>(new OrderResponseResourceExt(HttpStatus.NOT_FOUND, String.format("Order [%s] not found", orderId)), HttpStatus.NOT_FOUND);
		}

		if (!order.getSenderUuid().equals(senderUuid)) {
			log.warn("Sender [{}] is not allowed to cancel order [{}]", senderUuid, order.getId());
			return new ResponseEntity<>(new OrderResponseResourceExt(HttpStatus.FORBIDDEN, String.format("Sender [%s] is not allowed to cancel order [%s]", senderUuid, order.getId())), HttpStatus.FORBIDDEN);
		}

		if (order.getStatus() == OrderStatus.PENDING) {
			order.setStatus(OrderStatus.CANCELLED);
			orderRepository.save(order);

			socketService.sendToUser(
					new OrderMessage(MessageType.CANCEL, objectMapper.writeValueAsString(orderResourceAssembler.toResource(order, false))),
					order.getUser().getUsername());

			log.debug("Order [{}] successfully cancelled", orderId);
			return new ResponseEntity<>(new OrderResponseResourceExt(HttpStatus.OK), HttpStatus.OK);
		} else {
			log.warn("Order [{}] cannot be cancelled due to its current status [{}]", order.getId(), order.getStatus());
			return new ResponseEntity<>(new OrderResponseResourceExt(HttpStatus.FAILED_DEPENDENCY, String.format("Order [%s] cannot be cancelled due to its current status [%s]", order.getId(), order.getStatus())), HttpStatus.FAILED_DEPENDENCY);
		}

	}

	@Transactional
	public ResponseEntity<OrderResponseResourceExt> callWaiter(OrderRequestResourceExt resource) throws Exception {
		// validate sender
		if (StringUtils.isBlank(resource.getSenderUuid())) {
			return new ResponseEntity<>(new OrderResponseResourceExt(HttpStatus.BAD_REQUEST, "Sender not specified"), HttpStatus.BAD_REQUEST);
		}

		// validate table
		if (StringUtils.isBlank(resource.getTableCode())) {
			return new ResponseEntity<>(new OrderResponseResourceExt(HttpStatus.BAD_REQUEST, "Table not specified"), HttpStatus.BAD_REQUEST);
		}

		Table table = tableRepository.findByCode(resource.getTableCode());
		if (table == null) {
			log.warn("Table with code [{}] not found", resource.getTableCode());
			return new ResponseEntity<>(new OrderResponseResourceExt(HttpStatus.NOT_FOUND, String.format("Table with code [%s] not found", resource.getTableCode())), HttpStatus.NOT_FOUND);
		}

		// validate receiver
		if (table.getUser().getStatus() == UserStatus.INACTIVE || !socketEventListener.isSocketOpened(table.getUser().getUsername())) {
			return new ResponseEntity<>(new OrderResponseResourceExt(HttpStatus.SERVICE_UNAVAILABLE, "Service is unavailable"), HttpStatus.SERVICE_UNAVAILABLE);
		}

		Order order = new Order();
		order.setType(OrderType.CALL_WAITER);
		order.setStatus(OrderStatus.PENDING);
		order.setUser(table.getUser());
		order.setTable(table);
		order.setSenderUuid(resource.getSenderUuid());
		order.setSenderSerial(resource.getSenderSerial());
		order.setSenderPlatform(resource.getSenderPlatform());
		order.setHashCode(resource.hashCode());

		DateTime dateThreshold = new DateTime().minusSeconds(properties.getOrderOverflowThreshold());
		for (Order o : orderRepository.findByUserAndHashCodeAndType(order.getUser(), order.getHashCode(), OrderType.CALL_WAITER)) {
			if (o.getCreationDate().isAfter(dateThreshold)) {
				return new ResponseEntity<>(new OrderResponseResourceExt(HttpStatus.CONFLICT, String.format("CallWaiter request with hash code [%s] was already submitted in last %s seconds", order.getHashCode(), properties.getOrderOverflowThreshold())), HttpStatus.CONFLICT);
			}
		}

		order = orderRepository.save(order);

		socketService.sendToUser(
				new OrderMessage(MessageType.CALL_WAITER, objectMapper.writeValueAsString(orderResourceAssembler.toResource(order, true))),
				order.getUser().getUsername());

		return new ResponseEntity<>(new OrderResponseResourceExt(HttpStatus.OK, order.getId()), HttpStatus.OK);
	}

	@Transactional
	public ResponseEntity<OrderResponseResourceExt> requestBill(OrderRequestResourceExt resource) throws Exception {
		// validate sender
		if (StringUtils.isBlank(resource.getSenderUuid())) {
			return new ResponseEntity<>(new OrderResponseResourceExt(HttpStatus.BAD_REQUEST, "Sender not specified"), HttpStatus.BAD_REQUEST);
		}

		// validate table
		if (StringUtils.isBlank(resource.getTableCode())) {
			return new ResponseEntity<>(new OrderResponseResourceExt(HttpStatus.BAD_REQUEST, "Table not specified"), HttpStatus.BAD_REQUEST);
		}

		Table table = tableRepository.findByCode(resource.getTableCode());
		if (table == null) {
			log.warn("Table with code [{}] not found", resource.getTableCode());
			return new ResponseEntity<>(new OrderResponseResourceExt(HttpStatus.NOT_FOUND, String.format("Table with code [%s] not found", resource.getTableCode())), HttpStatus.NOT_FOUND);
		}

		// validate receiver
		if (table.getUser().getStatus() == UserStatus.INACTIVE || !socketEventListener.isSocketOpened(table.getUser().getUsername())) {
			return new ResponseEntity<>(new OrderResponseResourceExt(HttpStatus.SERVICE_UNAVAILABLE, "Service is unavailable"), HttpStatus.SERVICE_UNAVAILABLE);
		}

		Order order = new Order();
		order.setType(OrderType.REQUEST_BILL);
		order.setStatus(OrderStatus.PENDING);
		order.setUser(table.getUser());
		order.setTable(table);
		order.setSenderUuid(resource.getSenderUuid());
		order.setSenderSerial(resource.getSenderSerial());
		order.setSenderPlatform(resource.getSenderPlatform());
		order.setHashCode(resource.hashCode());

		DateTime dateThreshold = new DateTime().minusSeconds(properties.getOrderOverflowThreshold());
		for (Order o : orderRepository.findByUserAndHashCodeAndType(order.getUser(), order.getHashCode(), OrderType.REQUEST_BILL)) {
			if (o.getCreationDate().isAfter(dateThreshold)) {
				return new ResponseEntity<>(new OrderResponseResourceExt(HttpStatus.CONFLICT, String.format("RequestBill request with hash code [%s] was already submitted in last %s seconds", order.getHashCode(), properties.getOrderOverflowThreshold())), HttpStatus.CONFLICT);
			}
		}

		order = orderRepository.save(order);

		socketService.sendToUser(
				new OrderMessage(MessageType.REQUEST_BILL, objectMapper.writeValueAsString(orderResourceAssembler.toResource(order, true))),
				order.getUser().getUsername());

		return new ResponseEntity<>(new OrderResponseResourceExt(HttpStatus.OK, order.getId()), HttpStatus.OK);
	}

}