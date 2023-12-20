package com.web.clone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class CloneApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloneApplication.class, args);
	}

}
