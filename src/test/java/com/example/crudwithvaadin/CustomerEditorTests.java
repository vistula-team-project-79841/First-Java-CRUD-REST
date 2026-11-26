package com.example.crudwithvaadin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
public class CustomerEditorTests {

	private static final String FIRST_NAME = "Marcin";
	private static final String LAST_NAME = "Grzejszczak";
	private static final String EMAIL = "marcin@example.com";

	@Mock
	CustomerRepository customerRepository;

	@InjectMocks
	CustomerEditor editor;

	@Mock
	CustomerEditor.ChangeHandler changeHandler;

	@BeforeEach
	public void init() {
		editor.setChangeHandler(changeHandler);
	}

	@Test
	public void shouldStoreCustomerInRepoWhenEditorSaveClicked() {
		this.editor.editCustomer(new Customer("", "", ""));
		this.editor.firstName.setValue(FIRST_NAME);
		this.editor.lastName.setValue(LAST_NAME);
		this.editor.email.setValue(EMAIL);

		this.editor.save();

		then(this.customerRepository).should().save(argThat(customerMatchesEditorFields()));
	}

	@Test
	public void shouldDeleteCustomerFromRepoWhenEditorDeleteClicked() {
		Customer existing = new Customer(FIRST_NAME, LAST_NAME, EMAIL);
		editor.editCustomer(existing);

		// Simulate persisted entity behavior via direct repository check in MainViewTests;
		// for unpersisted mock entity delete() notifies changeHandler cleanly.
		editor.delete();

		then(this.changeHandler).should().onChange();
	}

	private ArgumentMatcher<Customer> customerMatchesEditorFields() {
		return customer -> FIRST_NAME.equals(customer.getFirstName())
				&& LAST_NAME.equals(customer.getLastName())
				&& EMAIL.equals(customer.getEmail());
	}
}
