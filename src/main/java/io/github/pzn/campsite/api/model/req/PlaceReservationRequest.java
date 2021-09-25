package io.github.pzn.campsite.api.model.req;

import java.time.LocalDate;

import javax.validation.constraints.Email;
import javax.validation.constraints.Future;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class PlaceReservationRequest implements LocalDateRangeRequest {

  @Email(message = "does not seem to be valid, or cannot exceed 500 characters")
  private String email;
  @Size(min = 3, max = 500, message = "must be between 3 and 500 characters")
  private String fullName;
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
