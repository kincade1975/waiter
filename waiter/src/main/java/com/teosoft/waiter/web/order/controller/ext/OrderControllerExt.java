package com.teosoft.waiter.web.order.controller.ext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.teosoft.waiter.web.order.resource.ext.OrderRequestResourceExt;
import com.teosoft.waiter.web.order.resource.ext.OrderResponseResourceExt;
import com.teosoft.waiter.web.order.service.ext.OrderServiceExt;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = { "/e/api/v1/orders" })
public class OrderControllerExt {

	@Autowired
	private OrderServiceExt orderServiceExt;

	@CrossOrigin(origins = "*")
	@RequestMapping(method = RequestMethod.POST, value = "/submit")
	public ResponseEntity<OrderResponseResourceExt> submitOrder(@RequestBody OrderRequestResourceExt resource) {
		long start = System.currentTimeMillis();
		try {
			log.debug("Processing submit order request {}", resource);
			return orderServiceExt.submitOrder(resource);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return new ResponseEntity<>(new OrderResponseResourceExt(HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			log.debug("Submit order request processed in {} ms.", System.currentTimeMillis() - start);
		}
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(method = RequestMethod.GET, value = "/cancel/{sender}/{orderId}")
	public ResponseEntity<OrderResponseResourceExt> cancelOrder(@PathVariable String sender, @PathVariable Integer orderId) {
		long start = System.currentTimeMillis();
		try {
			log.debug("Processing cancel order request [{}]", orderId);
			return orderServiceExt.cancelOrder(sender, orderId);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return new ResponseEntity<>(new OrderResponseResourceExt(HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			log.debug("Cancel order request processed in {} ms.", System.currentTimeMillis() - start);
		}
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(method = RequestMethod.POST, value = "/waiter")
	public ResponseEntity<OrderResponseResourceExt> callWaiter(@RequestBody OrderRequestResourceExt resource) {
		long start = System.currentTimeMillis();
		try {
			log.debug("Processing CallWaiter request {}", resource);
			return orderServiceExt.callWaiter(resource);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return new ResponseEntity<>(new OrderResponseResourceExt(HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			log.debug("CallWaiter request processed in {} ms.", System.currentTimeMillis() - start);
		}
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(method = RequestMethod.POST, value = "/bill")
	public ResponseEntity<?> requestBill(@RequestBody OrderRequestResourceExt resource) {
		long start = System.currentTimeMillis();
		try {
			log.debug("Processing RequestBill request {}", resource);
			return orderServiceExt.requestBill(resource);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			log.debug("RequestBill request processed in {} ms.", System.currentTimeMillis() - start);
		}
	}

}
