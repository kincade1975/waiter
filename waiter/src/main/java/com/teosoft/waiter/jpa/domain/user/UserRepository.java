package com.teosoft.waiter.jpa.domain.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer>, JpaSpecificationExecutor<User> {

	Page<User> findAll(Pageable pageable);

	User findByUsername(String username);

}