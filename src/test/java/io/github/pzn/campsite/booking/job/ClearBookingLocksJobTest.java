package io.github.pzn.campsite.booking.job;

import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static io.github.pzn.test.datafixture.BookingEntityDbFixture.DELETE_BOOKINGS;
import static io.github.pzn.test.datafixture.BookingLockEntityDbFixture.DELETE_BOOKING_LOCKS;
import static java.time.Month.JANUARY;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.ArrayUtils.isEmpty;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import javax.inject.Inject;
import javax.sql.DataSource;
import javax.transaction.UserTransaction;
import javax.validation.constraints.NotNull;

import org.junit.jupiter.api.Test;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;
import io.github.pzn.campsite.booking.model.entity.BookingEntity;
import io.github.pzn.campsite.booking.model.entity.BookingLockEntity;
import io.github.pzn.campsite.booking.repository.BookingLockRepository;
import io.github.pzn.test.datafixture.BookingEntityDbFixture;
import io.github.pzn.test.datafixture.BookingLockEntityDbFixture;
import io.github.pzn.test.quarkus.PostgresTestContainersQuarkusTestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@QuarkusTestResource(PostgresTestContainersQuarkusTestResource.class)
public class ClearBookingLocksJobTest {

	@Inject
	DataSource dataSource;
	@Inject
	UserTransaction userTransaction;
	@Inject
	BookingLockRepository bookingLockRepository;
	@Inject
	ClearBookingLocksJob clearBookingLocksJob;

	@Test
	public void can_cleanBookingLocksJob__happy_path() throws Exception {
		LocalDate firstDay = LocalDate.of(2020, JANUARY, 1);
		LocalDate secondDay = firstDay.plus(1, DAYS);
		BookingEntity existingBookingEntity = aBookingEntity("email", firstDay, secondDay);
		initializeDatabase(
			BookingEntityDbFixture.insert(existingBookingEntity),
			BookingLockEntityDbFixture.insert(aBookingLockEntity(firstDay, existingBookingEntity)),
			BookingLockEntityDbFixture.insert(aBookingLockEntity(secondDay, existingBookingEntity)));
		executeWithinTransaction(() -> assertThat(bookingLockRepository.count()).isEqualTo(2));

		clearBookingLocksJob.clearBookingLocks();

		executeWithinTransaction(() -> bookingLockRepository.count() == 0);
	}

	protected void initializeDatabase(@NotNull Operation... operations) {
		if (isEmpty(operations)) {
			operations = new Operation[0];
		}
		List<Operation> allOperations = new ArrayList<>(2 + operations.length);
		allOperations.add(DELETE_BOOKING_LOCKS);
		allOperations.add(DELETE_BOOKINGS);
		allOperations.addAll(asList(operations));

		new DbSetup(new DataSourceDestination(dataSource), sequenceOf(allOperations)).launch();
	}

	private BookingEntity aBookingEntity(String email, LocalDate arrival, LocalDate departure) {
		return BookingEntity.builder()
			.id(BookingLockEntityDbFixture.SEQUENCE_ID.nextValue())
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

	private <T> T executeWithinTransaction(Supplier<T> supplier) throws Exception {
		try {
			userTransaction.begin();
			return supplier.get();
		} catch (Exception e) {
			userTransaction.rollback();
			throw e;
		} finally {
			userTransaction.commit();
		}
	}
}
