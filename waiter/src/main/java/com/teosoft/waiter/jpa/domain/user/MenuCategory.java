package com.teosoft.waiter.jpa.domain.user;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.teosoft.waiter.jpa.AbstractEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "_menu_category")
@NoArgsConstructor @Getter @Setter @ToString
public class MenuCategory extends AbstractEntity {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@Column(name = "position", nullable = false)
	private Integer position;

	@Column(name = "name", nullable = false, columnDefinition = "varchar(512)")
	private String name;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "category", orphanRemoval = true)
	@OrderBy("position")
	private Set<MenuItem> items;

}

