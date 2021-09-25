package io.github.pzn.campsite.api.model.res;

import lombok.Value;

@Value(staticConstructor = "of")
public class PlaceReservationResponse {

  String reservationIdentifier;
}
