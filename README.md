# First Java CRUD REST

Spring Boot and Vaadin application for managing customers through a graphical interface and a JSON REST API. Both interfaces use the same JPA repository and the same H2 database.

- Student ID: `79841`
- Application name: `First-Java-CRUD-REST`
- Version: `1.0.0`
- Java target: `21`

## Run the application

Requirements:

- Java 21 or newer
- No separate Maven installation is needed because the Maven Wrapper is included

```bash
./mvnw test
./mvnw spring-boot:run
```

Open the Vaadin interface at [http://localhost:8080](http://localhost:8080). The application uses an in-memory H2 database and adds five sample customers when the database is empty.

## REST API

| Method | Endpoint | Success status | Description |
| --- | --- | --- | --- |
| `GET` | `/api/customers` | `200 OK` | Return all customers |
| `GET` | `/api/customers/{id}` | `200 OK` | Return one customer |
| `GET` | `/api/customers?name=John` | `200 OK` | Filter by first or last name, ignoring case |
| `POST` | `/api/customers` | `201 Created` | Create a customer |
| `PUT` | `/api/customers/{id}` | `200 OK` | Update a customer |
| `DELETE` | `/api/customers/{id}` | `204 No Content` | Delete a customer |
| `GET` | `/api/info` | `200 OK` | Return the student ID, application name, and version |
| `GET` | `/api/customers/statistics` | `200 OK` | Return a dynamic customer count after the partner contribution is merged |

Invalid JSON or customer data returns `400 Bad Request`. A request for a missing customer returns `404 Not Found`.

## Example requests

List and filter customers:

```bash
curl http://localhost:8080/api/customers
curl "http://localhost:8080/api/customers?name=Bauer"
curl http://localhost:8080/api/customers/1
```

Create a customer:

```bash
curl -i -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -d '{"firstName":"John","lastName":"Doe","email":"john.doe@example.com"}'
```

Update customer `6`:

```bash
curl -i -X PUT http://localhost:8080/api/customers/6 \
  -H "Content-Type: application/json" \
  -d '{"firstName":"John","lastName":"Smith","email":"john.smith@example.com"}'
```

Delete customer `6`:

```bash
curl -i -X DELETE http://localhost:8080/api/customers/6
```

Read application information:

```bash
curl http://localhost:8080/api/info
```

## Test evidence

The screenshots in [`doc`](doc) were captured from the running local application and live HTTP requests:

- [`01-application-running.png`](doc/01-application-running.png): Vaadin interface using the shared customer data
- [`02-get-filter-info.png`](doc/02-get-filter-info.png): application info, customer list, and name filter
- [`03-post-put-delete.png`](doc/03-post-put-delete.png): successful create, read, update, and delete requests
- [`04-error-responses.png`](doc/04-error-responses.png): `400 Bad Request` and `404 Not Found`

The statistics screenshot will be added only after the partner's Pull Request has been reviewed and merged.

## Collaboration Pull Requests

- Contribution by student `79841` to the partner repository: local branch `feature-statistics` is prepared. The Pull Request link will be added after it is opened and merged by the partner.
- Contribution by student `79822` to this repository: no Pull Request is currently available. It must be genuinely authored by the partner, reviewed, and merged before submission.

Both merged Pull Request links and the statistics screenshot are required before this repository is ready for Platon submission.

## Code guide

- `Customer` is the JPA entity stored in H2.
- `CustomerRepository` provides CRUD operations and the derived name-filter query.
- `CustomerRestController` maps HTTP requests to repository operations and returns the required status codes.
- `InfoRestController` exposes application metadata from configuration.
- `MainView` and `CustomerEditor` implement the Vaadin interface.
- `CrudWithVaadinApplication` starts Spring Boot and inserts sample data when the database is empty.

See [`ORAL-ASSESSMENT.md`](ORAL-ASSESSMENT.md) for a code walkthrough and practical assessment notes.
