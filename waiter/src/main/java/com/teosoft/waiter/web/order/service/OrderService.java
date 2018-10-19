package com.teosoft.waiter.web.order.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import com.teosoft.waiter.jpa.SearchCriteria;
import com.teosoft.waiter.jpa.SearchOperation;
import com.teosoft.waiter.jpa.domain.order.Order;
import com.teosoft.waiter.jpa.domain.order.Order.OrderStatus;
import com.teosoft.waiter.jpa.domain.order.Order.OrderType;
import com.teosoft.waiter.jpa.domain.order.OrderRepository;
import com.teosoft.waiter.jpa.domain.order.OrderSpecification;
import com.teosoft.waiter.jpa.domain.user.User;
import com.teosoft.waiter.jpa.domain.user.User.UserRole;
import com.teosoft.waiter.web.PageableResource;
import com.teosoft.waiter.web.UiGridFilterResource;
import com.teosoft.waiter.web.UiGridResource;
import com.teosoft.waiter.web.WebBaseService;
import com.teosoft.waiter.web.order.resource.OrderResource;
import com.teosoft.waiter.web.order.resource.OrderResourceAssembler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OrderService extends WebBaseService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private OrderResourceAssembler orderResourceAssembler;

	@Transactional(readOnly = true)
	public OrderResource find(Integer id) {
		log.debug("Finding order [{}]...", id);
		Order entity = orderRepository.findOne(id);
		if (getAuthenticatedUser().getRole() == UserRole.CUSTOMER && getAuthenticatedUser().getId().intValue() != entity.getId().intValue()) {
			throw new HttpClientErrorException(HttpStatus.FORBIDDEN, String.format("You are not allowed to access order with ID [%s]", id));
		}
		return orderResourceAssembler.toResource(entity, false);
	}

	@Transactional
	public void delete(Integer id) {
		log.debug("Deleting order [{}]...", id);
		orderRepository.delete(orderRepository.findOne(id));
	}

	@Transactional(readOnly = true)
	public PageableResource<OrderResource> page(UiGridResource resource) {
		log.debug("Getting page [{}]...", resource);

		Page<Order> page = null;

		List<Specification<Order>> specifications = new ArrayList<>();

		if (getAuthenticatedUser().getRole() == UserRole.CUSTOMER) {
			specifications.add(new OrderSpecification(new SearchCriteria("user.id", SearchOperation.EQUALITY, getAuthenticatedUser().getId())));
		}

		if (resource.getFilter() != null && !resource.getFilter().isEmpty()) {
			for (UiGridFilterResource uiGridFilterResource : resource.getFilter()) {
				OrderSpecification specification = createSpecification(uiGridFilterResource);
				if (specification != null) {
					specifications.add(specification);
				}
			}
		}

		if (!specifications.isEmpty()) {
			// filtering
			Specification<Order> specification = specifications.get(0);
			if (specifications.size() > 1) {
				for (int i=1; i<specifications.size(); i++) {
					specification = Specifications.where(specification).and(specifications.get(i));
				}
			}
			page = orderRepository.findAll(specification, createPageable(resource));
		} else {
			// no filtering
			page = orderRepository.findAll(createPageable(resource));
		}

		return new PageableResource<>(page.getTotalElements(), orderResourceAssembler.toResources(page.getContent(), true));
	}

	private OrderSpecification createSpecification(UiGridFilterResource resource) {
		if (resource.getName().equalsIgnoreCase("id")) {
			return new OrderSpecification(new SearchCriteria(resource.getName(), SearchOperation.EQUALITY, resource.getTerm()));
		} else if (resource.getName().equalsIgnoreCase("type")) {
			if (EnumUtils.isValidEnum(OrderType.class, resource.getTerm().toUpperCase())) {
				return new OrderSpecification(new SearchCriteria(resource.getName(), SearchOperation.EQUALITY, OrderType.valueOf(resource.getTerm().toUpperCase())));
			} else {
				return null;
			}
		} else if (resource.getName().equalsIgnoreCase("status")) {
			if (EnumUtils.isValidEnum(OrderStatus.class, resource.getTerm().toUpperCase())) {
				return new OrderSpecification(new SearchCriteria(resource.getName(), SearchOperation.EQUALITY, OrderStatus.valueOf(resource.getTerm().toUpperCase())));
			} else {
				return null;
			}
		} else if (resource.getName().equalsIgnoreCase("creationDate") || resource.getName().equalsIgnoreCase("lastModifiedDate")) {
			return new OrderSpecification(new SearchCriteria(resource.getName(), SearchOperation.BETWEEN, getDateRange(resource.getTerm())));
		} else {
			return new OrderSpecification(new SearchCriteria(resource.getName(), SearchOperation.CONTAINS, resource.getTerm()));
		}
	}

	@Transactional
	public ResponseEntity<?> setStatus(Integer id, OrderStatus status) {
		Order entity = orderRepository.findOne(id);

		if (entity == null) {
			log.debug("Order [{}] not found", id);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		if (!isAuthorized(entity)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}

		entity.setStatus(status);
		orderRepository.save(entity);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	private boolean isAuthorized(Order entity) {
		User user = getAuthenticatedUser();
		if (entity.getUser().getId().intValue() != user.getId().intValue()) {
			log.warn("User [{}] is not authorized to modify order [{}]", user.getId(), entity.getId());
			return false;
		}
		return true;
	}

}