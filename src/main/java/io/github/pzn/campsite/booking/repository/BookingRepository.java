package io.github.pzn.campsite.booking.repository;

import javax.enterprise.context.ApplicationScoped;

import io.github.pzn.campsite.booking.model.entity.BookingEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class BookingRepository implements PanacheRepository<BookingEntity> {}
