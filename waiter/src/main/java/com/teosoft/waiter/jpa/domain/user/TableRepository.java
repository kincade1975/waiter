package com.teosoft.waiter.jpa.domain.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

public interface TableRepository extends CrudRepository<Table, Integer>, JpaSpecificationExecutor<Table> {

	Page<Table> findAll(Pageable pageable);

	Table findByCode(String code);

}