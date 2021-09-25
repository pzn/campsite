CREATE TABLE bookings(
    id SERIAL PRIMARY KEY,
    created_on TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    last_modified TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    reservation_identifier VARCHAR(36) NOT NULL,
    email VARCHAR(500) NOT NULL,
    full_name VARCHAR(500) NOT NULL,
    arrival DATE NOT NULL,
    departure DATE NOT NULL
);
CREATE INDEX booking_reservation_identifier_idx ON bookings(reservation_identifier);
CREATE INDEX email ON bookings(email);

CREATE TABLE booking_locks(
    id SERIAL PRIMARY KEY,
    booking_day DATE NOT NULL,
    booking_id SERIAL NOT NULL,
    CONSTRAINT fk_booking
        FOREIGN KEY(booking_id)
        REFERENCES bookings(id)
);
ALTER TABLE booking_locks ADD CONSTRAINT booking_locks_booking_day_uq UNIQUE (booking_day);
