package com.example.crudwithvaadin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerStatisticsController {

	private final CustomerRepository repository;

	public CustomerStatisticsController(CustomerRepository repository) {
		this.repository = repository;
	}

	@GetMapping("/api/customers/statistics")
	public CustomerStatistics getStatistics() {
		return new CustomerStatistics(repository.count(), "79822");
	}

	public record CustomerStatistics(long totalCustomers, String contributorStudentId) {
	}
}