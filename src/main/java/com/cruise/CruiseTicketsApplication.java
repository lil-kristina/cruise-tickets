package com.cruise;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.cruise")
public class CruiseTicketsApplication {

	public static void main(String[] args) {
		SpringApplication.run(CruiseTicketsApplication.class, args);
	}
}