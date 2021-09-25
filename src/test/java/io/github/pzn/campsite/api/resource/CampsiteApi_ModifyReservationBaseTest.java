package io.github.pzn.campsite.api.resource;

import static java.time.temporal.ChronoUnit.DAYS;

import java.time.LocalDate;
import java.time.LocalDateTime;

import io.github.pzn.campsite.booking.model.entity.BookingEntity;
import io.github.pzn.test.datafixture.BookingEntityDbFixture;
import io.quarkus.test.common.http.TestHTTPResource;

abstract class CampsiteApi_ModifyReservationBaseTest extends CampsiteApiBaseTest {

	protected static final String VALID_EMAIL = "steve@lukather.com";
	protected static final String VALID_FULL_NAME = "Steve \"Luke\" Lukather";
	protected static final LocalDate VALID_ARRIVAL = LocalDate.ofInstant(CLOCK_CURRENT_INSTANT.plus(1, DAYS), UTC);
	protected static final LocalDate VALID_DEPARTURE = VALID_ARRIVAL.plus(1, DAYS);

	@TestHTTPResource("/api/v1/campsite/reservation")
	String modifyUrl;

	protected BookingEntity aBooking(String email, String fullName, String reservationIdentifier, LocalDate arrival, LocalDate departure) {
		LocalDateTime now = localDateTimeNow();
		return BookingEntity.builder()
			.id(BookingEntityDbFixture.SEQUENCE_ID.nextValue())
			.createdOn(now)
			.lastModified(now)
			.reservationIdentifier(reservationIdentifier)
			.email(email)
			.fullName(fullName)
			.arrival(arrival)
			.departure(departure)
			.build();
	}
}
