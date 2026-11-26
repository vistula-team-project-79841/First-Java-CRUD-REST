package com.example.crudwithvaadin;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.regex.Pattern;

/**
 * REST controller providing JSON CRUD and filtering endpoints for customers.
 */
@RestController
@RequestMapping("/api/customers")
public class CustomerRestController {

	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

	private final CustomerRepository repository;

	public CustomerRestController(CustomerRepository repository) {
		this.repository = repository;
	}

	@GetMapping
	public List<Customer> getAll(@RequestParam(required = false) String name) {
		if (name == null || name.isBlank()) {
			return repository.findAll();
		}
		String query = name.trim();
		return repository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(query, query);
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getById(@PathVariable Long id) {
		return repository.findById(id)
				.<ResponseEntity<?>>map(ResponseEntity::ok)
				.orElseGet(() -> notFound(id));
	}

	@PostMapping
	public ResponseEntity<?> create(@RequestBody Customer customer) {
		String validationError = validate(customer);
		if (validationError != null) {
			return badRequest(validationError);
		}
		Customer saved = repository.save(new Customer(
				customer.getFirstName().trim(),
				customer.getLastName().trim(),
				customer.getEmail().trim()
		));
		return ResponseEntity.created(URI.create("/api/customers/" + saved.getId())).body(saved);
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Customer customer) {
		String validationError = validate(customer);
		if (validationError != null) {
			return badRequest(validationError);
		}
		return repository.findById(id)
				.<ResponseEntity<?>>map(existing -> {
					existing.setFirstName(customer.getFirstName().trim());
					existing.setLastName(customer.getLastName().trim());
					existing.setEmail(customer.getEmail().trim());
					return ResponseEntity.ok(repository.save(existing));
				})
				.orElseGet(() -> notFound(id));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		if (!repository.existsById(id)) {
			return notFound(id);
		}
		repository.deleteById(id);
		return ResponseEntity.noContent().build();
	}

	@ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
	public ResponseEntity<ApiError> handleBadInput(Exception ex) {
		return badRequest("Invalid request format or parameter value.");
	}

	private String validate(Customer customer) {
		if (customer == null) {
			return "Request body must not be empty.";
		}
		if (customer.getFirstName() == null || customer.getFirstName().isBlank()) {
			return "firstName is required and must not be blank.";
		}
		if (customer.getLastName() == null || customer.getLastName().isBlank()) {
			return "lastName is required and must not be blank.";
		}
		if (customer.getEmail() == null || customer.getEmail().isBlank()) {
			return "email is required and must not be blank.";
		}
		if (!EMAIL_PATTERN.matcher(customer.getEmail().trim()).matches()) {
			return "email must be a valid email address.";
		}
		return null;
	}

	private ResponseEntity<ApiError> badRequest(String message) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ApiError(400, "Bad Request", message, Instant.now().toString()));
	}

	private ResponseEntity<ApiError> notFound(Long id) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(new ApiError(404, "Not Found", "Customer not found with id: " + id, Instant.now().toString()));
	}

	public record ApiError(int status, String error, String message, String timestamp) {
	}
}
