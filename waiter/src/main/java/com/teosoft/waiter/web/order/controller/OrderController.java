package com.teosoft.waiter.web.order.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.teosoft.waiter.jpa.domain.order.Order.OrderStatus;
import com.teosoft.waiter.web.PageableResource;
import com.teosoft.waiter.web.UiGridResource;
import com.teosoft.waiter.web.order.resource.OrderResource;
import com.teosoft.waiter.web.order.service.OrderService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = { "/api/v1/orders" })
public class OrderController {

	@Autowired
	private OrderService orderService;

	@RequestMapping(method = RequestMethod.GET, value="/{id}")
	public OrderResource find(@PathVariable Integer id) {
		return orderService.find(id);
	}

	@RequestMapping(method = RequestMethod.DELETE, value="/{id}")
	public void delete(@PathVariable Integer id, HttpServletRequest request) {
		orderService.delete(id);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/page")
	public PageableResource<OrderResource> page(@RequestBody UiGridResource resource) {
		return orderService.page(resource);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/set-status/{id}/{status}")
	public ResponseEntity<?> setStatus(@PathVariable Integer id, @PathVariable String status) {
		long start = System.currentTimeMillis();
		try {
			log.debug("Settings status to [{}] for order with ID [{}]", id, status);
			return orderService.setStatus(id, OrderStatus.valueOf(status));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			log.debug("Settings order status processed in {} ms.", System.currentTimeMillis() - start);
		}
	}

}
