package com.example.crudwithvaadin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import org.springframework.util.StringUtils;

/**
 * Primary Vaadin graphical interface operating on the shared CustomerRepository.
 */
@Route
public class MainView extends VerticalLayout {

	private final CustomerRepository repo;

	private final CustomerEditor editor;

	final Grid<Customer> grid;

	final TextField filter;

	private final Button addNewBtn;

	private final Button refreshBtn;

	public MainView(CustomerRepository repo, CustomerEditor editor) {
		this.repo = repo;
		this.editor = editor;
		this.grid = new Grid<>(Customer.class);
		this.filter = new TextField();
		this.addNewBtn = new Button("New customer", VaadinIcon.PLUS.create());
		this.refreshBtn = new Button("Refresh", VaadinIcon.REFRESH.create());

		H2 heading = new H2("Customer Management System");
		Span subtitle = new Span("Student ID: 79841 | Shared JPA data with REST API (/api/customers, /api/info)");

		HorizontalLayout actions = new HorizontalLayout(filter, addNewBtn, refreshBtn);
		add(heading, subtitle, actions, grid, editor);

		grid.setHeight("340px");
		grid.setColumns("id", "firstName", "lastName", "email");
		grid.getColumnByKey("id").setWidth("70px").setFlexGrow(0);

		filter.setPlaceholder("Filter by name");
		filter.setClearButtonVisible(true);
		filter.setValueChangeMode(ValueChangeMode.LAZY);
		filter.addValueChangeListener(e -> listCustomers(e.getValue()));

		grid.asSingleSelect().addValueChangeListener(e -> editor.editCustomer(e.getValue()));

		addNewBtn.addClickListener(e -> editor.editCustomer(new Customer("", "", "")));
		refreshBtn.addClickListener(e -> listCustomers(filter.getValue()));

		editor.setChangeHandler(() -> {
			editor.setVisible(false);
			listCustomers(filter.getValue());
		});

		listCustomers(null);
	}

	void listCustomers(String filterText) {
		if (StringUtils.hasText(filterText)) {
			String query = filterText.trim();
			grid.setItems(repo.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(query, query));
		} else {
			grid.setItems(repo.findAll());
		}
	}
}
