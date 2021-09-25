package io.github.pzn.campsite.booking.model.vo;

import java.time.LocalDate;

import lombok.Value;

@Value(staticConstructor = "of")
public class NewBookingVO {

  String email;
  String fullName;
  LocalDate arrival;
  LocalDate departure;
}
