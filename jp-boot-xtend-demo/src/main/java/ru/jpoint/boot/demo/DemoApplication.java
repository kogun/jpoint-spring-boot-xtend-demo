package ru.jpoint.boot.demo;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import ru.jpoint.boot.demo.domain.Country;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
	
	@Bean
	InitializingBean init() {
	    return () -> {
	        Country c = new Country();
	        c.setName("Name");
	    };
	}
}
