package de.iteratec.iteraOfficeMap.reservation

import de.iteratec.iteraOfficeMap.utility.endOfDayMillis
import de.iteratec.iteraOfficeMap.utility.millisToGermanLocalDate
import de.iteratec.iteraOfficeMap.utility.startOfDayMillis
import de.iteratec.iteraOfficeMap.workplace.WorkplaceService
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/api")
class ReservationController
@Autowired constructor(
        private val workplaceService: WorkplaceService,
        private val reservationService: ReservationService
) {

    @GetMapping("/allreservations")
    @ApiOperation(value = "allReservations", notes = "Returns a list of all existing reservations.")
    fun allReservations(): List<ReservationDTO> {
        return reservationService.getAllReservations()
                .map { it.toReservationDTO() }
    }

    @PostMapping("/addreservation", consumes = [MediaType.APPLICATION_JSON_VALUE])
    @ApiOperation(value = "addReservation", notes = "Adds one new reservation to the database.")
    @ApiResponses(value = [ApiResponse(code = 406, message = "Entry already exists")])
    fun addReservation(@RequestBody reservation: AddReservationDTO, principal: Principal) {
        addReservations(listOf(reservation), principal)
    }

    @PostMapping("/addreservations", consumes = [MediaType.APPLICATION_JSON_VALUE])
    @ApiOperation(value = "addReservations", notes = "Adds a list of reservations to the database.")
    @ApiResponses(value = [ApiResponse(code = 406, message = "Entry already exists")])
    fun addReservations(@RequestBody reservationDTOs: List<AddReservationDTO>, principal: Principal) {
        if (reservationDTOs.isEmpty()) {
            return
        }

        val workplaceId = reservationDTOs.first().workplaceId
        require(reservationDTOs.all { it.workplaceId == workplaceId })
        val workplace = workplaceService.findById(workplaceId)

        reservationService.addReservations(
                reservationDTOs.map { reservationDTO ->
                    Reservation(
                            null,
                            reservationDTO.startDate.millisToGermanLocalDate(),
                            reservationDTO.endDate.millisToGermanLocalDate(),
                            principal.name,
                            workplace
                    )
                }
        )
    }

    @GetMapping("/getdailyreservations")
    @ApiOperation(value = "getDailyReservations", notes = "Returns a list of reservations for exactly one day.")
    fun getDailyReservations(@RequestParam date: Long): List<ReservationDTO> {
        return reservationService.getDailyReservations(date.millisToGermanLocalDate())
    }

    @GetMapping("/getperiodreservations")
    @ApiOperation(value = "getPeriodReservations", notes = "Returns a list of reservations for a time period.")
    fun getPeriodReservations(@RequestParam startDate: Long, endDate: Long): List<ReservationDTO> {
        return reservationService
                .getPeriodReservations(startDate.millisToGermanLocalDate(), endDate.millisToGermanLocalDate())
                .map { it.toReservationDTO() }
    }

    @GetMapping("/getworkplacereservations")
    @ApiOperation(value = "getWorkplaceReservations", notes = "Returns a list of a workplaces reservations for a time period.")
    fun getWorkplaceReservations(@RequestParam startDate: Long, endDate: Long, workplaceId: Long): List<ReservationDTO> {
        return reservationService
                .getWorkplaceReservations(
                        startDate.millisToGermanLocalDate(),
                        endDate.millisToGermanLocalDate(),
                        workplaceId
                )
                .map { it.toReservationDTO() }
    }

    @GetMapping("/getuserreservations")
    @ApiOperation(value = "getUserReservations", notes = "Returns a list of reservations for exactly one user.")
    fun getUserReservations(@RequestParam user: String): List<ReservationDTO> {
        return reservationService.getUserReservations(user)
                .map { it.toReservationDTO() }
    }

    @DeleteMapping("/deletereservations", consumes = [MediaType.APPLICATION_JSON_VALUE])
    @ApiOperation(value = "deleteReservations", notes = "Deletes a list of reservations from the database.")
    @ApiResponses(value = [ApiResponse(code = 200, message = "Entry has been deleted or does not exist")])
    fun deleteReservations(@RequestBody deleteReservationDTOs: List<DeleteReservationDTO>) {
        reservationService.deleteReservations(deleteReservationDTOs.map {
            DeleteReservationRequest(
                    it.workplaceId,
                    it.startDate.millisToGermanLocalDate(),
                    it.endDate.millisToGermanLocalDate()
            )
        })
    }

}

fun Reservation.toReservationDTO() = ReservationDTO(
        id!!,
        startDate.startOfDayMillis(),
        endDate.endOfDayMillis(),
        username,
        workplace
)
