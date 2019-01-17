package de.iteratec.iteraOfficeMap.reservation

import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

import java.security.Principal

@RestController
@RequestMapping("/api")
class ReservationController @Autowired constructor(private val reservationService: ReservationService) {

    @GetMapping("/allreservations")
    @ApiOperation(value = "allReservations", notes = "Returns a list of all existing reservations.")
    fun allReservations(): List<ReservationDTO> {
        return reservationService.getAllReservations()
    }

    @PostMapping("/addreservation", consumes = [MediaType.APPLICATION_JSON_VALUE])
    @ApiOperation(value = "addReservation", notes = "Adds one new reservation to the database.")
    @ApiResponses(value = [ApiResponse(code = 406, message = "Entry already exists")])
    fun addReservation(@RequestBody reservation: AddReservationDTO, principal: Principal) {
        reservationService.addReservation(reservation, principal)
    }

    @PostMapping("/addreservations", consumes = [MediaType.APPLICATION_JSON_VALUE])
    @ApiOperation(value = "addReservations", notes = "Adds a list of reservations to the database.")
    @ApiResponses(value = [ApiResponse(code = 406, message = "Entry already exists")])
    fun addReservations(@RequestBody reservations: List<AddReservationDTO>, principal: Principal) {
        reservationService.addReservations(reservations, principal)
    }

    @GetMapping("/getdailyreservations")
    @ApiOperation(value = "getDailyReservations", notes = "Returns a list of reservations for exactly one day.")
    fun getDailyReservations(@RequestParam date: Long): List<ReservationDTO> {
        return reservationService.getDailyReservations(date)
    }

    @GetMapping("/getperiodreservations")
    @ApiOperation(value = "getPeriodReservations", notes = "Returns a list of reservations for a time period.")
    fun getPeriodReservations(@RequestParam startDate: Long, endDate: Long): List<ReservationDTO> {
        return reservationService.getPeriodReservations(startDate, endDate)
    }

    @GetMapping("/getworkplacereservations")
    @ApiOperation(value = "getWorkplaceReservations", notes = "Returns a list of a workplaces reservations for a time period.")
    fun getWorkplaceReservations(@RequestParam startDate: Long, endDate: Long, workplaceId: Long): List<ReservationDTO> {
        return reservationService.getWorkplaceReservations(startDate, endDate, workplaceId)
    }

    @GetMapping("/getuserreservations")
    @ApiOperation(value = "getUserReservations", notes = "Returns a list of reservations for exactly one user.")
    fun getUserReservations(@RequestParam user: String): List<ReservationDTO> {
        return reservationService.getUserReservations(user)
    }

    @DeleteMapping("/deletereservations", consumes = [MediaType.APPLICATION_JSON_VALUE])
    @ApiOperation(value = "deleteReservations", notes = "Deletes a list of reservations from the database.")
    @ApiResponses(value = [ApiResponse(code = 200, message = "Entry has been deleted or does not exist")])
    fun deleteReservations(@RequestBody reservations: List<DeleteReservationDTO>) {
        reservationService.deleteReservations(reservations)
    }

}
