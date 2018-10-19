package com.teosoft.waiter.web.user.resource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.teosoft.waiter.jpa.domain.city.CityRepository;
import com.teosoft.waiter.jpa.domain.user.MenuCategory;
import com.teosoft.waiter.jpa.domain.user.Table;
import com.teosoft.waiter.jpa.domain.user.User;
import com.teosoft.waiter.jpa.domain.user.User.UserRole;
import com.teosoft.waiter.jpa.domain.user.User.UserStatus;
import com.teosoft.waiter.web.city.resource.CityResourceAssembler;

@Component
public class UserResourceAssembler {

	@Autowired
	private CityRepository cityRepository;

	@Autowired
	private CityResourceAssembler cityResourceAssembler;

	@Autowired
	private MenuCategoryResourceAssembler menuCategoryResourceAssembler;

	@Autowired
	private TableResourceAssembler tableResourceAssembler;

	public UserResource toResource(User entity, boolean light) {
		UserResource resource = new UserResource();
		resource.setId(entity.getId());
		resource.setOwnerId(entity.getOwnerId());
		resource.setStatus(entity.getStatus());
		resource.setRole(entity.getRole());
		resource.setUsername(entity.getUsername());
		resource.setName(entity.getName());
		resource.setAddress(entity.getAddress());
		resource.setCity((entity.getCity() != null) ? cityResourceAssembler.toResource(entity.getCity(), light) : null);
		resource.setPhone(entity.getPhone());
		resource.setMobile(entity.getMobile());
		resource.setEmail(entity.getEmail());
		resource.setCreationDate(entity.getCreationDate().toDate());
		resource.setLastModifiedDate(entity.getLastModifiedDate().toDate());
		if (!light) {
			resource.setWifiName(entity.getWifiName());
			resource.setWifiPassword(entity.getWifiPassword());
			resource.setNotes(entity.getNotes());

			if (entity.getTables() != null && !entity.getTables().isEmpty()) {
				resource.setTables(tableResourceAssembler.toResources(entity.getTables(), light));
			} else {
				resource.setTables(new ArrayList<TableResource>());
			}

			if (entity.getCategories() != null && !entity.getCategories().isEmpty()) {
				resource.setCategories(menuCategoryResourceAssembler.toResources(entity.getCategories()));
			} else {
				resource.setCategories(new ArrayList<MenuCategoryResource>());
			}

			resource.setMenuNotes(entity.getMenuNotes());
		}
		return resource;
	}

	public List<UserResource> toResources(Iterable<User> entities, boolean light) {
		List<UserResource> resources = new ArrayList<>();
		for (User entity : entities) {
			resources.add(toResource(entity, light));
		}
		return resources;
	}

	public User createEntity(UserResource resource) {
		User entity = new User();
		entity.setOwnerId(resource.getOwnerId());
		entity.setStatus((resource.getStatus() != null) ? resource.getStatus() : UserStatus.INACTIVE);
		entity.setRole(UserRole.CUSTOMER);
		entity.setUsername(resource.getUsername());
		entity.setPassword(resource.getPassword());
		entity.setName(resource.getName());
		entity.setAddress(resource.getAddress());
		entity.setCity((resource.getCity() != null) ? cityRepository.findOne(resource.getCity().getId()) : null);
		entity.setPhone(resource.getPhone());
		entity.setMobile(resource.getMobile());
		entity.setEmail(resource.getEmail());
		entity.setWifiName(resource.getWifiName());
		entity.setWifiPassword(resource.getWifiPassword());
		entity.setNotes(resource.getNotes());

		processTables(entity, resource);
		processCategories(entity, resource);

		entity.setMenuNotes(resource.getMenuNotes());

		return entity;
	}

	public User updateEntity(User entity, UserResource resource) {
		entity.setStatus((resource.getStatus() != null) ? resource.getStatus() : UserStatus.INACTIVE);
		entity.setRole(resource.getRole());
		entity.setUsername(resource.getUsername());
		if (StringUtils.isNotBlank(resource.getPassword())) {
			entity.setPassword(resource.getPassword());
		}
		entity.setName(resource.getName());
		entity.setAddress(resource.getAddress());
		entity.setCity((resource.getCity() != null) ? cityRepository.findOne(resource.getCity().getId()) : null);
		entity.setPhone(resource.getPhone());
		entity.setMobile(resource.getMobile());
		entity.setEmail(resource.getEmail());
		entity.setWifiName(resource.getWifiName());
		entity.setWifiPassword(resource.getWifiPassword());
		entity.setNotes(resource.getNotes());

		processTables(entity, resource);
		processCategories(entity, resource);

		entity.setMenuNotes(resource.getMenuNotes());

		return entity;
	}

	private void processTables(User entity, UserResource resource) {
		if (entity.getTables() == null) {
			entity.setTables(new HashSet<>());
		}
		entity.getTables().clear();
		if (resource.getTables() != null && !resource.getTables().isEmpty()) {
			Set<Table> tables = tableResourceAssembler.toEntities(resource.getTables());
			for (Table table : tables) {
				table.setUser(entity);
				entity.getTables().add(table);
			}
		}
	}

	private void processCategories(User entity, UserResource resource) {
		if (entity.getCategories() == null) {
			entity.setCategories(new HashSet<>());
		}
		entity.getCategories().clear();
		if (resource.getCategories() != null && !resource.getCategories().isEmpty()) {
			Set<MenuCategory> categories = menuCategoryResourceAssembler.toEntities(resource.getCategories());
			for (MenuCategory category : categories) {
				category.setUser(entity);
				entity.getCategories().add(category);
			}
		}
	}

}
