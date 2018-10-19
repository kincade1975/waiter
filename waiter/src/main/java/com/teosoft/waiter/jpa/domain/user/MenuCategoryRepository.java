package com.teosoft.waiter.jpa.domain.user;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

public interface MenuCategoryRepository extends CrudRepository<MenuCategory, Integer>, JpaSpecificationExecutor<MenuCategory> {

}