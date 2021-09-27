package io.github.pzn.campsite.booking.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import io.github.pzn.campsite.booking.error.ReservationNotFoundException;
import io.github.pzn.campsite.booking.model.entity.BookingEntity;
import io.github.pzn.test.datafixture.BookingEntityDbFixture;
import io.github.pzn.test.datafixture.BookingLockEntityDbFixture;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class SaveBookingService_DeleteBookingTest extends SaveBookingServiceBaseTest {

	private static final String RESERVATION_IDENTIFIER = "reservationIdentifier";

	@Test
	public void can_deleteBooking__happy_path() {
		initializeDatabase(
			BookingEntityDbFixture.insert(aBookingEntity(LocalDate.now(), LocalDate.now())));

		saveBookingService.deleteBooking(RESERVATION_IDENTIFIER);
	}

	@Test
	public void cannot_deleteBooking__reservation_does_not_exists__shall_throw_exception() {
		initializeDatabase();

		assertThatThrownBy(() -> saveBookingService.deleteBooking(RESERVATION_IDENTIFIER))
			.isInstanceOf(ReservationNotFoundException.class);
	}

	private BookingEntity aBookingEntity(LocalDate arrival, LocalDate departure) {
		return BookingEntity.builder()
			.id(BookingLockEntityDbFixture.SEQUENCE_ID.nextValue())
			.createdOn(LocalDateTime.now())
			.lastModified(LocalDateTime.now())
			.email("email")
			.fullName("fullName")
			.reservationIdentifier(RESERVATION_IDENTIFIER)
			.arrival(arrival)
			.departure(departure)
			.build();
	}
}
