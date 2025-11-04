package io.harman.flight_be;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import io.harman.flight_be.config.DummyDataGenerator;

@SpringBootApplication
public class FlightBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlightBeApplication.class, args);
	}

	@Bean
	@Profile("!test")
	public CommandLineRunner createDummyData(DummyDataGenerator dummyDataGenerator) {
		return args -> dummyDataGenerator.generate();
	}
}
