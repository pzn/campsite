package io.github.pzn.campsite.booking.model.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.SEQUENCE;

@Entity(name = "booking_locks")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingLockEntity {

  @Id
  @GeneratedValue(strategy = SEQUENCE, generator = "booking_locks_id_seq")
  private Long id;

  @Column(name = "booking_day")
  private LocalDate bookingDay;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "booking_id")
  private BookingEntity booking;
}
