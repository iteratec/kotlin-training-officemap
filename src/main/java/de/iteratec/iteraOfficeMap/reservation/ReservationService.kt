package de.iteratec.iteraOfficeMap.reservation

import de.iteratec.iteraOfficeMap.exceptions.*
import de.iteratec.iteraOfficeMap.utility.millisToGermanLocalDate
import de.iteratec.iteraOfficeMap.workplace.Workplace
import de.iteratec.iteraOfficeMap.workplace.WorkplaceRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class ReservationService @Autowired constructor(
        private val reservationRepository: ReservationRepository,
        private val workplaceRepository: WorkplaceRepository
) {

    fun getAllReservations(): List<Reservation> {
        return reservationRepository.findAll()
    }

    fun addReservation(reservation: Reservation) {
        reservationRepository.save(reservation)
    }

    fun addReservations(reservations: List<Reservation>) {
        reservationRepository.saveAll(reservations)
    }

    private fun checkValidityAndGetWorkplace(addReservationDTO: AddReservationDTO): Workplace {
        val workplace = workplaceRepository
                .findById(addReservationDTO.workplaceId)
                .orElseThrow { InvalidWorkplaceException() }

        if (addReservationDTO.startDate > addReservationDTO.endDate) {
            throw InvalidReservationException()
        }


        if (getConflictingReservations(
                        addReservationDTO.startDate.millisToGermanLocalDate(),
                        addReservationDTO.endDate.millisToGermanLocalDate(),
                        addReservationDTO.workplaceId
                ).isNotEmpty()) {
            throw AlreadyExistsException()
        }
        return workplace
    }


    /**
     * @return a list of reservations, if there are any in given period
     */
    fun getConflictingReservations(startDate: LocalDate, endDate: LocalDate, workplaceId: Long): List<Reservation> {
        val workplace = workplaceRepository.findById(workplaceId).orElseThrow { DoesNotExistException() }

        return reservationRepository
                .findByStartDateLessThanEqualAndEndDateGreaterThanEqualAndWorkplace(endDate, startDate, workplace)

    }

    /**
     * @return a list of all reservations for one day
     */
    fun getDailyReservations(date: LocalDate): List<ReservationDTO> {
        return reservationRepository
                .findByStartDateLessThanEqualAndEndDateGreaterThanEqual(date, date)
                .map { it.toReservationDTO() }
    }

    /**
     * @return a list of all reservations for a time period
     */
    fun getPeriodReservations(startDate: LocalDate, endDate: LocalDate): List<Reservation> {
        if (startDate > endDate) {
            throw InvalidDatesException()
        }
        return reservationRepository.findByStartDateLessThanEqualAndEndDateGreaterThanEqual(endDate, startDate)
    }

    /**
     * @return a list of a workplaces reservations for a time period
     */
    fun getWorkplaceReservations(startDate: LocalDate, endDate: LocalDate, workplaceId: Long): List<Reservation> {
        return reservationRepository.findByStartDateLessThanEqualAndEndDateGreaterThanEqualAndWorkplace(
                endDate,
                startDate,
                workplaceRepository.findById(workplaceId).orElseThrow { DoesNotExistException() }
        )
    }

    /**
     * @return a list of all reservations for one user
     */
    fun getUserReservations(user: String): List<Reservation> {
        return reservationRepository.findByUsernameEqualsOrderByStartDateAsc(user)
    }

    /**
     * deletes a list of reservation.
     * If any of the reservations does not exist, it will ignore the exception and delete the other
     */
    fun deleteReservations(deletionRequests: List<DeleteReservationRequest>) {
        if (deletionRequests.isEmpty()) {
            return
        }

        val workplaceId = deletionRequests.first().workplaceId
        require(deletionRequests.all { it.workplaceId == workplaceId })
        val workplace = workplaceRepository.findById(workplaceId).orElseThrow { DoesNotExistException() }

        for (request in deletionRequests) {
            try {
                reservationRepository.deleteByWorkplaceAndStartDateAndEndDate(
                        workplace,
                        request.startDate,
                        request.endDate)
            } catch (e: Exception) {
                System.err.println(e.message)
            }

        }
    }

}

data class DeleteReservationRequest(val workplaceId: Long, val startDate: LocalDate, val endDate: LocalDate)
