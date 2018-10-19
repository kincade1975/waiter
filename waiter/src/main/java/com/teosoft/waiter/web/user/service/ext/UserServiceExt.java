package com.teosoft.waiter.web.user.service.ext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.teosoft.waiter.jpa.domain.user.MenuCategory;
import com.teosoft.waiter.jpa.domain.user.MenuItem;
import com.teosoft.waiter.jpa.domain.user.Table;
import com.teosoft.waiter.jpa.domain.user.TableRepository;
import com.teosoft.waiter.jpa.domain.user.User;
import com.teosoft.waiter.jpa.domain.user.User.UserStatus;
import com.teosoft.waiter.web.WebBaseService;
import com.teosoft.waiter.web.user.resource.ext.MenuCategoryResourceExt;
import com.teosoft.waiter.web.user.resource.ext.MenuItemResourceExt;
import com.teosoft.waiter.web.user.resource.ext.UserResourceExt;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserServiceExt extends WebBaseService {

	@Autowired
	private TableRepository tableRepository;

	public ResponseEntity<?> find(String code) {
		Table table = tableRepository.findByCode(code);

		if (table == null) {
			log.warn("Table with code [{}] not found", code);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		User user = table.getUser();

		// validate receiver
		if (user.getStatus() == UserStatus.INACTIVE) {
			return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
		}

		UserResourceExt result = new UserResourceExt();
		result.setName(user.getName());
		result.setAddress(user.getAddress());
		result.setCity((user.getCity() != null) ? user.getCity().getName() : null);
		result.setPhone(user.getPhone());
		result.setMobile(user.getMobile());
		result.setEmail(user.getEmail());
		result.setWifiName(user.getWifiName());
		result.setWifiPassword(user.getWifiPassword());
		result.setMenuNotes(user.getMenuNotes());

		for (MenuCategory category : user.getCategories()) {
			MenuCategoryResourceExt categoryResource = new MenuCategoryResourceExt();
			categoryResource.setName(category.getName());
			result.getCategories().add(categoryResource);
			for (MenuItem item : category.getItems()) {
				MenuItemResourceExt itemResource = new MenuItemResourceExt();
				itemResource.setId(item.getId());
				itemResource.setName(item.getName());
				itemResource.setDescription(item.getDescription());
				itemResource.setUnit(item.getUnit());
				itemResource.setPrice(item.getPrice());
				categoryResource.getItems().add(itemResource);
			}
		}

		return new ResponseEntity<>(result, HttpStatus.OK);
	}

}