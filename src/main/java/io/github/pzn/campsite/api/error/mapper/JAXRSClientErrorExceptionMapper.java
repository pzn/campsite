package io.github.pzn.campsite.api.error.mapper;

import static io.github.pzn.campsite.api.error.CampsiteApiErrorCode.BAD_INPUT;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import lombok.extern.jbosslog.JBossLog;

import io.github.pzn.campsite.api.error.CampsiteApiErrorResponse;

@ApplicationScoped
@JBossLog
@Provider
public class JAXRSClientErrorExceptionMapper implements ExceptionMapper<ClientErrorException> {

	@Override
	public Response toResponse(ClientErrorException e) {
		log.warn(e);
		return Response.status(BAD_REQUEST)
				.entity(CampsiteApiErrorResponse.of(BAD_INPUT.getErrorCode(), e.getResponse().getStatusInfo().getReasonPhrase()))
				.type(APPLICATION_JSON_TYPE)
				.build();
	}
}
