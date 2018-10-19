package com.teosoft.waiter.jpa.domain.order;

import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.teosoft.waiter.jpa.SearchCriteria;
import com.teosoft.waiter.jpa.domain.user.Table;
import com.teosoft.waiter.jpa.domain.user.User;

public class OrderSpecification implements Specification<Order> {

	private SearchCriteria criteria;

	public OrderSpecification(final SearchCriteria criteria) {
		super();
		this.criteria = criteria;
	}

	public SearchCriteria getCriteria() {
		return criteria;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Predicate toPredicate(Root<Order> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
		switch (criteria.getOperation()) {
			case EQUALITY:
				if (criteria.getKey().equals("user.id")) {
					return builder.equal(root.<User> get("user").<String>get("id"), criteria.getValue());
				}
				return builder.equal(root.get(criteria.getKey()), criteria.getValue());
			case NEGATION:
				return builder.notEqual(root.get(criteria.getKey()), criteria.getValue());
			case GREATER_THAN:
				return builder.greaterThan(root.<String> get(criteria.getKey()), criteria.getValue().toString());
			case LESS_THAN:
				return builder.lessThan(root.<String> get(criteria.getKey()), criteria.getValue().toString());
			case LIKE:
				return builder.like(root.<String> get(criteria.getKey()), criteria.getValue().toString());
			case STARTS_WITH:
				return builder.like(root.<String> get(criteria.getKey()), criteria.getValue() + "%");
			case ENDS_WITH:
				return builder.like(root.<String> get(criteria.getKey()), "%" + criteria.getValue());
			case CONTAINS:
				if (criteria.getKey().equals("user.name")) {
					return builder.like(root.<User> get("user").<String>get("name"), "%" + criteria.getValue() + "%");
				} else if (criteria.getKey().equals("table.identifier")) {
					return builder.like(root.<Table> get("table").<String>get("identifier"), "%" + criteria.getValue() + "%");
				}
				return builder.like(root.<String> get(criteria.getKey()), "%" + criteria.getValue() + "%");
			case BETWEEN:
				List<Date> range = (List<Date>) criteria.getValue();
				return builder.between(root.<Date> get(criteria.getKey()), range.get(0), range.get(1));
			default:
				return null;
		}
	}

}