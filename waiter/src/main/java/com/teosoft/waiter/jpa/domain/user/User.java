package com.teosoft.waiter.jpa.domain.user;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.teosoft.waiter.jpa.AbstractEntity;
import com.teosoft.waiter.jpa.domain.city.City;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "_user")
@NoArgsConstructor @Getter @Setter @ToString
public class User extends AbstractEntity {

	public enum UserStatus { ACTIVE, INACTIVE };

	public enum UserRole { SUPERADMIN, ADMIN, CUSTOMER };

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name = "owner_id", nullable = false)
	private Integer ownerId;

	@Column(name = "username", nullable = false, columnDefinition = "varchar(128)")
	private String username;

	@Column(name = "password", nullable = true, columnDefinition = "varchar(128)")
	private String password;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, columnDefinition = "varchar(16)")
	private UserStatus status;

	@Enumerated(EnumType.STRING)
	@Column(name = "role", nullable = false, columnDefinition = "varchar(16)")
	private UserRole role;

	@Column(name = "name", nullable = true, columnDefinition = "varchar(256)")
	private String name;

	@Column(name = "address", nullable = true, columnDefinition = "varchar(512)")
	private String address;

	@ManyToOne
	@JoinColumn(name = "city_id", nullable = true)
	private City city;

	@Column(name = "phone", nullable = true, columnDefinition = "varchar(128)")
	private String phone;

	@Column(name = "mobile", nullable = true, columnDefinition = "varchar(128)")
	private String mobile;

	@Column(name = "email", nullable = true, columnDefinition = "varchar(256)")
	private String email;

	@Column(name = "wifi_name", nullable = true, columnDefinition = "varchar(128)")
	private String wifiName;

	@Column(name = "wifi_password", nullable = true, columnDefinition = "varchar(128)")
	private String wifiPassword;

	@Column(name = "notes", nullable = true, columnDefinition = "text")
	private String notes;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
	@OrderBy("position")
	private Set<com.teosoft.waiter.jpa.domain.user.Table> tables;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
	@OrderBy("position")
	private Set<MenuCategory> categories;

	@Column(name = "menu_notes", nullable = true, columnDefinition = "text")
	private String menuNotes;

}

