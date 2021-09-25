package io.github.pzn.campsite.api.config;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.ext.Provider;

import org.apache.cxf.jaxrs.provider.JavaTimeTypesParamConverterProvider;

/**
 * Coming from <code>org.apache.cxf:cxf-rt-frontend-jaxrs</code> but Quarkus requires
 * {@code @ApplicationScoped} for auto-detection.
 */
@ApplicationScoped
@Provider
public class QuarkusJavaTimeTypesParamConverterProvider extends JavaTimeTypesParamConverterProvider {}
