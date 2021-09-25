package io.github.pzn.campsite.api.error;

import lombok.Value;

@Value(staticConstructor = "of")
public class CampsiteApiErrorResponse {

  String errorCode;
  String message;
}
