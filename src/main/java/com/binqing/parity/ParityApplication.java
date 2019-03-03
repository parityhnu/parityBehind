package com.binqing.parity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class ParityApplication {

	public static void main(String[] args) {
		SpringApplication.run(ParityApplication.class, args);
	}


}

