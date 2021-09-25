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
import io.github.pzn.campsite.booking.model.entity.BookingEntity;

@NoArgsConstructor
public class BookingEntityDbFixture {

	private static final String TABLE_NAME = "bookings";

	public static final SequenceValueGenerator SEQUENCE_ID = ValueGenerators.sequence().startingAt(1L).incrementingBy(1);
	public static final Operation DELETE_BOOKINGS = deleteAllFrom(TABLE_NAME);

	public static Operation insert(BookingEntity bookingEntity) {
		Map<String, Object> allFields = allFields(bookingEntity);
		List<String> columns = new ArrayList<>(allFields.keySet());
		List<Object> values = new ArrayList<>(allFields.values());

		return insertInto(TABLE_NAME)
			.columns(columns.toArray(new String[0]))
			.values(values.toArray(new Object[0]))
			.build();
	}

	private static Map<String, Object> allFields(BookingEntity bookingEntity) {
		return ImmutableMap.<String, Object>builder()
			.put("id", String.valueOf(bookingEntity.getId()))
			.put("created_on", bookingEntity.getCreatedOn())
			.put("last_modified", bookingEntity.getLastModified())
			.put("reservation_identifier", bookingEntity.getReservationIdentifier())
			.put("email", bookingEntity.getEmail())
			.put("full_name", bookingEntity.getFullName())
			.put("arrival", bookingEntity.getArrival())
			.put("departure", bookingEntity.getDeparture())
			.build();
	}
}
