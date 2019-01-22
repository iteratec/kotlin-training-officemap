package de.iteratec.officemap.reservation

import de.iteratec.officemap.exceptions.AlreadyExistsException
import de.iteratec.officemap.exceptions.DoesNotExistException
import de.iteratec.officemap.exceptions.InvalidDatesException
import de.iteratec.officemap.exceptions.InvalidReservationException
import de.iteratec.officemap.workplace.Workplace
import de.iteratec.officemap.workplace.WorkplaceRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class ReservationService @Autowired constructor(
        private val reservationRepository: ReservationRepository,
        private val workplaceRepository: WorkplaceRepository
) {

    fun getAllReservations(): List<Reservation> {
        return reservationRepository.findAll()
    }

    @Transactional
    fun addReservations(reservations: List<Reservation>) {
        if (reservations.isEmpty()) {
            return
        }

        if (reservations.any { it.startDate > it.endDate }) {
            throw InvalidReservationException()
        }

        val workplace = reservations.first().workplace
        requireNotNull(workplace.id)
        if (reservations.any { it.workplace != workplace }) {
            throw InvalidReservationException()
        }

        if (reservations.any { getConflictingReservations(it.startDate, it.endDate, it.workplace).isNotEmpty() }) {
            throw AlreadyExistsException()
        }
        // TODO: Ok, so we checked that the incoming reservations do not conflict with any in the database.
        //       But what if they have conflicts with each other? Then we will still save them...

        reservationRepository.saveAll(reservations)
    }

    /**
     * @return a list of reservations, if there are any in given period
     */
    fun getConflictingReservations(startDate: LocalDate, endDate: LocalDate, workplace: Workplace): List<Reservation> {
        return reservationRepository
                .findByStartDateLessThanEqualAndEndDateGreaterThanEqualAndWorkplace(endDate, startDate, workplace)

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
