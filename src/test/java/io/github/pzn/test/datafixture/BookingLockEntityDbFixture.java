package io.github.pzn.test.datafixture;

import static com.ninja_squad.dbsetup.Operations.deleteAllFrom;
import static com.ninja_squad.dbsetup.Operations.insertInto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.NoArgsConstructor;

import com.google.common.collect.ImmutableMap;
import com.ninja_squad.dbsetup.generator.SequenceValueGenerator;
import com.ninja_squad.dbsetup.generator.ValueGenerators;
import com.ninja_squad.dbsetup.operation.Operation;
import io.github.pzn.campsite.booking.model.entity.BookingLockEntity;

@NoArgsConstructor
public class BookingLockEntityDbFixture {

	private static final String TABLE_NAME = "booking_locks";

	public static final SequenceValueGenerator SEQUENCE_ID = ValueGenerators.sequence().startingAt(1L).incrementingBy(1);
	public static final Operation DELETE_BOOKING_LOCKS = deleteAllFrom("booking_locks");

	public static Operation insert(BookingLockEntity bookingLockEntity) {
		Map<String, Object> allFields = allFields(bookingLockEntity);
		List<String> columns = new ArrayList<>(allFields.keySet());
		List<Object> values = new ArrayList<>(allFields.values());

		return insertInto(TABLE_NAME)
			.columns(columns.toArray(new String[0]))
			.values(values.toArray(new Object[0]))
			.build();
	}

	private static Map<String, Object> allFields(BookingLockEntity bookingLockEntity) {
		return ImmutableMap.<String, Object>builder()
			.put("id", String.valueOf(bookingLockEntity.getId()))
			.put("booking_day", bookingLockEntity.getBookingDay())
			.put("booking_id", bookingLockEntity.getBooking().getId())
			.build();
	}
}
