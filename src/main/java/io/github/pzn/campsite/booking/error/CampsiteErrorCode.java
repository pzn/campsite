package io.github.pzn.campsite.booking.error;

public enum CampsiteErrorCode {
  UNKNOWN("An unexpected error occurred"),
  EMAIL_HAS_AN_UPCOMING_RESERVATION("E-mail is already associated to an upcoming reservation"),
  DATES_UNAVAILABLE("Some dates in your reservation have been booked"),
  DATES_UNAVAILABLE_FOR_RESERVATION("Some dates in your reservation have been booked"),
  RESERVATION_NOT_FOUND("Reservation does not to exist"),
  RESERVATION_IN_THE_PAST("Reservation does not exist or already passed");

  private final String message;

  CampsiteErrorCode(String message) {
    this.message = message;
  }

  public String getErrorCode() {
    return String.format("BEx%03X", ordinal());
  }

  public String getMessage() {
    return message;
  }
}
