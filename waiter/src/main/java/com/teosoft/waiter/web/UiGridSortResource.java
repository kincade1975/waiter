package com.teosoft.waiter.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.ALWAYS)
public class UiGridSortResource {

	@JsonProperty("name")
	private String name;

	@JsonProperty("priority")
	private Integer priority;

	@JsonProperty("direction")
	private String direction;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	@Override
	public String toString() {
		return "UiGridSortResource [name=" + name + ", priority=" + priority + ", direction=" + direction + "]";
	}

}
