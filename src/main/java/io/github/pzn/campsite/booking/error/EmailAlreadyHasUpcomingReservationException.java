package io.github.pzn.campsite.booking.error;

import static io.github.pzn.campsite.booking.error.CampsiteErrorCode.EMAIL_HAS_AN_UPCOMING_RESERVATION;

public class EmailAlreadyHasUpcomingReservationException extends CampsiteBackendException {

  public EmailAlreadyHasUpcomingReservationException() {
    super(EMAIL_HAS_AN_UPCOMING_RESERVATION);
  }
}
