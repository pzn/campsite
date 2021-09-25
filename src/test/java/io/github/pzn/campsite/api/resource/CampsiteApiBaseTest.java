package io.github.pzn.campsite.api.resource;

import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static io.github.pzn.test.datafixture.BookingEntityDbFixture.DELETE_BOOKINGS;
import static io.github.pzn.test.datafixture.BookingLockEntityDbFixture.DELETE_BOOKING_LOCKS;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.ArrayUtils.isEmpty;
import static org.mockito.Mockito.doReturn;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;

import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.junit.jupiter.api.BeforeEach;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;
import io.github.pzn.test.quarkus.PostgresTestContainersQuarkusTestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.mockito.InjectMock;

@QuarkusTestResource(PostgresTestContainersQuarkusTestResource.class)
abstract class CampsiteApiBaseTest {

	private static final ObjectMapper OM = new ObjectMapper();
	protected static final EasyRandom easyRandom = new EasyRandom(new EasyRandomParameters().seed(42L));
	protected static final ZoneId UTC = ZoneId.of("UTC");
	protected static final Instant CLOCK_CURRENT_INSTANT = Instant.parse("2015-05-03T05:10:15Z");

	@Inject
	DataSource dataSource;

	@InjectMock
	Clock clock;

	@BeforeEach
	public void beforeEach() {
		doReturn(CLOCK_CURRENT_INSTANT)
			.when(clock).instant();
		doReturn(UTC)
			.when(clock).getZone();
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

	protected String toJsonPayload(Map<String, String> payload) {
		try {
			return OM.writeValueAsString(payload);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	protected LocalDate localDateNow() {
		return LocalDate.ofInstant(clock.instant(), UTC);
	}

	protected LocalDateTime localDateTimeNow() {
		return LocalDateTime.ofInstant(clock.instant(), UTC);
	}

	protected String formatLocalDate(LocalDate localDate) {
		return ISO_LOCAL_DATE.format(localDate);
	}
}
