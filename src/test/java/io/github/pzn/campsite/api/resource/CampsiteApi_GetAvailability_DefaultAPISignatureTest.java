package io.github.pzn.campsite.api.resource;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.YEARS;
import static org.hamcrest.CoreMatchers.is;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import io.github.pzn.test.datafixture.BookingEntityDbFixture;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class CampsiteApi_GetAvailability_DefaultAPISignatureTest extends CampsiteApi_GetAvailabilityBaseTest {

	@Test
	public void can_GET_availabilities__default_API_signature__no_bookings_for_period__shall_expect_the_whole_period_being_available() {
		initializeDatabase();
		LocalDate expectedAvailabilityFrom = expectedDefaultFrom();
		LocalDate expectedAvailabilityTo = expectedDefaultTo(expectedAvailabilityFrom);
		int numberOfDaysInTheSpecifiedPeriod = (int) expectedAvailabilityFrom.datesUntil(expectedAvailabilityTo.plus(1, DAYS)).count();

		given()
			.when()
			.get(availabilitiesUrl)
			.then()
			.statusCode(200)
			.contentType(JSON)
			.body(
				"size()", is(3),
				"availabilityFrom", is(formatLocalDate(expectedAvailabilityFrom)),
				"availabilityTo", is(formatLocalDate(expectedAvailabilityTo)),
				"availabilities.size()", is(numberOfDaysInTheSpecifiedPeriod));
	}

	@Test
	public void can_GET_availabilities__default_API_signature__one_booking_for_three_days__shall_expect_three_less_available_days() {
		LocalDate arrivalIsTomorrow = LocalDate.ofInstant(CLOCK_CURRENT_INSTANT.plus(1, DAYS), UTC);
		LocalDate departureIsThreeDaysAfter = arrivalIsTomorrow.plus(3, DAYS);
		initializeDatabase(BookingEntityDbFixture.insert(aBooking(arrivalIsTomorrow, departureIsThreeDaysAfter)));
		LocalDate expectedAvailabilityFrom = expectedDefaultFrom();
		LocalDate expectedAvailabilityTo = expectedDefaultTo(expectedAvailabilityFrom);
		int numberOfDaysInTheSpecifiedPeriod = (int) expectedAvailabilityFrom.datesUntil(expectedAvailabilityTo.plus(1, DAYS)).count();

		given()
			.when()
			.get(availabilitiesUrl)
			.then()
			.statusCode(200)
			.contentType(JSON)
			.body(
				"size()", is(3),
				"availabilityFrom", is(formatLocalDate(expectedAvailabilityFrom)),
				"availabilityTo", is(formatLocalDate(expectedAvailabilityTo)),
				"availabilities.size()", is(numberOfDaysInTheSpecifiedPeriod - 3)); // whole month minus three days
	}

	@Test
	public void can_GET_availabilities__default_API_signature__whole_period_is_booked__shall_expect_no_availabilities() {
		LocalDate arrivalIsToday = LocalDate.ofInstant(CLOCK_CURRENT_INSTANT, UTC);
		LocalDate departureIsInAYear = arrivalIsToday.plus(1, YEARS);
		initializeDatabase(BookingEntityDbFixture.insert(aBooking(arrivalIsToday, departureIsInAYear))); // TODO generate as many 3-days bookings as possible instead of an unexpected year in a single booking
		LocalDate expectedAvailabilityFrom = expectedDefaultFrom();
		LocalDate expectedAvailabilityTo = expectedDefaultTo(expectedAvailabilityFrom);

		given()
			.when()
			.get(availabilitiesUrl)
			.then()
			.statusCode(200)
			.contentType(JSON)
			.body(
				"size()", is(3),
				"availabilityFrom", is(formatLocalDate(expectedAvailabilityFrom)),
				"availabilityTo", is(formatLocalDate(expectedAvailabilityTo)),
				"availabilities.size()", is(0));
	}
}
