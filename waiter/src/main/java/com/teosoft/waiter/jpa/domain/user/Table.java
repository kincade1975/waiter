package com.teosoft.waiter.jpa.domain.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.teosoft.waiter.jpa.AbstractEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@javax.persistence.Table(name = "_table")
@NoArgsConstructor @Getter @Setter @ToString
public class Table extends AbstractEntity {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@Column(name = "position", nullable = false)
	private Integer position;

	@Column(name = "identifier", nullable = false, columnDefinition = "varchar(32)")
	private String identifier;

	@Column(name = "description", nullable = true, columnDefinition = "text")
	private String description;

	@Column(name = "code", nullable = true, columnDefinition = "varchar(8)")
	private String code;

}

