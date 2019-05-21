package de.iteratec.iteraOfficeMap;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ReservationController {

    private final ReservationService reservationService;

    @Autowired
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @ApiOperation(value = "allReservations", notes = "Returns a list of all existing Reserverations.")
    @RequestMapping(method = RequestMethod.GET, value = "/allreservations")
    public List<ReservationDTO> allReservations() throws Exception {
        return reservationService.getAllReservations();
    }

    @ApiOperation(value = "addReservation", notes = "Adds one new reservation to the database.")
    @RequestMapping(method = RequestMethod.POST, value = "/addreservation", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {@ApiResponse(code = 406, message = "Entry already exists")})
    public void addReservation(@RequestBody AddReservationDTO reservation, Principal principal) {
        reservationService.addReservation(reservation, principal);
    }

    @ApiOperation(value = "addReservations", notes = "Adds a list of reservations to the database.")
    @RequestMapping(method = RequestMethod.POST, value = "/addreservations", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {@ApiResponse(code = 406, message = "Entry already exists")})
    public void addReservations(
            @RequestBody List<AddReservationDTO> reservations, Principal principal) {
        reservationService.addReservations(reservations, principal);
    }

    @ApiOperation(value = "getDailyReservations", notes = "Returns a list of reservations for exactly one day.")
    @RequestMapping(method = RequestMethod.GET, value = "/getdailyreservations")
    public List<ReservationDTO> getDailyReservations(@RequestParam Long date) {
        return reservationService.getDailyReservations(date);
    }

    @ApiOperation(value = "getPeriodReservations", notes = "Returns a list of reservations for a time period.")
    @RequestMapping(method = RequestMethod.GET, value = "/getperiodreservations")
    public List<ReservationDTO> getPeriodReservations(
            @RequestParam Long startDate, Long endDate) {
        return reservationService.getPeriodReservations(startDate, endDate);
    }

    @ApiOperation(value = "getWorkplaceReservations", notes = "Returns a list of a workplaces reservations for a time period.")
    @RequestMapping(method = RequestMethod.GET, value = "/getworkplacereservations")
    public List<ReservationDTO> getWorkplaceReservations(
            @RequestParam Long startDate, Long endDate, Long workplaceId) {
        return reservationService.getWorkplaceReservations(startDate, endDate,
                workplaceId);
    }

    @ApiOperation(value = "getUserReservations", notes = "Returns a list of reservations for exactly one user.")
    @RequestMapping(method = RequestMethod.GET, value = "/getuserreservations")
    public List<ReservationDTO> getUserReservations(@RequestParam String user) {
        return reservationService.getUserReservations(user);
    }

    @ApiOperation(value = "deleteReservations", notes = "Deletes a list of reservations from the database.")
    @RequestMapping(method = RequestMethod.DELETE, value = "/deletereservations", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Entry has been deleted or does not exist")})
    public void deleteReservations(
            @RequestBody List<DeleteReservationDTO> reservations) {
        reservationService.deleteReservations(reservations);
    }

}
