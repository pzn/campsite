package io.github.pzn.campsite.booking.job;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

import lombok.extern.jbosslog.JBossLog;

import io.github.pzn.campsite.booking.repository.BookingLockRepository;
import io.quarkus.scheduler.Scheduled;

@ApplicationScoped
@JBossLog
public class ClearBookingLocksJob {

	BookingLockRepository bookingLockRepository;

	public ClearBookingLocksJob(BookingLockRepository bookingLockRepository) {
		this.bookingLockRepository = bookingLockRepository;
	}

	@Scheduled(cron = "{campsite.booking.job.clear-locks.cron}")
	@Transactional
	void clearBookingLocks() {
		log.info("Triggered!");
		bookingLockRepository.deleteAll();
	}
}
