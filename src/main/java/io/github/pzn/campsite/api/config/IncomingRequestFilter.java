package io.github.pzn.campsite.api.config;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

import lombok.extern.jbosslog.JBossLog;

import org.apache.cxf.helpers.IOUtils;

@ApplicationScoped
@Provider
@JBossLog
public class IncomingRequestFilter implements ContainerRequestFilter {

  @Override
  public void filter(ContainerRequestContext ctx) throws IOException {
    if (log.isDebugEnabled()) {
      log.debug(
          "Incoming " + ctx.getMethod() + " " + ctx.getUriInfo().getPath() + ": " + extract(ctx));
    }
  }

  private String extract(ContainerRequestContext ctx) throws IOException {
    try (InputStream in = ctx.getEntityStream();
        ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      if (in.available() > 0) {
        IOUtils.copy(in, out);
      }
      byte[] requestEntity = out.toByteArray();
      ctx.setEntityStream(new ByteArrayInputStream(requestEntity));
      return out.toString();
    }
  }
}
