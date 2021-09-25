package io.github.pzn.campsite.api.model.req;

import javax.validation.constraints.NotEmpty;
import javax.ws.rs.PathParam;

import lombok.Data;

@Data
public class CancelReservationRequest {

  @PathParam("reservationIdentifier")
  @NotEmpty
  private String reservationIdentifier;
}
