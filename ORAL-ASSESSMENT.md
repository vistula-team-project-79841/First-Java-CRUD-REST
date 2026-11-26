# Oral and practical assessment guide

## Explain the architecture

The application has four connected parts:

1. `Customer` defines the data model and becomes an H2 table through JPA.
2. `CustomerRepository` extends `JpaRepository<Customer, Long>`. Spring supplies the CRUD implementation at runtime.
3. `CustomerRestController` and the Vaadin classes both receive the same repository through constructor injection.
4. H2 stores the data in memory. Restarting the application creates a fresh database and reloads the five sample customers.

The most important point is that the REST API and the graphical interface do not keep separate customer lists. They call the same `CustomerRepository`, so a change made through the API becomes visible in the UI after a refresh.

## Follow one REST request

For `POST /api/customers`:

1. Spring maps the JSON request body to a `Customer` object.
2. `CustomerRestController.create` checks the name and email fields.
3. The controller creates a new entity without trusting any incoming ID.
4. `repository.save` inserts the entity through JPA and Hibernate.
5. The controller returns `201 Created`, the saved JSON object, and a `Location` header.

For `GET /api/customers/{id}`, `repository.findById` returns an `Optional`. The controller returns the customer with `200 OK` or an error body with `404 Not Found`.

For `DELETE /api/customers/{id}`, the controller checks that the row exists, deletes it, and returns `204 No Content`. The empty body is intentional for status `204`.

## Explain filtering

The optional `name` query parameter is handled by `getAll`. A missing or blank value returns every customer. A non-blank value calls:

```java
findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(query, query)
```

Spring Data reads this method name and creates a query that searches both first and last names without case sensitivity.

## Explain validation and errors

The REST controller rejects blank names, blank emails, and invalid email formats. It returns a structured JSON error with status, error name, message, and timestamp. Malformed JSON and an ID with the wrong type are handled as `400 Bad Request`.

The Vaadin editor performs similar checks with a `Binder`. REST validation and UI validation are separate because an API client can call the server without using the UI.

## Explain `/api/info`

`InfoRestController` reads `app.student-id`, `spring.application.name`, and `app.version` from `application.properties`. It returns an `AppInfo` record, which Spring serializes to JSON.

## Explain the partner statistics endpoint

`CustomerStatisticsController` (contributed by student `79822` in Pull Request #1) exposes `GET /api/customers/statistics`. Its `totalCustomers` count comes dynamically from `repository.count()`, so it changes whenever customers are created or deleted. `contributorStudentId` identifies the partner (`"79822"`) who authored that Pull Request.

Similarly, the endpoint contributed to the partner repository by student `79841` identifies `"79841"`. Both merged Pull Requests are visible in the GitHub Pull Request history.

## Files to locate during the assessment

- Entity: `src/main/java/com/example/crudwithvaadin/Customer.java`
- Repository: `src/main/java/com/example/crudwithvaadin/CustomerRepository.java`
- REST CRUD: `src/main/java/com/example/crudwithvaadin/CustomerRestController.java`
- Statistics endpoint (partner PR): `src/main/java/com/example/crudwithvaadin/CustomerStatisticsController.java`
- Application info: `src/main/java/com/example/crudwithvaadin/InfoRestController.java`
- UI list and filter: `src/main/java/com/example/crudwithvaadin/MainView.java`
- UI form: `src/main/java/com/example/crudwithvaadin/CustomerEditor.java`
- Startup and sample data: `src/main/java/com/example/crudwithvaadin/CrudWithVaadinApplication.java`
- REST tests: `src/test/java/com/example/crudwithvaadin/CustomerRestControllerTests.java`

## Practical changes to practise without AI

1. Add a new customer field such as `phoneNumber` and update the entity, API JSON, UI grid, editor, and tests.
2. Add an email filter using a new repository method and a request parameter.
3. Change a successful update from `200 OK` with JSON to `204 No Content`, then update the test.
4. Add a test that proves the statistics count changes after creating a customer.
5. Diagnose a `404` by checking the URL mapping, path variable, and database ID.

## Commands to remember

```bash
./mvnw test
./mvnw spring-boot:run
curl -i http://localhost:8080/api/customers
curl -i http://localhost:8080/api/info
```
