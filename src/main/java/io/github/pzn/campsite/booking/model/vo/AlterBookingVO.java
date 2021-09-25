package io.github.pzn.campsite.booking.model.vo;

import java.time.LocalDate;

import lombok.Value;

@Value(staticConstructor = "of")
public class AlterBookingVO {

  LocalDate newArrival;
  LocalDate newDeparture;
}
