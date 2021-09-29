package io.github.pzn.campsite.booking.service;

import io.github.pzn.campsite.booking.error.ReservationNotFoundException;
import io.github.pzn.campsite.booking.model.entity.BookingEntity;
import io.github.pzn.campsite.booking.model.entity.BookingLockEntity;
import io.github.pzn.campsite.booking.model.vo.AlterBookingVO;
import io.github.pzn.test.datafixture.BookingEntityDbFixture;
import io.github.pzn.test.datafixture.BookingLockEntityDbFixture;
import io.quarkus.test.junit.QuarkusTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.transaction.RollbackException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static java.time.Month.JANUARY;
import static java.time.temporal.ChronoUnit.DAYS;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@QuarkusTest
public class SaveBookingService_UpdateBookingTest extends SaveBookingServiceBaseTest {

	@Test
	public void can_updateBooking__happy_path() {
		LocalDate firstDay = LocalDate.of(2020, JANUARY, 1);
		LocalDate secondDay = firstDay.plus(1, DAYS);
		initializeDatabase(
			BookingEntityDbFixture.insert(aBookingEntity("email", "reservationIdentifier", firstDay, secondDay)));
		givenValidationPasses();

		saveBookingService.updateBooking("reservationIdentifier", anAlterBookingVO(firstDay, secondDay));
	}

	@Test
	public void cannot_updateBooking__reservation_not_found__shall_expect_exception() throws Exception {
		initializeDatabase();
		givenValidationDoesNotPass();

		assertThatThrownBy(() -> saveBookingService.updateBooking("reservationIdentifier", anAlterBookingVO()))
			.isInstanceOf(ReservationNotFoundException.class);
	}

	@Test
	public void cannot_updateBooking__validation_does_not_pass__shall_expect_exception() {
		initializeDatabase(
			BookingEntityDbFixture.insert(aBookingEntity("email", "reservationIdentifier", LocalDate.now(), LocalDate.now())));
		givenValidationDoesNotPass();

    assertThatThrownBy(() -> saveBookingService.updateBooking("reservationIdentifier", anAlterBookingVO()))
			.isInstanceOf(Exception.class);
	}

	@Test
	public void cannot_updateBooking__dates_have_been_locked_during_validation__shall_throw_exception() {
		LocalDate firstDay = LocalDate.of(2020, JANUARY, 1);
		LocalDate secondDay = firstDay.plus(1, DAYS);
		BookingEntity existingBookingEntity = aBookingEntity("email", "reservationIdentifier", firstDay, secondDay);
		initializeDatabase(
			BookingEntityDbFixture.insert(existingBookingEntity),
			BookingLockEntityDbFixture.insert(aBookingLockEntity(firstDay, existingBookingEntity)),
			BookingLockEntityDbFixture.insert(aBookingLockEntity(secondDay, existingBookingEntity)));
		givenValidationPasses();

		Assertions.setMaxStackTraceElementsDisplayed(5000);
		assertThatThrownBy(() -> saveBookingService.updateBooking("reservationIdentifier", anAlterBookingVO(firstDay, secondDay)))
						.hasCauseInstanceOf(RollbackException.class);
	}

	private AlterBookingVO anAlterBookingVO() {
		return AlterBookingVO.of(LocalDate.now(), LocalDate.now());
	}

	private AlterBookingVO anAlterBookingVO(LocalDate arrival, LocalDate departure) {
		return AlterBookingVO.of(arrival, departure);
	}

	private BookingEntity aBookingEntity(String email, String reservationIdentifier, LocalDate arrival, LocalDate departure) {
		return BookingEntity.builder()
			.id(BookingEntityDbFixture.SEQUENCE_ID.nextValue())
			.createdOn(LocalDateTime.now())
			.lastModified(LocalDateTime.now())
			.email(email)
			.fullName("fullName")
			.reservationIdentifier(reservationIdentifier)
			.arrival(arrival)
			.departure(departure)
			.build();
	}

	private BookingLockEntity aBookingLockEntity(LocalDate bookingDay, BookingEntity relatedBookingEntity) {
		return BookingLockEntity.builder()
			.id(BookingLockEntityDbFixture.SEQUENCE_ID.nextValue())
			.bookingDay(bookingDay)
			.booking(relatedBookingEntity)
			.build();
	}
}
