package com.teosoft.waiter.web.user.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import com.teosoft.waiter.jpa.SearchCriteria;
import com.teosoft.waiter.jpa.SearchOperation;
import com.teosoft.waiter.jpa.domain.user.Table;
import com.teosoft.waiter.jpa.domain.user.User;
import com.teosoft.waiter.jpa.domain.user.User.UserRole;
import com.teosoft.waiter.jpa.domain.user.User.UserStatus;
import com.teosoft.waiter.jpa.domain.user.UserSpecification;
import com.teosoft.waiter.security.auth.AppUser;
import com.teosoft.waiter.web.PageableResource;
import com.teosoft.waiter.web.UiGridFilterResource;
import com.teosoft.waiter.web.UiGridResource;
import com.teosoft.waiter.web.WebBaseService;
import com.teosoft.waiter.web.user.resource.UserResource;
import com.teosoft.waiter.web.user.resource.UserResourceAssembler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService extends WebBaseService {

	@Autowired
	private UserResourceAssembler userResourceAssembler;

	@Transactional(readOnly = true)
	public List<UserResource> findAll() {
		log.debug("Finding users...");
		return userResourceAssembler.toResources(userRepository.findAll(), true);
	}

	@Transactional(readOnly = true)
	public UserResource find(Integer id) {
		log.debug("Finding user [{}]...", id);
		if (id == 0) {
			AppUser appUser = appUserDetailsService.getAuthenticatedUser();

			UserResource resource = new UserResource();
			resource.setStatus(UserStatus.INACTIVE);
			resource.setOwnerId(appUser.getId());
			return resource;
		}

		User user = userRepository.findOne(id);

		if (user == null) {
			throw new HttpClientErrorException(HttpStatus.NOT_FOUND, String.format("User with ID [%s] not found", id));
		}

		AppUser appUser = appUserDetailsService.getAuthenticatedUser();

		if (user.getRole() == UserRole.ADMIN && !user.getOwnerId().equals(appUser.getId())) {
			throw new HttpClientErrorException(HttpStatus.FORBIDDEN);
		}

		return userResourceAssembler.toResource(user, false);
	}

	@Transactional
	public UserResource save(UserResource resource) {
		log.debug("Saving user [{}]...", resource);
		User entity = (resource.getId() == null) ? userResourceAssembler.createEntity(resource) : userResourceAssembler.updateEntity(userRepository.findOne(resource.getId()), resource);
		auditingHandler.markModified(entity);
		userRepository.save(entity);

		for (Table table : entity.getTables()) {
			if (StringUtils.isBlank(table.getCode())) {
				table.setCode(StringUtils.leftPad(table.getId().toString(), 6, '0'));
			}
		}
		userRepository.save(entity);

		return userResourceAssembler.toResource(entity, false);
	}

	@Transactional
	public UserResource activate(Integer id) {
		log.debug("Activating user [{}]...", id);
		User entity = userRepository.findOne(id);
		entity.setStatus(UserStatus.ACTIVE);
		userRepository.save(entity);
		return userResourceAssembler.toResource(entity, true);
	}

	@Transactional
	public UserResource deactivate(Integer id) {
		log.debug("Deactivating user [{}]...", id);
		User entity = userRepository.findOne(id);
		entity.setStatus(UserStatus.INACTIVE);
		userRepository.save(entity);
		return userResourceAssembler.toResource(entity, true);
	}

	@Transactional
	public void delete(Integer id) {
		log.debug("Deleting user [{}]...", id);
		userRepository.delete(userRepository.findOne(id));
	}

	@Transactional(readOnly = true)
	public PageableResource<UserResource> page(UiGridResource resource) {
		log.debug("Getting page [{}]...", resource);

		Page<User> page = null;

		List<Specification<User>> specifications = new ArrayList<>();

		AppUser appUser = appUserDetailsService.getAuthenticatedUser();

		if (appUser.isAdmin()) {
			specifications.add(new UserSpecification(new SearchCriteria("ownerId", SearchOperation.EQUALITY, appUser.getId())));
		}
		specifications.add(new UserSpecification(new SearchCriteria("role", SearchOperation.EQUALITY, UserRole.CUSTOMER)));

		if (resource.getFilter() != null && !resource.getFilter().isEmpty()) {
			for (UiGridFilterResource uiGridFilterResource : resource.getFilter()) {
				UserSpecification specification = createSpecification(uiGridFilterResource);
				if (specification != null) {
					specifications.add(specification);
				}
			}
		}

		if (!specifications.isEmpty()) {
			// filtering
			Specification<User> specification = specifications.get(0);
			if (specifications.size() > 1) {
				for (int i=1; i<specifications.size(); i++) {
					specification = Specifications.where(specification).and(specifications.get(i));
				}
			}
			page = userRepository.findAll(specification, createPageable(resource));
		} else {
			// no filtering
			page = userRepository.findAll(createPageable(resource));
		}

		return new PageableResource<>(page.getTotalElements(), userResourceAssembler.toResources(page.getContent(), true));
	}

	private UserSpecification createSpecification(UiGridFilterResource resource) {
		if (resource.getName().equalsIgnoreCase("id") || resource.getName().equalsIgnoreCase("ownerId")) {
			return new UserSpecification(new SearchCriteria(resource.getName(), SearchOperation.EQUALITY, resource.getTerm()));
		} else if (resource.getName().equalsIgnoreCase("status")) {
			if (EnumUtils.isValidEnum(UserStatus.class, resource.getTerm().toUpperCase())) {
				return new UserSpecification(new SearchCriteria(resource.getName(), SearchOperation.EQUALITY, UserStatus.valueOf(resource.getTerm().toUpperCase())));
			} else {
				return null;
			}
		} else if (resource.getName().equalsIgnoreCase("role")) {
			if (EnumUtils.isValidEnum(UserRole.class, resource.getTerm().toUpperCase())) {
				return new UserSpecification(new SearchCriteria(resource.getName(), SearchOperation.EQUALITY, UserRole.valueOf(resource.getTerm().toUpperCase())));
			} else {
				return null;
			}
		} else if (resource.getName().equalsIgnoreCase("creationDate") || resource.getName().equalsIgnoreCase("lastModifiedDate")) {
			return new UserSpecification(new SearchCriteria(resource.getName(), SearchOperation.BETWEEN, getDateRange(resource.getTerm())));
		} else {
			return new UserSpecification(new SearchCriteria(resource.getName(), SearchOperation.CONTAINS, resource.getTerm()));
		}
	}

}