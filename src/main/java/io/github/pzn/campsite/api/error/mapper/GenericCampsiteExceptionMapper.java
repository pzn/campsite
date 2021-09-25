package io.github.pzn.campsite.api.error.mapper;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import io.github.pzn.campsite.api.error.CampsiteApiErrorResponse;
import io.github.pzn.campsite.booking.error.CampsiteException;

@ApplicationScoped
@Provider
public class GenericCampsiteExceptionMapper implements ExceptionMapper<CampsiteException> {

	@Override
	public Response toResponse(CampsiteException e) {
		return Response.status(BAD_REQUEST)
				.entity(CampsiteApiErrorResponse.of(e.getErrorCode(), e.getMessage()))
				.type(APPLICATION_JSON_TYPE)
				.build();
	}
}
