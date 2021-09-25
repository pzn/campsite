package io.github.pzn.campsite.api.resource;

import static io.github.pzn.campsite.booking.error.CampsiteErrorCode.RESERVATION_IN_THE_PAST;
import static io.github.pzn.campsite.booking.error.CampsiteErrorCode.RESERVATION_NOT_FOUND;
import static io.github.pzn.test.datafixture.BookingEntityDbFixture.insert;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static java.time.Month.JANUARY;
import static org.hamcrest.CoreMatchers.is;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class CampsiteApi_CancelReservationTest extends CampsiteApi_CancelReservationBaseTest {

	@Test
	public void can_DELETE_reservation__reservationIdentifier_exists__happy_path() {
		initializeDatabase(
			insert(aBooking("reservationIdentifier", VALID_DEPARTURE, VALID_ARRIVAL)));

		given()
			.when()
			.contentType(JSON)
			.delete(cancelUrl + "/reservationIdentifier")
			.then()
			.statusCode(204);
	}

	@Test
	public void cannot_DELETE_reservation__reservationIdentifier_does_not_exist__shall_return_400() {
		initializeDatabase();

		given()
			.when()
			.contentType(JSON)
			.delete(cancelUrl + "/reservationIdentifier")
			.then()
			.statusCode(400)
			.body("errorCode", is(RESERVATION_NOT_FOUND.getErrorCode()),
				"message", is(RESERVATION_NOT_FOUND.getMessage()));
	}

	@Test
	public void cannot_DELETE_reservation__reservation_already_happened__shall_return_400() {
		initializeDatabase(
			insert(aBooking("reservationIdentifier", LocalDate.of(1990, JANUARY, 1), LocalDate.of(1990, JANUARY, 3))));

		given()
			.when()
			.contentType(JSON)
			.delete(cancelUrl + "/reservationIdentifier")
			.then()
			.statusCode(400)
			.body("errorCode", is(RESERVATION_IN_THE_PAST.getErrorCode()),
				"message", is(RESERVATION_IN_THE_PAST.getMessage()));
	}
}
