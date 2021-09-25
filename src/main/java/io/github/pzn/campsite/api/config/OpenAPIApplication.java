package io.github.pzn.campsite.api.config;

import static io.github.pzn.campsite.api.config.OpenAPIApplication.AVAILABILITIES_TAG;
import static io.github.pzn.campsite.api.config.OpenAPIApplication.BOOKING_TAG;

import javax.ws.rs.core.Application;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@OpenAPIDefinition(
    tags = {
      @Tag(name = BOOKING_TAG, description = "Booking operations."),
      @Tag(name = AVAILABILITIES_TAG, description = "Operations related to date availabilities")
    },
    info =
        @Info(
            title = "Campsite API",
            version = "0.0.1-SNAPSHOT",
            contact = @Contact(name = "Christophe Lé", url = "https://pzn.github.io/resume")))
public class OpenAPIApplication extends Application {

  public static final String BOOKING_TAG = "booking";
  public static final String AVAILABILITIES_TAG = "availabilities";
}
