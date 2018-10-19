package com.teosoft.waiter.web;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.ALWAYS)
public class UiGridResource {

	@JsonProperty("pagination")
	private UiGridPaginationResource pagination;

	@JsonProperty("sort")
	private List<UiGridSortResource> sort;

	@JsonProperty("filter")
	private List<UiGridFilterResource> filter;

	public UiGridPaginationResource getPagination() {
		return pagination;
	}

	public void setPagination(UiGridPaginationResource pagination) {
		this.pagination = pagination;
	}

	public List<UiGridSortResource> getSort() {
		return sort;
	}

	public void setSort(List<UiGridSortResource> sort) {
		this.sort = sort;
	}

	public List<UiGridFilterResource> getFilter() {
		return filter;
	}

	public void setFilter(List<UiGridFilterResource> filter) {
		this.filter = filter;
	}

	@Override
	public String toString() {
		return "UiGridResource [pagination=" + pagination + ", sort=" + sort + ", filter=" + filter + "]";
	}

}
