package io.harman.flight_be.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
public class HealthcheckRestController {

	@GetMapping
	public ResponseEntity<String> healthcheck() {
		return new ResponseEntity<>("OK - Service is healthy", HttpStatus.OK);
	}

}
