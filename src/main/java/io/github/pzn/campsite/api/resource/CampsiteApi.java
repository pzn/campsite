package io.github.pzn.campsite.api.resource;

import static io.github.pzn.campsite.api.config.OpenAPIApplication.AVAILABILITIES_TAG;
import static io.github.pzn.campsite.api.config.OpenAPIApplication.BOOKING_TAG;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.time.LocalDate;
import java.util.Collection;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import io.github.pzn.campsite.api.model.req.ModifyReservationRequest;
import io.github.pzn.campsite.api.model.req.PlaceReservationRequest;
import io.github.pzn.campsite.api.model.req.SeeAvailabilitiesRequest;
import io.github.pzn.campsite.api.model.res.ModifyReservationResponse;
import io.github.pzn.campsite.api.model.res.PlaceReservationResponse;
import io.github.pzn.campsite.api.model.res.SeeAvailabilitiesResponse;
import io.github.pzn.campsite.api.service.CampsiteApiVerificationService;
import io.github.pzn.campsite.booking.model.vo.AlterBookingVO;
import io.github.pzn.campsite.booking.model.vo.NewBookingVO;
import io.github.pzn.campsite.booking.service.AvailabilityService;
import io.github.pzn.campsite.booking.service.ReadBookingService;
import io.github.pzn.campsite.booking.service.SaveBookingService;

@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Path("/campsite")
public class CampsiteApi {

  CampsiteApiVerificationService campsiteApiVerificationService;
  AvailabilityService availabilityService;
  ReadBookingService readBookingService;
  SaveBookingService saveBookingService;

  public CampsiteApi(
      CampsiteApiVerificationService campsiteApiVerificationService,
      AvailabilityService availabilityService,
      ReadBookingService readBookingService,
      SaveBookingService saveBookingService) {
    this.campsiteApiVerificationService = campsiteApiVerificationService;
    this.availabilityService = availabilityService;
    this.readBookingService = readBookingService;
    this.saveBookingService = saveBookingService;
  }

  @GET
  @Path("/availabilities")
  @Tag(name = AVAILABILITIES_TAG)
  @APIResponses({
          @APIResponse(responseCode = "200", description = "OK"),
          @APIResponse(responseCode = "400", description = "User request malformed or invalid"),
          @APIResponse(responseCode = "500", description = "Unexpected error")
  })
  public SeeAvailabilitiesResponse seeAvailabilities(
      @Valid @BeanParam SeeAvailabilitiesRequest req) {
    req = campsiteApiVerificationService.transformWithDefaultValuesWhenRequired(req);
    campsiteApiVerificationService.verifySeeAvailabilitiesRequest(req);

    Collection<LocalDate> availableDates =
        availabilityService.findAvailableDates(req.getFrom(), req.getTo());

    return SeeAvailabilitiesResponse.of(req.getFrom(), req.getTo(), availableDates);
  }

  @POST
  @Path("/reservation")
  @Tag(name = BOOKING_TAG)
  @APIResponses({
          @APIResponse(responseCode = "200", description = "Reservation placed"),
          @APIResponse(responseCode = "400", description = "User request malformed or invalid"),
          @APIResponse(responseCode = "500", description = "Unexpected error")
  })
  public PlaceReservationResponse placeReservation(@Valid PlaceReservationRequest req) {
    campsiteApiVerificationService.verifyPlaceReservationRequest(req);

    String reservationIdentifier =
        saveBookingService.newBooking(
            NewBookingVO.of(
                req.getEmail(), req.getFullName(), req.getArrival(), req.getDeparture()));

    return PlaceReservationResponse.of(reservationIdentifier);
  }

  @PATCH
  @Path("/reservation/{reservationIdentifier}")
  @Tag(name = BOOKING_TAG)
  @APIResponses({
          @APIResponse(responseCode = "200", description = "Reservation updated"),
          @APIResponse(responseCode = "400", description = "User request malformed or invalid"),
          @APIResponse(responseCode = "500", description = "Unexpected error")
  })
  public ModifyReservationResponse modifyReservation(
      @PathParam("reservationIdentifier") @NotEmpty String reservationIdentifier,
      @Valid ModifyReservationRequest req) {
    campsiteApiVerificationService.verifyModifyReservationRequest(reservationIdentifier, req);

    saveBookingService.updateBooking(
        reservationIdentifier, AlterBookingVO.of(req.getArrival(), req.getDeparture()));

    return ModifyReservationResponse.of(reservationIdentifier);
  }

  @DELETE
  @Path("/reservation/{reservationIdentifier}")
  @Tag(name = BOOKING_TAG)
  @APIResponses({
          @APIResponse(responseCode = "204", description = "Reservation cancelled"),
          @APIResponse(responseCode = "400", description = "User request malformed or invalid"),
          @APIResponse(responseCode = "500", description = "Unexpected error")
  })
  public void deleteReservation(
      @PathParam("reservationIdentifier") @NotEmpty String reservationIdentifier) {
    campsiteApiVerificationService.verifyDeleteReservationRequest(reservationIdentifier);

    saveBookingService.deleteBooking(reservationIdentifier);
  }
}
