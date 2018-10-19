package com.teosoft.waiter.jpa.domain.city;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.teosoft.waiter.jpa.AbstractEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "_city")
@NoArgsConstructor @Getter @Setter @ToString
public class City extends AbstractEntity {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name = "name", nullable = true, columnDefinition = "varchar(256)")
	private String name;

	@Column(name = "postal_code", nullable = true, columnDefinition = "varchar(32)")
	private String postalCode;

}

