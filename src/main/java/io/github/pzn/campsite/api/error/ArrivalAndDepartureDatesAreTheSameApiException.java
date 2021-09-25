package io.github.pzn.campsite.api.error;

import static io.github.pzn.campsite.api.error.CampsiteApiErrorCode.ARRIVAL_AND_DEPARTURE_DATES_ARE_THE_SAME;

public class ArrivalAndDepartureDatesAreTheSameApiException extends CampsiteApiException {

  public ArrivalAndDepartureDatesAreTheSameApiException() {
    super(ARRIVAL_AND_DEPARTURE_DATES_ARE_THE_SAME);
  }
}
