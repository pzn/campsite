package io.github.pzn.campsite.api.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;

import static io.github.pzn.campsite.booking.error.CampsiteErrorCode.DATES_UNAVAILABLE;
import static io.github.pzn.campsite.booking.error.CampsiteErrorCode.EMAIL_HAS_AN_UPCOMING_RESERVATION;
import static io.github.pzn.test.datafixture.BookingEntityDbFixture.insert;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static java.time.Month.JANUARY;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.MONTHS;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.StringStartsWith.startsWith;

@QuarkusTest
public class CampsiteApi_PlaceReservation_ValidPayloadTest extends CampsiteApi_PlaceReservationBaseTest {

	@Test
	public void can_POST_reservation__no_upcoming_reservation__happy_path() {
		initializeDatabase();

		given()
			.when()
			.body(aValidDefaultPayload())
			.contentType(JSON)
			.post(reservationUrl)
			.then()
			.contentType(JSON)
			.statusCode(200)
			.body("reservationIdentifier", allOf(is(notNullValue()), hasLength(36)));
	}

	@Test
	public void can_POST_reservation__email_already_associated_to_past_reservation__happy_path() {
		initializeDatabase(
			insert(aBooking(VALID_EMAIL, VALID_FULL_NAME, LocalDate.of(1990, JANUARY, 1), LocalDate.of(1990, JANUARY, 3))));

		given()
			.when()
			.body(aValidDefaultPayload())
			.contentType(JSON)
			.post(reservationUrl)
			.then()
			.contentType(JSON)
			.statusCode(200)
			.body("reservationIdentifier", allOf(is(notNullValue()), hasLength(36)));
	}

	@Test
	public void cannot_POST_reservation__some_dates_already_booked___shall_return_400() {
		initializeDatabase(
			insert(aBooking(VALID_EMAIL, VALID_FULL_NAME, VALID_ARRIVAL, VALID_DEPARTURE)));

		given()
			.when()
			.body(aPayload("jean@michel.com", "Jean Michel", VALID_ARRIVAL, VALID_DEPARTURE))
			.contentType(JSON)
			.post(reservationUrl)
			.then()
			.contentType(JSON)
			.statusCode(400)
			.body("errorCode", is(DATES_UNAVAILABLE.getErrorCode()),
				"message", startsWith(DATES_UNAVAILABLE.getMessage()));
	}

	@Test
	public void cannot_POST_reservation__email_already_associated_to_upcoming_reservation___shall_return_400() {
		initializeDatabase(
			insert(aBooking(VALID_EMAIL, VALID_FULL_NAME, VALID_ARRIVAL, VALID_DEPARTURE)));
		LocalDate aMonthLater = VALID_ARRIVAL.plus(1, MONTHS);
		LocalDate aMonthLaterPlusThreeDays = aMonthLater.plus(1, DAYS);

		given()
			.when()
			.body(aPayload(VALID_EMAIL, VALID_FULL_NAME, aMonthLater, aMonthLaterPlusThreeDays))
			.contentType(JSON)
			.post(reservationUrl)
			.then()
			.contentType(JSON)
			.statusCode(400)
			.body("errorCode", is(EMAIL_HAS_AN_UPCOMING_RESERVATION.getErrorCode()),
				"message", startsWith(EMAIL_HAS_AN_UPCOMING_RESERVATION.getMessage()));
	}

	private String aPayload(String email, String fullName, LocalDate arrival, LocalDate departure) {
		return toJsonPayload(Map.of("email", email, "fullName", fullName, "arrival", formatLocalDate(arrival), "departure", formatLocalDate(departure)));
	}

	private String aValidDefaultPayload() {
		return aPayload(VALID_EMAIL, VALID_FULL_NAME, VALID_ARRIVAL, VALID_DEPARTURE);
	}
}
