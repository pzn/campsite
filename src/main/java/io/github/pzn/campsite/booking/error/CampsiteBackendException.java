package io.github.pzn.campsite.booking.error;

public abstract class CampsiteBackendException extends CampsiteException {

  public CampsiteBackendException(CampsiteErrorCode errorCode) {
    super(errorCode.getErrorCode(), errorCode.getMessage());
  }

  public CampsiteBackendException(CampsiteErrorCode errorCode, Throwable cause) {
    super(errorCode.getErrorCode(), errorCode.getMessage(), cause);
  }
}
