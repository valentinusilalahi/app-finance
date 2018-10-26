package com.silalahi.valentinus.finance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.thymeleaf.dialect.springdata.SpringDataDialect;
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;

import com.silalahi.valentinus.finance.service.RunningNumberService;

import nz.net.ultraq.thymeleaf.LayoutDialect;

@SpringBootApplication
@EnableScheduling
@EnableKafka
public class AppFinanceApplication implements CommandLineRunner {

	public static final Logger LOGGER = LoggerFactory.getLogger(AppFinanceApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(AppFinanceApplication.class, args);
	}

	@Autowired
	private RunningNumberService runningNumberService;

	@Override
	public void run(String... args) throws Exception {
		LOGGER.debug("Inisialisasi running number");
		runningNumberService.getNumber();
	}

	@Bean
	public SpringSecurityDialect springSecurityDialect() {
		return new SpringSecurityDialect();
	}

	@Bean
	public SpringDataDialect springDataDialect() {
		return new SpringDataDialect();
	}

	@Bean
	public LayoutDialect layoutDialect() {
		return new LayoutDialect();
	}

}
