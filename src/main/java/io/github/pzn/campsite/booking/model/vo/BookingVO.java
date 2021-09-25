package io.github.pzn.campsite.booking.model.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingVO {

  Long id;
  LocalDateTime createdOn;
  LocalDateTime lastModified;
  String reservationIdentifier;
  String email;
  String fullName;
  LocalDate arrival;
  LocalDate departure;
}
