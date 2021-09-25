package io.github.pzn.campsite.api.error.mapper;

import static io.github.pzn.campsite.api.error.CampsiteApiErrorCode.BAD_INPUT;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

import java.text.MessageFormat;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import io.github.pzn.campsite.api.error.CampsiteApiErrorResponse;

@ApplicationScoped
@Provider
@Produces(APPLICATION_JSON)
public class ConstraintViolationExceptionMapper
    implements ExceptionMapper<ConstraintViolationException> {

  @Override
  public Response toResponse(ConstraintViolationException e) {
    ConstraintViolation<?> firstViolation = e.getConstraintViolations().iterator().next();
    String queryParam = getQueryParamName(firstViolation);
    String errorMessage = firstViolation.getMessage();

    return Response.status(BAD_REQUEST)
        .entity(CampsiteApiErrorResponse.of(BAD_INPUT.getErrorCode(), MessageFormat.format("Parameter ''{0}'' {1}", queryParam, errorMessage)))
        .type(APPLICATION_JSON_TYPE)
        .build();
  }

  private String getQueryParamName(ConstraintViolation<?> firstViolation) {
    String[] splitted = firstViolation.getPropertyPath().toString().split("\\.");
    return splitted[splitted.length - 1];
  }
}
