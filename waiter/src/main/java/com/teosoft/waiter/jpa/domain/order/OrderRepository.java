package com.teosoft.waiter.jpa.domain.order;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import com.teosoft.waiter.jpa.domain.order.Order.OrderType;
import com.teosoft.waiter.jpa.domain.user.User;

public interface OrderRepository extends CrudRepository<Order, Integer>, JpaSpecificationExecutor<Order> {

	Page<Order> findAll(Pageable pageable);

	List<Order> findByUserAndHashCodeAndType(User user, Integer hashCode, OrderType type);

}