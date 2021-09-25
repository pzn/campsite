package io.github.pzn.campsite.api.error.mapper;

import static io.github.pzn.campsite.api.error.CampsiteApiErrorCode.UNKNOWN;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static javax.ws.rs.core.Response.serverError;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import lombok.extern.jbosslog.JBossLog;

import io.github.pzn.campsite.api.error.CampsiteApiErrorResponse;

@ApplicationScoped
@JBossLog
@Provider
public class GenericRuntimeExceptionMapper implements ExceptionMapper<RuntimeException> {

	@Override
	public Response toResponse(RuntimeException e) {
		log.error(e.getMessage(), e);
		return serverError()
				.entity(CampsiteApiErrorResponse.of(UNKNOWN.getErrorCode(), UNKNOWN.getMessage()))
				.type(APPLICATION_JSON_TYPE)
				.build();
	}
}
