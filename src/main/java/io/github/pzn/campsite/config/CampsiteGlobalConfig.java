package io.github.pzn.campsite.config;

import java.time.Clock;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.ClockProvider;

public class CampsiteGlobalConfig {

	@ApplicationScoped
	public Clock clock() {
		return Clock.systemDefaultZone();
	}

	@ApplicationScoped
	public ClockProvider clockProvider(Clock clock) {
		return () -> clock;
	}
}
