package com.example.roniantonius.jejakkerja;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class JejakkerjaApplication {

	public static void main(String[] args) {
		SpringApplication.run(JejakkerjaApplication.class, args);
	}

}
