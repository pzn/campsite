package io.github.pzn.campsite.api.resource;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static java.time.Month.APRIL;
import static java.time.Month.JULY;
import static java.time.Month.MARCH;
import static java.time.Month.MAY;
import static java.time.Month.OCTOBER;
import static java.time.Month.SEPTEMBER;
import static org.hamcrest.CoreMatchers.is;

import java.time.LocalDate;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class CampsiteApi_GetAvailability_CustomParamAPISignatureTest extends CampsiteApi_GetAvailabilityBaseTest {

	@BeforeEach
	public void beforeEach() {
		super.beforeEach();
		initializeDatabase();
	}

	@ParameterizedTest
	@MethodSource("fromAndToParameters")
	public void can_GET_availabilities__from_and_to_make_a_valid_range(LocalDate from, LocalDate to) {
		given()
			.when()
			.queryParam("from", formatLocalDate(from))
			.queryParam("to", formatLocalDate(to))
			.get(availabilitiesUrl)
			.then()
			.statusCode(200)
			.contentType(JSON)
			.body(
				"size()", is(3),
				"availabilityFrom", is(formatLocalDate(from)),
				"availabilityTo", is(formatLocalDate(to)));
	}

	private static Stream<Arguments> fromAndToParameters() {
		return Stream.of(
			Arguments.of(LocalDate.of(2021, APRIL, 1), LocalDate.of(2021, APRIL, 5)),
			Arguments.of(LocalDate.of(2021, SEPTEMBER, 15), LocalDate.of(2021, OCTOBER, 15)),
			Arguments.of(LocalDate.of(2021, JULY, 1), LocalDate.of(2021, JULY, 3))
		);
	}

	@Test
	public void can_GET_availabilities__only_fromQueryParam_provided__availabilityTo_shall_be_a_month_after_fromQueryParam() {
		LocalDate from = LocalDate.of(2020, APRIL, 1);
		LocalDate expectedAvailabilityTo = LocalDate.of(2020, MAY, 1);

		given()
			.when()
			.queryParam("from", formatLocalDate(from))
			.get(availabilitiesUrl)
			.then()
			.statusCode(200)
			.contentType(JSON)
			.body(
				"size()", is(3),
				"availabilityFrom", is(formatLocalDate(from)),
				"availabilityTo", is(formatLocalDate(expectedAvailabilityTo)));
	}

	@Test
	public void can_GET_availabilities__only_to_specified__availabilityFrom_shall_be_a_month_prior_to_toQueryParam() {
		LocalDate to = LocalDate.of(2020, APRIL, 5);
		LocalDate expectedAvailabilityFrom = LocalDate.of(2020, MARCH, 5);

		given()
			.when()
			.queryParam("to", formatLocalDate(to))
			.get(availabilitiesUrl)
			.then()
			.statusCode(200)
			.contentType(JSON)
			.body(
				"size()", is(3),
				"availabilityFrom", is(formatLocalDate(expectedAvailabilityFrom)),
				"availabilityTo", is(formatLocalDate(to)));
	}
}
