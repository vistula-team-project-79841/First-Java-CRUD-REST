package com.example.crudwithvaadin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
public class CustomerRestControllerTests {

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private CustomerRepository repository;

	private MockMvc mockMvc;

	@BeforeEach
	void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
	}

	@Test
	void shouldReturnInfoWithoutAuthentication() throws Exception {
		mockMvc.perform(get("/api/info"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.studentId", is("79841")))
				.andExpect(jsonPath("$.applicationName", is("First-Java-CRUD-REST")))
				.andExpect(jsonPath("$.version", is("1.0.0")));
	}

	@Test
	void shouldReturnAllAndFilteredCustomers() throws Exception {
		mockMvc.perform(get("/api/customers"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(5))));

		mockMvc.perform(get("/api/customers").param("name", "Bauer"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)));
	}

	@Test
	void shouldCreateUpdateAndDeleteCustomer() throws Exception {
		String body = """
				{
				  "firstName": "John",
				  "lastName": "Doe",
				  "email": "john.doe@example.com"
				}
				""";

		mockMvc.perform(post("/api/customers")
						.contentType(MediaType.APPLICATION_JSON)
						.content(body))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.firstName", is("John")))
				.andExpect(jsonPath("$.lastName", is("Doe")))
				.andExpect(jsonPath("$.email", is("john.doe@example.com")));

		Customer john = repository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase("John", "John")
				.getFirst();

		mockMvc.perform(get("/api/customers/{id}", john.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.email", is("john.doe@example.com")));

		String updateBody = """
				{
				  "firstName": "John",
				  "lastName": "Smith",
				  "email": "john.smith@example.com"
				}
				""";

		mockMvc.perform(put("/api/customers/{id}", john.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(updateBody))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.lastName", is("Smith")))
				.andExpect(jsonPath("$.email", is("john.smith@example.com")));

		mockMvc.perform(delete("/api/customers/{id}", john.getId()))
				.andExpect(status().isNoContent());

		mockMvc.perform(get("/api/customers/{id}", john.getId()))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status", is(404)));
	}

	@Test
	void shouldReturnBadRequestForInvalidInputAndNotFoundForMissingCustomer() throws Exception {
		String invalidBody = """
				{
				  "firstName": "",
				  "lastName": "Doe",
				  "email": "not-an-email"
				}
				""";

		mockMvc.perform(post("/api/customers")
						.contentType(MediaType.APPLICATION_JSON)
						.content(invalidBody))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status", is(400)));

		mockMvc.perform(get("/api/customers/999999"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status", is(404)));

		mockMvc.perform(delete("/api/customers/999999"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status", is(404)));
	}

	@Test
	void shouldReturnDynamicCustomerStatistics() throws Exception {
		long initialCount = repository.count();

		mockMvc.perform(get("/api/customers/statistics"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.totalCustomers", is((int) initialCount)))
				.andExpect(jsonPath("$.contributorStudentId", is("79822")));

		String body = """
				{
				  "firstName": "Ada",
				  "lastName": "Lovelace",
				  "email": "ada.lovelace@example.com"
				}
				""";

		mockMvc.perform(post("/api/customers")
						.contentType(MediaType.APPLICATION_JSON)
						.content(body))
				.andExpect(status().isCreated());

		mockMvc.perform(get("/api/customers/statistics"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.totalCustomers", is((int) initialCount + 1)))
				.andExpect(jsonPath("$.contributorStudentId", is("79822")));
	}
}
