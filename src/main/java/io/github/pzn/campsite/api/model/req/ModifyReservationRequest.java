package io.github.pzn.campsite.api.model.req;

import java.time.LocalDate;

import javax.validation.constraints.Future;

import lombok.Data;

@Data
public class ModifyReservationRequest implements LocalDateRangeRequest {

  @Future
  private LocalDate arrival;
  @Future
  private LocalDate departure;

  @Override
  public LocalDate getFrom() {
    return arrival;
  }

  @Override
  public LocalDate getTo() {
    return departure;
  }
}
