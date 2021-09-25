package io.github.pzn.campsite.api.resource;

import static io.github.pzn.campsite.booking.error.CampsiteErrorCode.DATES_UNAVAILABLE_FOR_RESERVATION;
import static io.github.pzn.campsite.booking.error.CampsiteErrorCode.RESERVATION_NOT_FOUND;
import static io.github.pzn.test.datafixture.BookingEntityDbFixture.insert;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.MONTHS;
import static org.hamcrest.CoreMatchers.is;

import java.time.LocalDate;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class CampsiteApi_ModifyReservation_ValidPayloadTest extends CampsiteApi_ModifyReservationBaseTest {

	@Test
	public void can_PATCH_reservation__new_dates_are_not_booked_whatsoever__happy_path() {
		initializeDatabase(
			insert(aBooking(VALID_EMAIL, VALID_FULL_NAME, "reservationIdentifier", VALID_ARRIVAL, VALID_DEPARTURE)));
		LocalDate newArrivalDate = VALID_ARRIVAL.plus(1, MONTHS);
		LocalDate newDepartureDate = VALID_DEPARTURE.plus(1, MONTHS);

		given()
			.when()
			.body(toJsonPayload(Map.of("arrival", formatLocalDate(newArrivalDate), "departure", formatLocalDate(newDepartureDate))))
			.contentType(JSON)
			.patch(modifyUrl + "/reservationIdentifier")
			.then()
			.contentType(JSON)
			.statusCode(200)
			.body("reservationIdentifier", is("reservationIdentifier"));
	}

	@Test
	public void can_PATCH_reservation__new_dates_overlaps_with_same_reservation__happy_path() {
		LocalDate initialArrivalDate = VALID_ARRIVAL;
		LocalDate initialDepartureDate = VALID_ARRIVAL.plus(3, DAYS);
		initializeDatabase(
			insert(aBooking(VALID_EMAIL, VALID_FULL_NAME, "reservationIdentifier", initialArrivalDate, initialDepartureDate)));
		LocalDate newArrivalDate = VALID_ARRIVAL.plus(1, DAYS);
		LocalDate newDepartureDate = initialDepartureDate;

		given()
			.when()
			.body(toJsonPayload(Map.of("arrival", formatLocalDate(newArrivalDate), "departure", formatLocalDate(newDepartureDate))))
			.contentType(JSON)
			.patch(modifyUrl + "/reservationIdentifier")
			.then()
			.contentType(JSON)
			.statusCode(200)
			.body("reservationIdentifier", is("reservationIdentifier"));
	}

	@Test
	public void cannot_PATCH_reservation__reservationIdentifier_does_not_exist__shall_return_400() {
		initializeDatabase();

		given()
			.when()
			.body(toJsonPayload(Map.of("arrival", formatLocalDate(VALID_ARRIVAL), "departure", formatLocalDate(VALID_DEPARTURE))))
			.contentType(JSON)
			.patch(modifyUrl + "/reservationIdentifier")
			.then()
			.contentType(JSON)
			.statusCode(400)
			.body("errorCode", is(RESERVATION_NOT_FOUND.getErrorCode()),
				"message", is(RESERVATION_NOT_FOUND.getMessage()));
	}

	@Test
	public void cannot_PATCH_reservation__requested_range_already_booked_by_another_reservation__shall_return_400() {
		LocalDate otherReservationArrivalLocalDate = VALID_ARRIVAL;
		LocalDate otherReservationDepartureLocalDate = VALID_DEPARTURE;
		LocalDate myReservationStartsTheDayAfterMyReservation = otherReservationDepartureLocalDate;
		LocalDate myReservationEndsOneDayAfter = myReservationStartsTheDayAfterMyReservation.plus(1, DAYS);
		initializeDatabase(
			insert(aBooking(VALID_EMAIL, VALID_FULL_NAME, "otherReservationIdentifier", otherReservationArrivalLocalDate, otherReservationDepartureLocalDate)),
			insert(aBooking("jean@michel.com", "Jean Michel", "myReservationIdentifier", myReservationStartsTheDayAfterMyReservation, myReservationEndsOneDayAfter)));
		LocalDate myNewDepartureIsTheDayBeforeTheOtherReservationEnds = otherReservationDepartureLocalDate.minus(1, DAYS);

		given()
			.when()
			.body(toJsonPayload(Map.of("arrival", formatLocalDate(myNewDepartureIsTheDayBeforeTheOtherReservationEnds), "departure", formatLocalDate(myReservationEndsOneDayAfter))))
			.contentType(JSON)
			.patch(modifyUrl + "/myReservationIdentifier")
			.then()
			.contentType(JSON)
			.statusCode(400)
			.body("errorCode", is(DATES_UNAVAILABLE_FOR_RESERVATION.getErrorCode()),
				"message", is(DATES_UNAVAILABLE_FOR_RESERVATION.getMessage()));
	}
}
