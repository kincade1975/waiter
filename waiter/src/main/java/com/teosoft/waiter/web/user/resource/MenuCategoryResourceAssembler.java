package com.teosoft.waiter.web.user.resource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.teosoft.waiter.jpa.domain.user.MenuCategory;
import com.teosoft.waiter.jpa.domain.user.MenuCategoryRepository;
import com.teosoft.waiter.jpa.domain.user.MenuItem;

@Component
public class MenuCategoryResourceAssembler {

	@Autowired
	private MenuCategoryRepository menuCategoryRepository;

	@Autowired
	private MenuItemResourceAssembler menuItemResourceAssembler;

	public MenuCategoryResource toResource(MenuCategory entity) {
		MenuCategoryResource resource = new MenuCategoryResource();
		resource.setId(entity.getId());
		resource.setPosition(entity.getPosition());
		resource.setName(entity.getName());
		resource.setCreationDate(entity.getCreationDate().toDate());
		resource.setLastModifiedDate(entity.getLastModifiedDate().toDate());
		if (entity.getItems() != null && !entity.getItems().isEmpty()) {
			resource.setItems(menuItemResourceAssembler.toResources(entity.getItems()));
		} else {
			resource.setItems(new ArrayList<MenuItemResource>());
		}
		return resource;
	}

	public List<MenuCategoryResource> toResources(Iterable<MenuCategory> entities) {
		List<MenuCategoryResource> resources = new ArrayList<>();
		for (MenuCategory entity : entities) {
			resources.add(toResource(entity));
		}
		return resources;
	}

	public Set<MenuCategory> toEntities(Iterable<MenuCategoryResource> resources) {
		Set<MenuCategory> entities = new LinkedHashSet<>();
		int position = 0;
		for (MenuCategoryResource resource : resources) {
			MenuCategory entity = toEntity(resource);
			entity.setPosition(position++);
			entities.add(entity);
		}
		return entities;
	}

	private MenuCategory toEntity(MenuCategoryResource resource) {
		MenuCategory entity = (resource.getId() == null) ? new MenuCategory() : menuCategoryRepository.findOne(resource.getId());
		entity.setName(resource.getName());
		processItems(entity, resource);
		return entity;
	}

	private void processItems(MenuCategory entity, MenuCategoryResource resource) {
		if (entity.getItems() == null) {
			entity.setItems(new HashSet<>());
		}
		entity.getItems().clear();
		if (resource.getItems() != null && !resource.getItems().isEmpty()) {
			Set<MenuItem> items = menuItemResourceAssembler.toEntities(resource.getItems());
			for (MenuItem item : items) {
				item.setCategory(entity);
				entity.getItems().add(item);
			}
		}
	}

}
