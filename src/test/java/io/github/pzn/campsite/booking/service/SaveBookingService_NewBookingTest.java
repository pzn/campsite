package io.github.pzn.campsite.booking.service;

import io.github.pzn.campsite.booking.model.entity.BookingEntity;
import io.github.pzn.campsite.booking.model.entity.BookingLockEntity;
import io.github.pzn.campsite.booking.model.vo.NewBookingVO;
import io.github.pzn.test.datafixture.BookingEntityDbFixture;
import io.github.pzn.test.datafixture.BookingLockEntityDbFixture;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.transaction.RollbackException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static java.time.Month.JANUARY;
import static java.time.temporal.ChronoUnit.DAYS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.testcontainers.shaded.org.apache.commons.lang.RandomStringUtils.randomAlphanumeric;

@QuarkusTest
public class SaveBookingService_NewBookingTest extends SaveBookingServiceBaseTest {

	@Test
	public void can_newBooking__happy_path() {
		initializeDatabase();
		givenValidationPasses();

		String reservationIdentifier = saveBookingService.newBooking(aNewBookingVO());

		assertThat(reservationIdentifier).isNotEmpty();
	}

	@Test
	public void cannot_newBooking__when_validation_does_not_pass__shall_expect_exception() {
		initializeDatabase();
		givenValidationDoesNotPass();

		assertThatThrownBy(() -> saveBookingService.newBooking(aNewBookingVO()))
						.isInstanceOf(RuntimeException.class);
	}

	@Test
	public void cannot_newBooking__dates_have_been_locked_during_validation__shall_throw_exception() {
		LocalDate firstDay = LocalDate.of(2020, JANUARY, 1);
		LocalDate secondDay = firstDay.plus(1, DAYS);
		BookingEntity existingBookingEntity = aBookingEntity("email", firstDay, secondDay);
		initializeDatabase(
						BookingEntityDbFixture.insert(existingBookingEntity),
						BookingLockEntityDbFixture.insert(aBookingLockEntity(firstDay, existingBookingEntity)),
						BookingLockEntityDbFixture.insert(aBookingLockEntity(secondDay, existingBookingEntity)));
		givenValidationPasses();

		assertThatThrownBy(() -> saveBookingService.newBooking(aNewBookingVO("another-email", firstDay, secondDay)))
						.hasCauseInstanceOf(RollbackException.class);
	}

	private NewBookingVO aNewBookingVO() {
		return aNewBookingVO(randomAlphanumeric(5), LocalDate.now(), LocalDate.now());
	}

	private NewBookingVO aNewBookingVO(String email, LocalDate arrival, LocalDate departure) {
		return NewBookingVO.of(email, "fullName", arrival, departure);
	}

	private BookingEntity aBookingEntity(String email, LocalDate arrival, LocalDate departure) {
		return BookingEntity.builder()
			.id(BookingEntityDbFixture.SEQUENCE_ID.nextValue())
			.createdOn(LocalDateTime.now())
			.lastModified(LocalDateTime.now())
			.email(email)
			.fullName("fullName")
			.reservationIdentifier("reservationIdentifier")
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
