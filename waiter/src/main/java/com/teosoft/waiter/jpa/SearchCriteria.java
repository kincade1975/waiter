package com.teosoft.waiter.jpa;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor @AllArgsConstructor
public class SearchCriteria {

	private String key;

	private SearchOperation operation;

	private Object value;

}
