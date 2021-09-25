package io.github.pzn.campsite.api.model.req;

import java.time.LocalDate;

import javax.ws.rs.QueryParam;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeeAvailabilitiesRequest implements LocalDateRangeRequest {

  @QueryParam("from")
  private LocalDate from;

  @QueryParam("to")
  private LocalDate to;
}
