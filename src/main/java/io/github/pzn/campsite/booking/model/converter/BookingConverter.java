package io.github.pzn.campsite.booking.model.converter;

import io.github.pzn.campsite.booking.model.entity.BookingEntity;
import io.github.pzn.campsite.booking.model.vo.AlterBookingVO;
import io.github.pzn.campsite.booking.model.vo.BookingVO;
import io.github.pzn.campsite.booking.model.vo.NewBookingVO;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.constraints.NotNull;
import java.util.LinkedList;

@ApplicationScoped
public class BookingConverter {

  public @NotNull BookingEntity toBrandNewEntity(@NotNull NewBookingVO newBookingVO) {
    return BookingEntity.builder()
        .email(newBookingVO.getEmail())
        .fullName(newBookingVO.getFullName())
        .arrival(newBookingVO.getArrival())
        .departure(newBookingVO.getDeparture())
        .bookingDays(new LinkedList<>())
        .build();
  }

  public @NotNull BookingEntity toEntity(@NotNull BookingVO existingBookingVO) {
    return BookingEntity.builder()
            .id(existingBookingVO.getId())
            .createdOn(existingBookingVO.getCreatedOn())
            .lastModified(existingBookingVO.getLastModified())
            .email(existingBookingVO.getEmail())
            .fullName(existingBookingVO.getFullName())
            .arrival(existingBookingVO.getArrival())
            .departure(existingBookingVO.getDeparture())
            .bookingDays(new LinkedList<>())
            .build();
  }

  public @NotNull BookingVO toVO(@NotNull BookingEntity bookingEntity) {
    return BookingVO.builder()
        .id(bookingEntity.getId())
        .createdOn(bookingEntity.getCreatedOn())
        .lastModified(bookingEntity.getLastModified())
        .reservationIdentifier(bookingEntity.getReservationIdentifier())
        .email(bookingEntity.getEmail())
        .fullName(bookingEntity.getFullName())
        .arrival(bookingEntity.getArrival())
        .departure(bookingEntity.getDeparture())
        .build();
  }

  public void merge(
      @NotNull AlterBookingVO alterBookingVO, @NotNull BookingEntity existingBookingEntity) {
    existingBookingEntity.setArrival(alterBookingVO.getNewArrival());
    existingBookingEntity.setDeparture(alterBookingVO.getNewDeparture());
  }
}
