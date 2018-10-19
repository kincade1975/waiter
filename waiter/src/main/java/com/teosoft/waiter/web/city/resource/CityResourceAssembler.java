package com.teosoft.waiter.web.city.resource;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.teosoft.waiter.jpa.domain.city.City;

@Component
public class CityResourceAssembler {

	public CityResource toResource(City entity, boolean light) {
		CityResource resource = new CityResource();
		resource.setId(entity.getId());
		resource.setName(entity.getName());
		resource.setPostalCode(entity.getPostalCode());
		resource.setCreationDate(entity.getCreationDate().toDate());
		resource.setLastModifiedDate(entity.getLastModifiedDate().toDate());
		return resource;
	}

	public List<CityResource> toResources(Iterable<City> entities, boolean light) {
		List<CityResource> resources = new ArrayList<>();
		for (City entity : entities) {
			resources.add(toResource(entity, light));
		}
		return resources;
	}

	public City createEntity(CityResource resource) {
		City entity = new City();
		entity.setName(resource.getName());
		entity.setPostalCode(resource.getPostalCode());
		return entity;
	}

	public City updateEntity(City entity, CityResource resource) {
		entity.setName(resource.getName());
		entity.setPostalCode(resource.getPostalCode());
		return entity;
	}

}
