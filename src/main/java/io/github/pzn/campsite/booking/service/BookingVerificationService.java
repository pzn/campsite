package io.github.pzn.campsite.booking.service;

import java.time.Clock;
import java.time.LocalDate;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.constraints.NotNull;

import io.github.pzn.campsite.booking.error.DatesUnavailableException;
import io.github.pzn.campsite.booking.error.DatesUnavailableForReservationException;
import io.github.pzn.campsite.booking.error.EmailAlreadyHasUpcomingReservationException;
import io.github.pzn.campsite.booking.model.vo.AlterBookingVO;
import io.github.pzn.campsite.booking.model.vo.BookingVO;
import io.github.pzn.campsite.booking.model.vo.NewBookingVO;

@ApplicationScoped
public class BookingVerificationService {

  Clock clock;
  ReadBookingService readBookingService;
  AvailabilityService availabilityService;

  public BookingVerificationService(
    Clock clock, ReadBookingService readBookingService, AvailabilityService availabilityService) {
    this.clock = clock;
    this.readBookingService = readBookingService;
    this.availabilityService = availabilityService;
  }

  public void verifyCanNewBooking(@NotNull NewBookingVO newBookingVO) {
    throwExceptionIfEmailAlreadyAssociatedToUpcomingBooking(newBookingVO);
    throwExceptionIfRangeUnavailable(newBookingVO.getArrival(), newBookingVO.getDeparture());
  }

  public void verifyCanUpdateBooking(
      @NotNull AlterBookingVO alterBookingVO, @NotNull BookingVO existingBookingVO) {
    throwExceptionIfRangeUnavailableFor(alterBookingVO, existingBookingVO);
  }

  private void throwExceptionIfEmailAlreadyAssociatedToUpcomingBooking(NewBookingVO newBookingVO) {
    if (readBookingService.emailHasUpcomingBooking(newBookingVO.getEmail())) {
      throw new EmailAlreadyHasUpcomingReservationException();
    }
  }

  private void throwExceptionIfRangeUnavailable(LocalDate from, LocalDate to) {
    if (!availabilityService.isRangeAvailable(from, to)) {
      throw new DatesUnavailableException();
    }
  }

  private void throwExceptionIfRangeUnavailableFor(AlterBookingVO alterBookingVO, BookingVO existingBookingVO) {
    if (!availabilityService.isRangeAvailableFor(existingBookingVO.getReservationIdentifier(), alterBookingVO.getNewArrival(), alterBookingVO.getNewDeparture())) {
      throw new DatesUnavailableForReservationException();
    }
  }
}
