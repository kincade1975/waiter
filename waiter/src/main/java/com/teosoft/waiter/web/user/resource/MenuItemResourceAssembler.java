package com.teosoft.waiter.web.user.resource;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.teosoft.waiter.jpa.domain.user.MenuItem;
import com.teosoft.waiter.jpa.domain.user.MenuItemRepository;

@Component
public class MenuItemResourceAssembler {

	@Autowired
	private MenuItemRepository menuItemRepository;

	public MenuItemResource toResource(MenuItem entity) {
		MenuItemResource resource = new MenuItemResource();
		resource.setId(entity.getId());
		resource.setPosition(entity.getPosition());
		resource.setName(entity.getName());
		resource.setUnit(entity.getUnit());
		resource.setPrice(entity.getPrice());
		resource.setCreationDate(entity.getCreationDate().toDate());
		resource.setLastModifiedDate(entity.getLastModifiedDate().toDate());
		return resource;
	}

	public List<MenuItemResource> toResources(Iterable<MenuItem> entities) {
		List<MenuItemResource> resources = new ArrayList<>();
		for (MenuItem entity : entities) {
			resources.add(toResource(entity));
		}
		return resources;
	}

	private MenuItem toEntity(MenuItemResource resource) {
		MenuItem entity = (resource.getId() == null) ? new MenuItem() : menuItemRepository.findOne(resource.getId());
		entity.setName(resource.getName());
		entity.setUnit(resource.getUnit());
		entity.setDescription(resource.getDescription());
		entity.setPrice(resource.getPrice());
		return entity;
	}

	public Set<MenuItem> toEntities(Iterable<MenuItemResource> resources) {
		Set<MenuItem> entities = new LinkedHashSet<>();
		int position = 0;
		for (MenuItemResource resource : resources) {
			MenuItem entity = toEntity(resource);
			entity.setPosition(position++);
			entities.add(entity);
		}
		return entities;
	}

}
