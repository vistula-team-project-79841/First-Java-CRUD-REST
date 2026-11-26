package com.example.crudwithvaadin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Spring Boot entry point for First-Java-CRUD-REST.
 */
@SpringBootApplication
public class CrudWithVaadinApplication {

	private static final Logger log = LoggerFactory.getLogger(CrudWithVaadinApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(CrudWithVaadinApplication.class, args);
	}

	@Bean
	public CommandLineRunner loadData(CustomerRepository repository) {
		return args -> {
			if (repository.count() == 0) {
				repository.save(new Customer("Jack", "Bauer", "jack.bauer@example.com"));
				repository.save(new Customer("Chloe", "O'Brian", "chloe.obrian@example.com"));
				repository.save(new Customer("Kim", "Bauer", "kim.bauer@example.com"));
				repository.save(new Customer("David", "Palmer", "david.palmer@example.com"));
				repository.save(new Customer("Michelle", "Dessler", "michelle.dessler@example.com"));
			}

			log.info("Customers found with findAll():");
			log.info("-------------------------------");
			for (Customer customer : repository.findAll()) {
				log.info(customer.toString());
			}
			log.info("");
		};
	}
}
