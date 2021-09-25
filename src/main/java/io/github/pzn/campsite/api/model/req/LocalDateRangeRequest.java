package io.github.pzn.campsite.api.model.req;

import java.time.LocalDate;

public interface LocalDateRangeRequest {

	LocalDate getFrom();

	LocalDate getTo();
}
