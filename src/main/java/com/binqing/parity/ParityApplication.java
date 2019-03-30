package com.binqing.parity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
//jsp 相关 使用时将Login、forgetpassword、modify的url更改
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class ParityApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(ParityApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(ParityApplication.class);
	}


}

