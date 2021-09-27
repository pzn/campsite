# campsite
[![CI](https://github.com/pzn/campsite/actions/workflows/ci.yaml/badge.svg)](https://github.com/pzn/campsite/actions/workflows/ci.yaml)

## Architecture
The application is:
- Using Java 11
- Built using [Quarkus Framework](https://quarkus.io), following as much as possible Quarkus best practices
- Using Postgres v13 as a database
- Flyway for database schema migration
- Swagger/OpenAPI v3, [leveraging out-of-the-box Quarkus functionalities](https://quarkus.io/guides/openapi-swaggerui)
- Using [Testcontainers](https://www.testcontainers.org) for tests in order to spawn a test Postgres database (you will require Docker to run them)

## OpenAPI v3 / Swagger UI

Start the application (see below) and go to:
- `<baseUrl>/q/openapi` to retrieve the OpenAPI v3 Specifications
- `<baseUrl>/q/swagger-ui` to browse it through an embedded Swagger UI

Or visit the Heroku deployment (free tier, may take a few seconds to warm up):
- `https://sheltered-bayou-62697.herokuapp.com/q/openapi` to retrieve the OpenAPI v3 Specifications
- `https://sheltered-bayou-62697.herokuapp.com/q/swagger-ui` to browse it through an embedded Swagger UI

## Business considerations
Apart from the guideline given by the assignment, a few additional rules have been added to the API.

### `arrival` is inclusive whereas `departure` is exclusive
Whenever the API expects an `arrival` date, it will be the first day of the booking (check-in).

Whenever the API expects a `departure` date, it will be the end of the booking, which the reservation will end: the guest won't occupy that day (check-out).

### A reservation can only be made in the future
When placing a new reservation OR modifying an existing reservation, both `arrival` and `departure` dates must be in the future.

### Only upcoming reservations can be cancelled
A past reservation cannot be cancelled.

## Technical/design explanations

### Data model

Two SQL tables have been created:
- `bookings` (Java entity: `BookingEntity`): represents a reservation
  - Note: `email` is not unique because we want to keep past reservations, as described in [Business considerations](#business-considerations)
- `booking_locks` (Java entity: `BookingLockEntity`): represents a day being booked by a `bookings` record
  - `booking_day` has a *UNIQUE* constraint
  - `booking_locks` has a strong dependency (foreign key) on `bookings`

#### Booking concurrency concerns

When the application wants to either create or update a booking:
- Checks in `bookings` if the requested dates are available
- If available, we create or update `BookingEntity`
- We also create corresponding `booking_locks` records
- Once we commit the transaction
  - It will succeed if both `bookings` and `booking_locks` records have been successfully inserted/updated by the database
  - It will fail if `booking_locks` records cannot be inserted

=> This is how the application manages booking concurrencies, and avoid booking the same day for multiple reservations.

In a nutshell, `booking_locks` is used a locking mechanism whenever we attempt to create or update a `bookings` record. Apart from that,
its content is irrelevant. That is why a scheduled job named `ClearBookingLocksJob.java` as been created to empty it automatically.

#### API errors
When an error occurs, either from a malformed user request of a backend issue, the API will return the following JSON payload:
```json
{
  "errorCode": "APIx004",
  "message": "Reservation does not to exist"
}
```

Error codes are related to one and only situation in the application, which may be really useful for debugging/investigation purposes.

All errors are captured by an `ExceptionMapper` (equivalent of a `@ControllerAdvice` in Spring) in order to return consistent API errors.

### Testing
The Quarkus framework is pretty fast to start, in seconds. As a result, the documentation highly suggests writing as many integration tests as possible.

In the Quarkus world:
- All tests *running in the JVM* are suffixed with `*Test`
- All tests *running in a native image* are suffixed with `*IT`

Because of timing considerations, the application is expected to run in a JVM.

As a result, to follow Quarkus guidelines, all tests are suffixed with `*Test` despite all being "classic integration tests" (ie. starting the whole application).

## Improvements

- `booking_locks` table clean up
  - Instead of scheduled job running within the application, it could be extracted outside the application
    - To avoid running the same job as we have running instance
      - Could be run as a dedicated script deployed by Kubernetes (`kind: Job`)

## How to

### Run the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

You will also need a running Postgres 13 instance. Example using Docker:

```shell script
docker run --rm --detach \
    --network campsite \
    --name postgres \
    -p 5432:5432 \
    -e POSTGRES_USER=campsite \
    -e POSTGRES_PASSWORD=campsite \
    -e POSTGRES_DB=campsite \
    postgres:13-alpine
```

### Package and run the application

You will need [Docker Machine](https://docs.docker.com/machine/install-machine) installed on your machine.

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

### Package Docker image and run it

After packaging the application, you can build a Docker image:
```shell script
docker build -t campsite .
```

Then run it (it will reuse the Postgres Docker container from earlier):
```shell script
docker run --rm \
    --network campsite \
    --name campsite \
    -p 8080:8080 \
    -e QUARKUS_DATASOURCE_JDBC_URL='jdbc:postgresql://postgres:5432/campsite' \
    campsite
```
