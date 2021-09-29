package io.github.pzn.campsite.booking.model.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.SEQUENCE;

@Entity(name = "bookings")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingEntity {

  @Id
  @GeneratedValue(strategy = SEQUENCE, generator = "bookings_id_seq")
  private Long id;
  @CreationTimestamp
  @Column(name = "created_on")
  private LocalDateTime createdOn;
  @UpdateTimestamp
  @Column(name = "last_modified")
  private LocalDateTime lastModified;
  @Column(name = "reservation_identifier")
  private String reservationIdentifier;
  private String email;
  @Column(name = "full_name")
  private String fullName;
  private LocalDate arrival;
  private LocalDate departure;
  @OneToMany(fetch = LAZY, cascade = ALL, orphanRemoval = true, mappedBy = "booking")
  private Collection<BookingLockEntity> bookingDays;
}
