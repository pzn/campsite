package io.github.pzn.campsite.api.error;

public enum CampsiteApiErrorCode {
  UNKNOWN("An unexpected error occurred"),
  BAD_INPUT("Bad input"),
  INCOMPATIBLE_DEPARTURE_AND_ARRIVAL_DATES("Specified departure date cannot be before the arrival date"),
  ARRIVAL_AND_DEPARTURE_DATES_ARE_THE_SAME("Departure date cannot be on the same day as the arrival date"),
  BOOKING_RANGE_EXCEEDED("Your booking exceeds the number of three (3) consecutive days");

  private final String message;

  CampsiteApiErrorCode(String message) {
    this.message = message;
  }

  public String getErrorCode() {
    return String.format("APIx%03X", ordinal());
  }

  public String getMessage() {
    return message;
  }
}
