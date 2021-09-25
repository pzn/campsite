package io.github.pzn.campsite.booking.repository;

import javax.enterprise.context.ApplicationScoped;

import io.github.pzn.campsite.booking.model.entity.BookingLockEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class BookingLockRepository implements PanacheRepository<BookingLockEntity> {}
