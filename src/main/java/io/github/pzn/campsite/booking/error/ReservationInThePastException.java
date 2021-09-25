package io.github.pzn.campsite.booking.error;

import static io.github.pzn.campsite.booking.error.CampsiteErrorCode.RESERVATION_IN_THE_PAST;

public class ReservationInThePastException extends CampsiteBackendException {

  public ReservationInThePastException() {
    super(RESERVATION_IN_THE_PAST);
  }
}
