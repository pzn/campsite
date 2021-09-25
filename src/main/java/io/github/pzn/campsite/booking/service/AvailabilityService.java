package io.github.pzn.campsite.booking.service;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toUnmodifiableList;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.constraints.NotNull;

import io.github.pzn.campsite.booking.model.vo.BookingVO;

@ApplicationScoped
public class AvailabilityService {

	ReadBookingService readBookingService;

	public AvailabilityService(ReadBookingService readBookingService) {
		this.readBookingService = readBookingService;
	}

	public Collection<LocalDate> findAvailableDates(@NotNull LocalDate from, @NotNull LocalDate to) {
		Collection<BookingVO> bookingsWithinRange = readBookingService.findAllBookingsBetween(from, to);
		return findAvailableDates(bookingsWithinRange, from, to);
	}

	private Collection<LocalDate> findAvailableDatesForReservation(@NotNull String reservationIdentifier, @NotNull LocalDate from, @NotNull LocalDate to) {
		Collection<BookingVO> bookingsWithinRange = readBookingService.findAllBookingsExcludingReservationBetween(reservationIdentifier, from, to);
		return findAvailableDates(bookingsWithinRange, from, to);
	}

	private Collection<LocalDate> findAvailableDates(Collection<BookingVO> bookingsWithinRange, LocalDate from, LocalDate to) {
		List<LocalDate> allBookedDates =
			bookingsWithinRange.stream()
				.flatMap(booking -> booking.getArrival().datesUntil(booking.getDeparture()))
				.filter(bookedDate -> isBookedDateSameAsLowerBoundary(bookedDate, from) || isBookedDateWithinRangeAndWithinBookableRange(bookedDate, from, to))
				.collect(toList());

		return from.datesUntil(to.plus(1, DAYS))
			.filter(localDateWithinRange -> !allBookedDates.contains(localDateWithinRange))
			.collect(toUnmodifiableList());
	}

	private boolean isBookedDateSameAsLowerBoundary(LocalDate bookedDate, LocalDate from) {
		return bookedDate.isEqual(from);
	}

	private boolean isBookedDateWithinRangeAndWithinBookableRange(LocalDate bookedDate, LocalDate from, LocalDate to) {
		return bookedDate.isAfter(from) && bookedDate.isBefore(to.plus(1, DAYS));
	}

	public boolean isRangeAvailable(@NotNull LocalDate from, @NotNull LocalDate to) {
		List<LocalDate> requestedDates = from.datesUntil(to.plus(1, DAYS)).collect(toList());
		Collection<LocalDate> availableDates = findAvailableDates(from, to);
		return availableDates.containsAll(requestedDates);
	}

	public boolean isRangeAvailableFor(@NotNull String reservationIdentifier, @NotNull LocalDate from, @NotNull LocalDate to) {
		List<LocalDate> requestedDates = from.datesUntil(to.plus(1, DAYS)).collect(toList());
		Collection<LocalDate> availableDatesForReservation = findAvailableDatesForReservation(reservationIdentifier, from, to);
		return availableDatesForReservation.containsAll(requestedDates);
	}
}
