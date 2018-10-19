package com.teosoft.waiter.jpa.domain.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

public interface ApplicationEventRepository extends CrudRepository<ApplicationEvent, Integer>, JpaSpecificationExecutor<ApplicationEvent> {

	Page<ApplicationEvent> findAll(Pageable pageable);

}