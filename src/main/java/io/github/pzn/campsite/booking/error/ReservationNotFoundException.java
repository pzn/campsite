package io.github.pzn.campsite.booking.error;

import static io.github.pzn.campsite.booking.error.CampsiteErrorCode.RESERVATION_NOT_FOUND;

public class ReservationNotFoundException extends CampsiteBackendException {

  public ReservationNotFoundException() {
    super(RESERVATION_NOT_FOUND);
  }
}
