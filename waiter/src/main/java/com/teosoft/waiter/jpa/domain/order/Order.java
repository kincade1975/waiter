package com.teosoft.waiter.jpa.domain.order;

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
import javax.persistence.Table;

import com.teosoft.waiter.jpa.AbstractEntity;
import com.teosoft.waiter.jpa.domain.user.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "_order")
@NoArgsConstructor @Getter @Setter @ToString
public class Order extends AbstractEntity {

	public enum OrderType { ORDER, CALL_WAITER, REQUEST_BILL };

	public enum OrderStatus { PENDING, CLOSED, CANCELLED };

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false, columnDefinition = "varchar(16)")
	private OrderType type;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, columnDefinition = "varchar(16)")
	private OrderStatus status;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne
	@JoinColumn(name = "table_id", nullable = false)
	private com.teosoft.waiter.jpa.domain.user.Table table;

	@Column(name = "sender_uuid", nullable = true, columnDefinition = "varchar(64)")
	private String senderUuid;

	@Column(name = "sender_serial", nullable = true, columnDefinition = "varchar(64)")
	private String senderSerial;

	@Column(name = "sender_platform", nullable = true, columnDefinition = "varchar(128)")
	private String senderPlatform;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "order", orphanRemoval = true)
	private Set<OrderItem> items;

	@Column(name = "hash_code")
	private Integer hashCode;

}

