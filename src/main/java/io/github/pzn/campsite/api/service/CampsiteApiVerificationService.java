package io.github.pzn.campsite.api.service;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.MONTHS;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.constraints.NotNull;

import lombok.extern.jbosslog.JBossLog;

import io.github.pzn.campsite.api.error.BookingRangeExceededApiException;
import io.github.pzn.campsite.api.error.IncompatibleArrivalAndDepartureDatesApiException;
import io.github.pzn.campsite.api.model.req.LocalDateRangeRequest;
import io.github.pzn.campsite.api.model.req.ModifyReservationRequest;
import io.github.pzn.campsite.api.model.req.PlaceReservationRequest;
import io.github.pzn.campsite.api.model.req.SeeAvailabilitiesRequest;
import io.github.pzn.campsite.booking.error.ReservationInThePastException;
import io.github.pzn.campsite.booking.service.ReadBookingService;

@ApplicationScoped
@JBossLog
public class CampsiteApiVerificationService {

  Clock clock;
  ReadBookingService readBookingService;

  public CampsiteApiVerificationService(Clock clock, ReadBookingService readBookingService) {
    this.clock = clock;
    this.readBookingService = readBookingService;
  }

  public @NotNull SeeAvailabilitiesRequest transformWithDefaultValuesWhenRequired(@NotNull SeeAvailabilitiesRequest req) {
    LocalDate now = LocalDate.ofInstant(clock.instant(), ZoneId.of("UTC"));

    LocalDate from = Optional.ofNullable(req.getFrom())
            .filter(givenFrom -> givenFrom.isAfter(now))
            .orElseGet(() -> now.plus(1, DAYS));

    LocalDate to = Optional.ofNullable(req.getTo())
            .filter(givenTo -> givenTo.isAfter(from))
            .orElseGet(() -> from.plus(1, MONTHS));

    // In case the default 'from' has been too broadly defined if not provided, reduce it down to a maximum of 31 days
    LocalDate adjustedFrom = from.datesUntil(to).count() > 31 ? to.minus(1, MONTHS) : from;

    SeeAvailabilitiesRequest transformedReq = SeeAvailabilitiesRequest.builder()
      .from(adjustedFrom)
      .to(to)
      .build();

    if (!transformedReq.equals(req)) {
      log.info(req + " will be assigned the following values: from=" + from + ", to=" + to);
    }
    return transformedReq;
  }

  public void verifySeeAvailabilitiesRequest(@NotNull SeeAvailabilitiesRequest req) {
    throwExceptionIfInvalidLocalDateRange(req);
  }

  public void verifyPlaceReservationRequest(@NotNull PlaceReservationRequest req) {
    throwExceptionIfInvalidLocalDateRange(req);
    throwExceptionIfArrivalIsSameAsDeparture(req);
    throwExceptionIfRangeIsMoreThanThreeDays(req);
  }

  public void verifyModifyReservationRequest(@NotNull String reservationIdentifier, @NotNull ModifyReservationRequest req) {
    throwExceptionIfReservationInThePast(reservationIdentifier);
    throwExceptionIfInvalidLocalDateRange(req);
    throwExceptionIfArrivalIsSameAsDeparture(req);
    throwExceptionIfRangeIsMoreThanThreeDays(req);
  }

  public void verifyDeleteReservationRequest(@NotNull String reservationIdentifier) {
    throwExceptionIfReservationInThePast(reservationIdentifier);
  }

  private void throwExceptionIfInvalidLocalDateRange(LocalDateRangeRequest req) {
    if (req.getFrom().isAfter(req.getTo())) {
      throw new IncompatibleArrivalAndDepartureDatesApiException();
    }
  }

  private void throwExceptionIfArrivalIsSameAsDeparture(LocalDateRangeRequest req) {
    if (req.getFrom().isEqual(req.getTo())) {
      throw new IncompatibleArrivalAndDepartureDatesApiException();
    }
  }

  private void throwExceptionIfRangeIsMoreThanThreeDays(LocalDateRangeRequest req) {
    if (req.getFrom().datesUntil(req.getTo()).count() > 3) {
      throw new BookingRangeExceededApiException();
    }
  }

  private void throwExceptionIfReservationInThePast(String reservationIdentifier) {
    if (readBookingService.reservationIsInThePart(reservationIdentifier)) {
      throw new ReservationInThePastException();
    }
  }
}
