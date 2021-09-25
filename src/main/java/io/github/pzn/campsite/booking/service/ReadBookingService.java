package io.github.pzn.campsite.booking.service;

import io.github.pzn.campsite.booking.model.converter.BookingConverter;
import io.github.pzn.campsite.booking.model.vo.BookingVO;
import io.github.pzn.campsite.booking.repository.BookingRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;

import static java.util.stream.Collectors.toUnmodifiableList;

@ApplicationScoped
public class ReadBookingService {

  Clock clock;
  BookingRepository bookingRepository;
  BookingConverter bookingConverter;

  public ReadBookingService(
      Clock clock, BookingRepository bookingRepository, BookingConverter bookingConverter) {
    this.clock = clock;
    this.bookingRepository = bookingRepository;
    this.bookingConverter = bookingConverter;
  }

  @Transactional
  public boolean emailHasUpcomingBooking(@NotNull String email) {
    return bookingRepository.count("email = ?1 AND arrival > ?2", email, now()) != 0;
  }

  @Transactional
  public boolean reservationExists(@NotNull String reservationIdentifier) {
    return bookingRepository.count("reservationIdentifier", reservationIdentifier) != 0;
  }

  @Transactional
  public boolean reservationIsInThePart(@NotNull String reservationIdentifier) {
    return bookingRepository.count(
            "reservationIdentifier = ?1 AND arrival <= ?2", reservationIdentifier, now())
        != 0;
  }

  @Transactional
  public Collection<BookingVO> findAllBookingsBetween(
      @NotNull LocalDate from, @NotNull LocalDate to) {
    return bookingRepository.find("departure >= ?1 OR departure < ?2", from, to).stream()
        .map(bookingConverter::toVO)
        .collect(toUnmodifiableList());
  }

  @Transactional
  public Collection<BookingVO> findAllBookingsExcludingReservationBetween(
      @NotNull String reservationIdentifierToExclude,
      @NotNull LocalDate from,
      @NotNull LocalDate to) {
    return bookingRepository
        .find(
            "(departure >= ?1 OR departure < ?2) AND reservationIdentifier != ?3",
            from,
            to,
            reservationIdentifierToExclude)
        .stream()
        .map(bookingConverter::toVO)
        .collect(toUnmodifiableList());
  }

  private LocalDate now() {
    return LocalDate.ofInstant(clock.instant(), ZoneId.of("UTC"));
  }
}
