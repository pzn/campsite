package io.github.pzn.campsite.booking.service;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;
import io.github.pzn.campsite.booking.model.vo.AlterBookingVO;
import io.github.pzn.campsite.booking.model.vo.BookingVO;
import io.github.pzn.campsite.booking.model.vo.NewBookingVO;
import io.github.pzn.test.quarkus.PostgresTestContainersQuarkusTestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.mockito.InjectMock;

import javax.inject.Inject;
import javax.sql.DataSource;
import javax.transaction.UserTransaction;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static io.github.pzn.test.datafixture.BookingEntityDbFixture.DELETE_BOOKINGS;
import static io.github.pzn.test.datafixture.BookingLockEntityDbFixture.DELETE_BOOKING_LOCKS;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.ArrayUtils.isEmpty;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

@QuarkusTestResource(PostgresTestContainersQuarkusTestResource.class)
abstract class SaveBookingServiceBaseTest {

	@Inject
	DataSource dataSource;
	@Inject
	UserTransaction userTransaction;
	@Inject
	SaveBookingService saveBookingService;
	@InjectMock
	BookingVerificationService bookingVerificationService;

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

	protected void givenValidationPasses() {
		givenValidationReturns(true);
	}

	protected void givenValidationDoesNotPass() {
		givenValidationReturns(false);
	}

	private void givenValidationReturns(boolean isValid) {
		if (isValid) {
			doNothing()
				.when(bookingVerificationService).verifyCanNewBooking(any(NewBookingVO.class));
			doNothing()
				.when(bookingVerificationService).verifyCanUpdateBooking(any(AlterBookingVO.class), any(BookingVO.class));
		} else {
			doThrow(RuntimeException.class)
				.when(bookingVerificationService).verifyCanNewBooking(any(NewBookingVO.class));
			doThrow(RuntimeException.class)
				.when(bookingVerificationService).verifyCanUpdateBooking(any(AlterBookingVO.class), any(BookingVO.class));
		}
	}
}
