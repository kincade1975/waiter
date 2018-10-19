package com.teosoft.waiter;

import java.text.Collator;
import java.util.Locale;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.system.ApplicationPidFileWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.teosoft.waiter.jpa.SpringSecurityAuditorAware;
import com.zaxxer.hikari.HikariDataSource;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware")
@EnableTransactionManagement
public class Application {

	public static void main(String[] args) {
		SpringApplication springApplication = new SpringApplication(Application.class);
		springApplication.addListeners(new ApplicationPidFileWriter());
		springApplication.run(args);
	}

	@Bean
	@Primary
	@ConfigurationProperties(prefix="spring.datasource")
	public HikariDataSource primaryDataSource() {
		return (HikariDataSource) DataSourceBuilder.create().build();
	}

	@Bean
	AuditorAware<String> springSecurityAuditorAware() {
		return new SpringSecurityAuditorAware();
	}

	@Bean
	public Collator collator() {
		return Collator.getInstance(new Locale("hr"));
	}

}
