package com.links86.spider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TycApplication {

	public static void main(String[] args) {
		SpringApplication.run(TycApplication.class, args);
	}
}
