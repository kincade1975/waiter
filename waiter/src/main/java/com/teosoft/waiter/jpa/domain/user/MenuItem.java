package com.teosoft.waiter.jpa.domain.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.teosoft.waiter.jpa.AbstractEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "_menu_item")
@NoArgsConstructor @Getter @Setter @ToString
public class MenuItem extends AbstractEntity {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "category_id")
	private MenuCategory category;

	@Column(name = "position", nullable = false)
	private Integer position;

	@Column(name = "name", nullable = false, columnDefinition = "varchar(512)")
	private String name;

	@Column(name = "unit", nullable = true, columnDefinition = "varchar(256)")
	private String unit;

	@Column(name = "description", nullable = true, columnDefinition = "text")
	private String description;

	@Column(name = "price", nullable = true)
	private Double price;

}

