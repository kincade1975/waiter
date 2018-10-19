package com.teosoft.waiter.jpa.domain.event;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.teosoft.waiter.jpa.AbstractEntity;
import com.teosoft.waiter.jpa.domain.user.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "_application_event")
@NoArgsConstructor @AllArgsConstructor @Getter @Setter @ToString
public class ApplicationEvent extends AbstractEntity {

	public enum EventType { EVENT_1, EVENT_2 };

	public enum EventLevel { CRITICAL, ERROR, WARN, INFO };

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false, columnDefinition = "varchar(64)")
	private EventType type;

	@Enumerated(EnumType.STRING)
	@Column(name = "level", nullable = false, columnDefinition = "varchar(16)")
	private EventLevel level;

	@ManyToOne
	@JoinColumn(name="user_id", nullable = true)
	private User user;

	@Column(name = "message", nullable = true, columnDefinition = "text")
	private String message;

}
