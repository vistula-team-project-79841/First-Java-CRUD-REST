package com.example.crudwithvaadin;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Vaadin editor form for creating, updating, and deleting Customer entities.
 */
@SpringComponent
@UIScope
public class CustomerEditor extends VerticalLayout implements KeyNotifier {

	private final CustomerRepository repository;

	private Customer customer;

	TextField firstName = new TextField("First name");
	TextField lastName = new TextField("Last name");
	TextField email = new TextField("Email");

	Button save = new Button("Save", VaadinIcon.CHECK.create());
	Button cancel = new Button("Cancel");
	Button delete = new Button("Delete", VaadinIcon.TRASH.create());
	HorizontalLayout actions = new HorizontalLayout(save, cancel, delete);

	Binder<Customer> binder = new Binder<>(Customer.class);
	private ChangeHandler changeHandler;

	@Autowired
	public CustomerEditor(CustomerRepository repository) {
		this.repository = repository;

		HorizontalLayout fields = new HorizontalLayout(firstName, lastName, email);
		add(fields, actions);

		binder.forField(firstName)
				.asRequired("First name is required")
				.bind(Customer::getFirstName, Customer::setFirstName);
		binder.forField(lastName)
				.asRequired("Last name is required")
				.bind(Customer::getLastName, Customer::setLastName);
		binder.forField(email)
				.asRequired("Email is required")
				.withValidator(v -> v != null && v.trim().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"),
						"Enter a valid email address")
				.bind(Customer::getEmail, Customer::setEmail);

		setSpacing(true);

		save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		delete.addThemeVariants(ButtonVariant.LUMO_ERROR);

		addKeyPressListener(Key.ENTER, e -> save());

		save.addClickListener(e -> save());
		delete.addClickListener(e -> delete());
		cancel.addClickListener(e -> editCustomer(customer));
		setVisible(false);
	}

	void delete() {
		if (customer != null && customer.getId() != null) {
			repository.delete(customer);
		}
		if (changeHandler != null) {
			changeHandler.onChange();
		}
	}

	void save() {
		if (customer == null) {
			return;
		}
		if (!binder.validate().isOk()) {
			if (UI.getCurrent() != null) {
				Notification.show("Please enter valid first name, last name, and email.");
			}
			return;
		}
		repository.save(customer);
		if (changeHandler != null) {
			changeHandler.onChange();
		}
	}

	public interface ChangeHandler {
		void onChange();
	}

	public final void editCustomer(Customer c) {
		if (c == null) {
			setVisible(false);
			return;
		}
		final boolean persisted = c.getId() != null;
		if (persisted) {
			customer = repository.findById(c.getId()).orElse(c);
		} else {
			customer = c;
		}
		cancel.setVisible(persisted);
		binder.setBean(customer);
		setVisible(true);
		firstName.focus();
	}

	public void setChangeHandler(ChangeHandler h) {
		changeHandler = h;
	}
}
