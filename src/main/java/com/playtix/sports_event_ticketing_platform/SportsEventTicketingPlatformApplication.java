package com.playtix.sports_event_ticketing_platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SportsEventTicketingPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(SportsEventTicketingPlatformApplication.class, args);
	}

}
