package com.teosoft.waiter.web.city.service;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
import com.teosoft.waiter.jpa.domain.city.City;
import com.teosoft.waiter.jpa.domain.city.CityRepository;
import com.teosoft.waiter.jpa.domain.city.CitySpecification;
import com.teosoft.waiter.web.PageableResource;
import com.teosoft.waiter.web.UiGridFilterResource;
import com.teosoft.waiter.web.UiGridResource;
import com.teosoft.waiter.web.WebBaseService;
import com.teosoft.waiter.web.city.resource.CityResource;
import com.teosoft.waiter.web.city.resource.CityResourceAssembler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CityService extends WebBaseService {

	@Autowired
	private Collator collator;

	@Autowired
	private CityRepository cityRepository;

	@Autowired
	private CityResourceAssembler cityResourceAssembler;

	@Transactional(readOnly = true)
	public List<CityResource> findAll() {
		log.debug("Finding cities...");

		List<CityResource> result = cityResourceAssembler.toResources(cityRepository.findAll(), true);

		Collections.sort(result, new Comparator<CityResource>() {
			@Override
			public int compare(CityResource o1, CityResource o2) {
				return collator.compare(o1.getName(), o2.getName());
			}
		});

		return result;
	}

	@Transactional(readOnly = true)
	public CityResource find(Integer id) {
		log.debug("Finding city [{}]...", id);
		if (id == 0) {
			CityResource resource = new CityResource();
			return resource;
		}

		City entity = cityRepository.findOne(id);
		if (entity == null) {
			throw new HttpClientErrorException(HttpStatus.NOT_FOUND, String.format("City with ID [%s] not found", id));
		}

		return cityResourceAssembler.toResource(entity, false);
	}

	@Transactional
	public CityResource save(CityResource resource) {
		log.debug("Saving city [{}]...", resource);
		City entity = (resource.getId() == null) ? cityResourceAssembler.createEntity(resource) : cityResourceAssembler.updateEntity(cityRepository.findOne(resource.getId()), resource);
		return cityResourceAssembler.toResource(cityRepository.save(entity), false);
	}

	@Transactional
	public void delete(Integer id) {
		log.debug("Deleting city [{}]...", id);
		cityRepository.delete(cityRepository.findOne(id));
	}

	@Transactional(readOnly = true)
	public PageableResource<CityResource> page(UiGridResource resource) {
		log.debug("Getting page [{}]...", resource);

		Page<City> page = null;

		List<Specification<City>> specifications = new ArrayList<>();

		if (resource.getFilter() != null && !resource.getFilter().isEmpty()) {
			for (UiGridFilterResource uiGridFilterResource : resource.getFilter()) {
				CitySpecification specification = createSpecification(uiGridFilterResource);
				if (specification != null) {
					specifications.add(specification);
				}
			}
		}

		if (!specifications.isEmpty()) {
			// filtering
			Specification<City> specification = specifications.get(0);
			if (specifications.size() > 1) {
				for (int i=1; i<specifications.size(); i++) {
					specification = Specifications.where(specification).and(specifications.get(i));
				}
			}
			page = cityRepository.findAll(specification, createPageable(resource));
		} else {
			// no filtering
			page = cityRepository.findAll(createPageable(resource));
		}

		return new PageableResource<>(page.getTotalElements(), cityResourceAssembler.toResources(page.getContent(), true));
	}

	private CitySpecification createSpecification(UiGridFilterResource resource) {
		if (resource.getName().equalsIgnoreCase("id")) {
			return new CitySpecification(new SearchCriteria(resource.getName(), SearchOperation.EQUALITY, resource.getTerm()));
		} else if (resource.getName().equalsIgnoreCase("creationDate") || resource.getName().equalsIgnoreCase("lastModifiedDate")) {
			return new CitySpecification(new SearchCriteria(resource.getName(), SearchOperation.BETWEEN, getDateRange(resource.getTerm())));
		} else {
			return new CitySpecification(new SearchCriteria(resource.getName(), SearchOperation.CONTAINS, resource.getTerm()));
		}
	}

}