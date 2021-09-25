package io.github.pzn.campsite.api.resource;

import static io.github.pzn.campsite.api.error.CampsiteApiErrorCode.BAD_INPUT;
import static io.github.pzn.campsite.api.error.CampsiteApiErrorCode.BOOKING_RANGE_EXCEEDED;
import static io.github.pzn.campsite.api.error.CampsiteApiErrorCode.INCOMPATIBLE_DEPARTURE_AND_ARRIVAL_DATES;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static java.time.Month.JANUARY;
import static java.time.temporal.ChronoUnit.DAYS;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.StringStartsWith.startsWith;

import java.time.LocalDate;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class CampsiteApi_ModifyReservation_InvalidPayloadTest extends CampsiteApi_ModifyReservationBaseTest {

	@Test
	public void cannot_PATCH_reservation__arrival_is_in_the_past__shall_return_400() {
		given()
			.when()
			.body(toJsonPayload(Map.of("arrival", formatLocalDate(LocalDate.of(1990, JANUARY, 1)), "departure", formatLocalDate(VALID_DEPARTURE))))
			.contentType(JSON)
			.patch(modifyUrl + "/reservationIdentifier")
			.then()
			.contentType(JSON)
			.statusCode(400)
			.body("errorCode", is(BAD_INPUT.getErrorCode()),
				"message", startsWith("Parameter 'arrival'"));
	}

	@Test
	public void cannot_PATCH_reservation__departure_is_in_the_past__shall_return_400() {
		given()
			.when()
			.body(toJsonPayload(Map.of("arrival", formatLocalDate(VALID_ARRIVAL), "departure", formatLocalDate(LocalDate.of(1990, JANUARY, 1)))))
			.contentType(JSON)
			.patch(modifyUrl + "/reservationIdentifier")
			.then()
			.contentType(JSON)
			.statusCode(400)
			.body("errorCode", is(BAD_INPUT.getErrorCode()),
				"message", startsWith("Parameter 'departure'"));
	}

	@Test
	public void cannot_PATCH_reservation__arrival_is_same_as_departure__shall_return_400() {
		String fromAndTo = formatLocalDate(localDateNow().plus(5, DAYS));

		given()
			.when()
			.body(toJsonPayload(Map.of("arrival", fromAndTo, "departure", fromAndTo)))
			.contentType(JSON)
			.patch(modifyUrl + "/reservationIdentifier")
			.then()
			.contentType(JSON)
			.statusCode(400)
			.body("errorCode", is(INCOMPATIBLE_DEPARTURE_AND_ARRIVAL_DATES.getErrorCode()),
				"message", startsWith(INCOMPATIBLE_DEPARTURE_AND_ARRIVAL_DATES.getMessage()));
	}

	@Test
	public void cannot_PATCH_reservation__arrival_is_after_departure__shall_return_400() {
		LocalDate arrival = localDateNow().plus(5, DAYS);
		LocalDate departurePriorToArrival = arrival.minus(1, DAYS);

		given()
			.when()
			.body(toJsonPayload(Map.of("arrival", formatLocalDate(arrival), "departure", formatLocalDate(departurePriorToArrival))))
			.contentType(JSON)
			.patch(modifyUrl + "/reservationIdentifier")
			.then()
			.contentType(JSON)
			.statusCode(400)
			.body("errorCode", is(INCOMPATIBLE_DEPARTURE_AND_ARRIVAL_DATES.getErrorCode()),
				"message", startsWith(INCOMPATIBLE_DEPARTURE_AND_ARRIVAL_DATES.getMessage()));
	}

	@Test
	public void cannot_PATCH_reservation__range_exceeds_three_days__shall_return_400() {
		LocalDate arrival = localDateNow().plus(5, DAYS);
		LocalDate departureIsFourDaysLater = arrival.plus(4, DAYS);

		given()
			.when()
			.body(toJsonPayload(Map.of("arrival", formatLocalDate(arrival), "departure", formatLocalDate(departureIsFourDaysLater))))
			.contentType(JSON)
			.patch(modifyUrl + "/reservationIdentifier")
			.then()
			.contentType(JSON)
			.statusCode(400)
			.body("errorCode", is(BOOKING_RANGE_EXCEEDED.getErrorCode()),
				"message", startsWith(BOOKING_RANGE_EXCEEDED.getMessage()));
	}
}
