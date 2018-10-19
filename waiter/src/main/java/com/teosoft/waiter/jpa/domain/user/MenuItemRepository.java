package com.teosoft.waiter.jpa.domain.user;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

public interface MenuItemRepository extends CrudRepository<MenuItem, Integer>, JpaSpecificationExecutor<MenuItem> {

}