package io.github.pzn.campsite.api.error;

import io.github.pzn.campsite.booking.error.CampsiteException;

public abstract class CampsiteApiException extends CampsiteException {

  public CampsiteApiException(CampsiteApiErrorCode apiErrorCode) {
    super(apiErrorCode.getErrorCode(), apiErrorCode.getMessage());
  }

  public CampsiteApiException(CampsiteApiErrorCode apiErrorCode, Throwable cause) {
    super(apiErrorCode.getErrorCode(), apiErrorCode.getMessage(), cause);
  }
}
