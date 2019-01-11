package de.iteratec.iteraOfficeMap.reservation

import de.iteratec.iteraOfficeMap.exceptions.*
import de.iteratec.iteraOfficeMap.workplace.Workplace
import de.iteratec.iteraOfficeMap.workplace.WorkplaceRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.security.Principal

@Service
class ReservationService @Autowired constructor(
        private val reservationRepository: ReservationRepository,
        private val workplaceRepository: WorkplaceRepository
) {

    fun getAllReservations(): List<ReservationDTO> {
        return reservationRepository.findAll()
                .map { ReservationDTO(it) }
    }

    // adds a new reservation to the database
    fun addReservation(addReservationDTO: AddReservationDTO, principal: Principal) {

        val workplace = checkValidityAndGetWorkplace(addReservationDTO)

        addReservationDirect(addReservationDTO, workplace, principal)


    }

    private fun addReservationDirect(addReservationDTO: AddReservationDTO, workplace: Workplace, principal: Principal) {
        reservationRepository.save(Reservation(addReservationDTO.startDate,
                addReservationDTO.endDate, principal.name, workplace))
    }

    private fun checkValidityAndGetWorkplace(addReservationDTO: AddReservationDTO): Workplace {
        val workplace = workplaceRepository
                .findById(addReservationDTO.workplaceId)
                .orElseThrow { InvalidWorkplaceException() }

        if (addReservationDTO.startDate > addReservationDTO.endDate) {
            throw InvalidReservationException()
        }


        if (getConflictingReservations(addReservationDTO.startDate, addReservationDTO.endDate, addReservationDTO.workplaceId).isNotEmpty()) {
            throw AlreadyExistsException()
        }
        return workplace
    }


    /**
     * adds a list of reservations. None of them will be added if any of the reservations is invalid
     *
     * @param reservations
     * @throws AlreadyExistsException      if any reservation already exists
     * @throws InvalidDatesException       if the dates are invalid
     * @throws InvalidReservationException if the reservation is invalid
     */
    fun addReservations(reservations: List<AddReservationDTO>, principal: Principal) {
        val workplaceList = reservations.map { reservationDTO -> checkValidityAndGetWorkplace(reservationDTO) }
        for (i in reservations.indices) {
            addReservationDirect(reservations[i], workplaceList[i], principal)
        }
    }


    /**
     * @return a list of reservations, if there are any in given period
     */
    fun getConflictingReservations(startDate: Long, endDate: Long, workplaceId: Long): List<ReservationDTO> {
        val workplace = workplaceRepository.findById(workplaceId).get()

        return reservationRepository
                .findByStartDateLessThanEqualAndEndDateGreaterThanEqualAndWorkplace(endDate, startDate, workplace)
                .map { reservation -> ReservationDTO(reservation) }

    }

    /**
     * @return a list of all reservations for one day
     */
    fun getDailyReservations(date: Long): List<ReservationDTO> {
        return reservationRepository
                .findByStartDateLessThanEqualAndEndDateGreaterThanEqual(date, date)
                .map { reservation -> ReservationDTO(reservation) }
    }

    /**
     * @param startDate
     * @param endDate
     * @return a list of all reservations for a time period
     */
    fun getPeriodReservations(startDate: Long, endDate: Long): List<ReservationDTO> {
        if (startDate > endDate) {
            throw InvalidDatesException()
        }
        return reservationRepository
                .findByStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        endDate, startDate).map { reservation ->  ReservationDTO(reservation) }
    }

    /**
     * @param startDate
     * @param endDate
     * @param workplaceId
     * @return a list of a workplaces reservations for a time period
     */
    fun getWorkplaceReservations(startDate: Long,
                                 endDate: Long, workplaceId: Long): List<ReservationDTO> {

        return reservationRepository
                .findByStartDateLessThanEqualAndEndDateGreaterThanEqualAndWorkplace(
                        endDate, startDate, workplaceRepository.findById(workplaceId).orElseThrow { DoesNotExistException() })
                        .map { reservation -> ReservationDTO(reservation) }
    }

    /**
     * @param user
     * @return a list of all reservations for one user
     */
    fun getUserReservations(user: String): List<ReservationDTO> {

        return reservationRepository
                .findByUsernameEqualsOrderByStartDateAsc(user).map { reservation -> ReservationDTO(reservation) }
    }


    /**
     * deletes one explicit reservation
     *
     * @param deleteReservationDTO
     * @throws InvalidReservationException
     * @throws DoesNotExistException
     */
    fun deleteReservation(deleteReservationDTO: DeleteReservationDTO) {
        val workplace = workplaceRepository.findById(deleteReservationDTO.workplaceId).orElseThrow { InvalidReservationException() }

        val reservation = reservationRepository.findByWorkplaceAndStartDateEqualsAndEndDateEquals(
                workplace,
                deleteReservationDTO.startDate,
                deleteReservationDTO.endDate) ?: throw DoesNotExistException()

        reservationRepository.deleteByWorkplaceAndStartDateAndEndDate(
                workplace,
                reservation.startDate,
                reservation.endDate)
    }

    /**
     * deletes a list of reservation.
     * If any of the reservations does not exist, it will ignore the exception and delete the other
     *
     * @param reservations
     * @throws InvalidReservationException
     * @throws DoesNotExistException
     */
    fun deleteReservations(reservations: List<DeleteReservationDTO>) {

        for (deleteReservationDTO in reservations) {
            try {
                deleteReservation(deleteReservationDTO)
            } catch (e: Exception) {
                System.err.println(e.message)
            }

        }
    }


}
