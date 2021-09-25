package io.github.pzn.campsite.api.error;

import static io.github.pzn.campsite.api.error.CampsiteApiErrorCode.INCOMPATIBLE_DEPARTURE_AND_ARRIVAL_DATES;

public class IncompatibleArrivalAndDepartureDatesApiException extends CampsiteApiException {

  public IncompatibleArrivalAndDepartureDatesApiException() {
    super(INCOMPATIBLE_DEPARTURE_AND_ARRIVAL_DATES);
  }
}
