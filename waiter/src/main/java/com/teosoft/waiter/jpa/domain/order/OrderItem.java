package com.teosoft.waiter.jpa.domain.order;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.teosoft.waiter.jpa.AbstractEntity;
import com.teosoft.waiter.jpa.domain.user.MenuItem;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "_order_item")
@NoArgsConstructor @Getter @Setter @ToString
public class OrderItem extends AbstractEntity {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "order_id", nullable = false)
	private Order order;

	@ManyToOne
	@JoinColumn(name = "item_id", nullable = false)
	private MenuItem item;

	@Column(name = "quantity", nullable = false)
	private Integer quantity;

}

