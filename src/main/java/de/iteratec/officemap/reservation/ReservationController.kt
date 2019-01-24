package de.iteratec.officemap.reservation

import de.iteratec.officemap.workplace.Workplace
import de.iteratec.officemap.workplace.WorkplaceService
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.format.annotation.DateTimeFormat.ISO.DATE
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import java.security.Principal
import java.time.LocalDate

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
                            reservationDTO.startDate,
                            reservationDTO.endDate,
                            principal.name,
                            workplace
                    )
                }
        )
    }

    @GetMapping("/getperiodreservations")
    @ApiOperation(value = "getPeriodReservations", notes = "Returns a list of reservations for a time period.")
    fun getPeriodReservations(
            @RequestParam @DateTimeFormat(iso = DATE) startDate: LocalDate,
            @RequestParam @DateTimeFormat(iso = DATE) endDate: LocalDate
    ): List<ReservationDTO> {
        return reservationService
                .getPeriodReservations(startDate, endDate)
                .map { it.toReservationDTO() }
    }

    @GetMapping("/getworkplacereservations")
    @ApiOperation(value = "getWorkplaceReservations", notes = "Returns a list of a workplaces reservations for a time period.")
    fun getWorkplaceReservations(
            @RequestParam @DateTimeFormat(iso = DATE) startDate: LocalDate,
            @RequestParam @DateTimeFormat(iso = DATE) endDate: LocalDate,
            workplaceId: Long
    ): List<ReservationDTO> {
        return reservationService
                .getWorkplaceReservations(
                        startDate,
                        endDate,
                        workplaceId
                ).map { it.toReservationDTO() }
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
    fun deleteReservations(@RequestBody deleteReservationsDTOs: DeleteReservationsDTO) {
        reservationService.deleteReservations(deleteReservationsDTOs.reservationIds)
    }

}

private fun Reservation.toReservationDTO() = ReservationDTO(
        id!!,
        startDate,
        endDate,
        username,
        workplace
)

data class ReservationDTO(
        val id: Long,
        val startDate: LocalDate,
        val endDate: LocalDate,
        val user: String,
        val workplace: Workplace
)

data class DeleteReservationsDTO(
        val reservationIds: List<Long>
)

data class AddReservationDTO(val workplaceId: Long,
                             val startDate: LocalDate,
                             val endDate: LocalDate)
