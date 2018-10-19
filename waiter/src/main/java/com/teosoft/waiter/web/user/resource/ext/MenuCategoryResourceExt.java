package com.teosoft.waiter.web.user.resource.ext;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class MenuCategoryResourceExt {

	@JsonProperty("name")
	private String name;

	@JsonProperty("items")
	private List<MenuItemResourceExt> items = new ArrayList<>();

}
