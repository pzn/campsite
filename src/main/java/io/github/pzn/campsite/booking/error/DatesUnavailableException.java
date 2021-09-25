package io.github.pzn.campsite.booking.error;

import static io.github.pzn.campsite.booking.error.CampsiteErrorCode.DATES_UNAVAILABLE;

public class DatesUnavailableException extends CampsiteBackendException {

  public DatesUnavailableException() {
    super(DATES_UNAVAILABLE);
  }
}
