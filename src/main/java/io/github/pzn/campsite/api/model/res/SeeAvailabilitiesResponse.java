package io.github.pzn.campsite.api.model.res;

import java.time.LocalDate;
import java.util.Collection;

import lombok.Value;

@Value(staticConstructor = "of")
public class SeeAvailabilitiesResponse {

  LocalDate availabilityFrom;
  LocalDate availabilityTo;
  Collection<LocalDate> availabilities;
}
