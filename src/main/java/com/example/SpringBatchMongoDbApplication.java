package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
@EnableScheduling
public class SpringBatchMongoDbApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBatchMongoDbApplication.class, args);
	}
}
