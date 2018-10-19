package com.teosoft.waiter.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.ALWAYS)
public class UiGridFilterResource {

	@JsonProperty("name")
	private String name;

	@JsonProperty("term")
	private String term;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	@Override
	public String toString() {
		return "UiGridFilterResource [name=" + name + ", term=" + term + "]";
	}

}
