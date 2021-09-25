package io.github.pzn.campsite.booking.error;

import static io.github.pzn.campsite.booking.error.CampsiteErrorCode.DATES_UNAVAILABLE_FOR_RESERVATION;

public class DatesUnavailableForReservationException extends CampsiteBackendException {

  public DatesUnavailableForReservationException() {
    super(DATES_UNAVAILABLE_FOR_RESERVATION);
  }
}
