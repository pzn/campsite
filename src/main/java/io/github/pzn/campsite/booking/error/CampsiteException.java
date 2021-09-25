package io.github.pzn.campsite.booking.error;

public abstract class CampsiteException extends RuntimeException {

  private final String errorCode;

  public CampsiteException(String errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }

  public CampsiteException(String errorCode, String message, Throwable cause) {
    super(message, cause);
    this.errorCode = errorCode;
  }

  public String getErrorCode() {
    return errorCode;
  }
}
