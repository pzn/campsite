package io.github.pzn.campsite.booking.model.entity;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.SEQUENCE;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "booking_locks")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingLockEntity {

  @Id
  @GeneratedValue(strategy = SEQUENCE, generator = "bl_generator")
  @SequenceGenerator(name = "bl_generator", sequenceName = "booking_locks_id_seq")
  private Long id;

  @Column(name = "booking_day")
  private LocalDate bookingDay;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "booking_id")
  private BookingEntity booking;
}
