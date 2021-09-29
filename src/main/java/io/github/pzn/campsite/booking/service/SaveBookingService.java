package io.github.pzn.campsite.booking.service;

import io.github.pzn.campsite.booking.error.ReservationNotFoundException;
import io.github.pzn.campsite.booking.model.converter.BookingConverter;
import io.github.pzn.campsite.booking.model.entity.BookingEntity;
import io.github.pzn.campsite.booking.model.entity.BookingLockEntity;
import io.github.pzn.campsite.booking.model.vo.AlterBookingVO;
import io.github.pzn.campsite.booking.model.vo.BookingVO;
import io.github.pzn.campsite.booking.model.vo.NewBookingVO;
import io.github.pzn.campsite.booking.repository.BookingRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@ApplicationScoped
public class SaveBookingService {

  BookingConverter bookingConverter;
  BookingVerificationService bookingVerificationService;
  BookingRepository bookingRepository;
  ReadBookingService readBookingService;

  public SaveBookingService(
      BookingConverter bookingConverter,
      BookingVerificationService bookingVerificationService,
      BookingRepository bookingRepository,
      ReadBookingService readBookingService) {
    this.bookingConverter = bookingConverter;
    this.bookingVerificationService = bookingVerificationService;
    this.bookingRepository = bookingRepository;
    this.readBookingService = readBookingService;
  }

  @Transactional
  public String newBooking(@NotNull NewBookingVO newBookingVO) {
    bookingVerificationService.verifyCanNewBooking(newBookingVO);

    BookingEntity newBookingEntity = bookingConverter.toBrandNewEntity(newBookingVO);

    String reservationIdentifier = UUID.randomUUID().toString();
    newBookingEntity.setReservationIdentifier(reservationIdentifier);

    lockNewDates(newBookingEntity.getArrival(), newBookingEntity.getDeparture(), newBookingEntity);

    bookingRepository.persist(newBookingEntity);

    return reservationIdentifier;
  }

  @Transactional
  public void updateBooking(
      @NotNull String reservationIdentifier, @NotNull AlterBookingVO alterBookingVO) {
    BookingEntity existingBookingEntity = requireByReservationIdentifier(reservationIdentifier);
    BookingVO existingBookingVO = bookingConverter.toVO(existingBookingEntity);

    bookingVerificationService.verifyCanUpdateBooking(alterBookingVO, existingBookingVO);

    bookingConverter.merge(alterBookingVO, existingBookingEntity);
    releaseObsoleteDates(alterBookingVO, existingBookingEntity);
    lockNewDates(alterBookingVO.getNewArrival(), alterBookingVO.getNewDeparture(), existingBookingEntity);
  }

  private BookingEntity requireByReservationIdentifier(String reservationIdentifier) {
    return bookingRepository
        .find("reservationIdentifier", reservationIdentifier)
        .singleResultOptional()
        .orElseThrow(ReservationNotFoundException::new);
  }

  private void releaseObsoleteDates(
      AlterBookingVO alterBookingVO, BookingEntity existingBookingEntity) {
    List<LocalDate> newDates =
        alterBookingVO
            .getNewArrival()
            .datesUntil(alterBookingVO.getNewDeparture())
            .collect(toList());

    List<BookingLockEntity> obsoleteDates =
        existingBookingEntity.getBookingDays().stream()
            .collect(toMap(BookingLockEntity::getBookingDay, identity()))
            .entrySet()
            .stream()
            .filter(e -> !newDates.contains(e.getKey()))
            .map(Entry::getValue)
            .collect(toList());
    existingBookingEntity.getBookingDays().removeAll(obsoleteDates);
  }

  private void lockNewDates(
      LocalDate arrival, LocalDate departure, BookingEntity existingBookingEntity) {
    List<LocalDate> newDates = arrival.datesUntil(departure).collect(toList());

    List<BookingLockEntity> newDatesToLock =
        newDates.stream()
            .map(
                newDate ->
                    BookingLockEntity.builder()
                        .bookingDay(newDate)
                        .booking(existingBookingEntity)
                        .build())
            .collect(toList());

    existingBookingEntity.getBookingDays().addAll(newDatesToLock);
  }

  @Transactional
  public void deleteBooking(@NotNull String reservationIdentifier) {
    BookingEntity bookingEntity =
        bookingRepository
            .find("reservationIdentifier", reservationIdentifier)
            .singleResultOptional()
            .orElseThrow(ReservationNotFoundException::new);

    bookingRepository.delete(bookingEntity);
  }
}
