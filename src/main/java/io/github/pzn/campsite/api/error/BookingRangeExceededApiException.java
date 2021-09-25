package io.github.pzn.campsite.api.error;

import static io.github.pzn.campsite.api.error.CampsiteApiErrorCode.BOOKING_RANGE_EXCEEDED;

public class BookingRangeExceededApiException extends CampsiteApiException {

  public BookingRangeExceededApiException() {
    super(BOOKING_RANGE_EXCEEDED);
  }
}
