package com.example.crudwithvaadin;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest
public class CrudWithVaadinApplicationTests {

	@Autowired
	private CustomerRepository repository;

	@Test
	public void shouldLoadInitialCustomersOnStartup() {
		then(this.repository.count()).isGreaterThanOrEqualTo(5);
	}

	@Test
	public void shouldFindBauerCustomers() {
		then(this.repository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase("Bauer", "Bauer"))
				.hasSizeGreaterThanOrEqualTo(2);
	}
}
