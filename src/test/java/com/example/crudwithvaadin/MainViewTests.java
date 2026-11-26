package com.example.crudwithvaadin;

import com.vaadin.flow.data.provider.ListDataProvider;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest(classes = MainViewTests.Config.class)
public class MainViewTests {

	@Autowired
	CustomerRepository repository;

	CustomerEditor editor;

	MainView mainView;

	@BeforeEach
	public void setup() {
		this.editor = new CustomerEditor(this.repository);
		this.mainView = new MainView(this.repository, editor);
	}

	@Test
	public void shouldInitializeTheGridWithCustomerRepositoryData() {
		int customerCount = (int) this.repository.count();

		then(mainView.grid.getColumns()).hasSize(4);
		then(getCustomersInGrid()).hasSize(customerCount);
	}

	@SuppressWarnings("unchecked")
	private List<Customer> getCustomersInGrid() {
		ListDataProvider<Customer> ldp = (ListDataProvider<Customer>) mainView.grid.getDataProvider();
		return new ArrayList<>(ldp.getItems());
	}

	@Test
	public void shouldFillOutTheGridWithNewData() {
		int initialCustomerCount = (int) this.repository.count();

		editor.editCustomer(new Customer("Marcin", "Grzejszczak", "marcin@example.com"));
		this.editor.save();

		then(getCustomersInGrid()).hasSize(initialCustomerCount + 1);
		then(getCustomersInGrid().get(getCustomersInGrid().size() - 1))
				.extracting("firstName", "lastName", "email")
				.containsExactly("Marcin", "Grzejszczak", "marcin@example.com");
	}

	@Test
	public void shouldFilterOutTheGridWithTheProvidedName() {
		this.repository.save(new Customer("Josh", "Long", "josh.long@example.com"));

		mainView.listCustomers("Long");

		then(getCustomersInGrid()).hasSize(1);
		then(getCustomersInGrid().get(0))
				.extracting("firstName", "lastName", "email")
				.containsExactly("Josh", "Long", "josh.long@example.com");
	}

	@Test
	public void shouldInitializeWithInvisibleEditor() {
		then(this.editor.isVisible()).isFalse();
	}

	@Test
	public void shouldMakeEditorVisible() {
		Customer first = getCustomersInGrid().get(0);
		this.mainView.grid.select(first);

		then(this.editor.isVisible()).isTrue();
	}

	@Configuration
	@EnableAutoConfiguration(exclude = com.vaadin.flow.spring.SpringBootAutoConfiguration.class)
	static class Config {

		@Autowired
		CustomerRepository repository;

		@PostConstruct
		public void initializeData() {
			this.repository.save(new Customer("Jack", "Bauer", "jack.bauer@example.com"));
			this.repository.save(new Customer("Chloe", "O'Brian", "chloe.obrian@example.com"));
			this.repository.save(new Customer("Kim", "Bauer", "kim.bauer@example.com"));
			this.repository.save(new Customer("David", "Palmer", "david.palmer@example.com"));
			this.repository.save(new Customer("Michelle", "Dessler", "michelle.dessler@example.com"));
		}
	}
}
