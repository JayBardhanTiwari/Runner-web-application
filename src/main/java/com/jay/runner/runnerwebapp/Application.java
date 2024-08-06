package com.jay.runner.runnerwebapp;

import java.lang.reflect.AccessFlag.Location;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.jay.runner.runnerwebapp.run.Run;

@SpringBootApplication
public class Application {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	CommandLineRunner runner(){
		
		return args ->{
			Run run = new Run(1, "first run", LocalDateTime.now(), LocalDateTime.now().plus(1, ChronoUnit.HOURS), 8,Location.OUTDOOR);
			log.info(run + "run");
		}
	}

}
