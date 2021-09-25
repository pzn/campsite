package io.github.pzn.campsite.api.resource;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.MONTHS;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import io.github.pzn.campsite.booking.model.entity.BookingEntity;
import io.github.pzn.test.datafixture.BookingEntityDbFixture;
import io.quarkus.test.common.http.TestHTTPResource;

abstract class CampsiteApi_GetAvailabilityBaseTest extends CampsiteApiBaseTest {

	@TestHTTPResource("/api/v1/campsite/availabilities")
	String availabilitiesUrl;

	protected BookingEntity aBooking(LocalDate arrival, LocalDate departure) {
		LocalDateTime now = LocalDateTime.ofInstant(clock.instant(), ZoneId.of("UTC"));
		return BookingEntity.builder()
			.id(BookingEntityDbFixture.SEQUENCE_ID.nextValue())
			.createdOn(now)
			.lastModified(now)
			.reservationIdentifier(easyRandom.nextObject(String.class))
			.email(easyRandom.nextObject(String.class))
			.fullName(easyRandom.nextObject(String.class))
			.arrival(arrival)
			.departure(departure)
			.build();
	}

	protected LocalDate expectedDefaultFrom() {
		return localDateNow().plus(1, DAYS);
	}

	protected LocalDate expectedDefaultTo(LocalDate to) {
		return to.plus(1, MONTHS);
	}
}
