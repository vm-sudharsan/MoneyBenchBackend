package com.moneybench.MoneyBenchBackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MoneyBenchBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoneyBenchBackendApplication.class, args);
	}

}
