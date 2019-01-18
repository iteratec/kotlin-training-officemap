package de.iteratec.iteraOfficeMap.workplace

import de.iteratec.iteraOfficeMap.exceptions.AlreadyExistsException
import de.iteratec.iteraOfficeMap.exceptions.DoesNotExistException
import de.iteratec.iteraOfficeMap.reservation.ReservationRepository
import de.iteratec.iteraOfficeMap.reservation.ReservationStatus
import de.iteratec.iteraOfficeMap.utility.startOfDay
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class WorkplaceService @Autowired constructor(
        private val workplaceRepository: WorkplaceRepository,
        private val reservationRepository: ReservationRepository
) {

    fun getAllWorkplaces(): List<Workplace> = workplaceRepository.findAll()

    /**
     * checks the status of the reservation
     */
    fun getStatus(workplaceID: Long): ReservationStatus {
        val workplace = workplaceRepository.findById(workplaceID).orElseThrow { DoesNotExistException() }

        val date = startOfDay(Date())
        val reservation = reservationRepository.findByStartDateEqualsAndWorkplace(date, workplace)

        return if (reservation == null) {
            ReservationStatus.FREE
        } else {
            ReservationStatus.NORMALRESERVATION
        }
    }

    fun addNewWorkplace(workplace: Workplace) {
        val existingWorkplace = workplaceRepository.findByXEqualsAndYEqualsOrNameEquals(workplace.x, workplace.y, workplace.name)

        if (existingWorkplace == null) {
            workplaceRepository.save(workplace)
        } else {
            throw AlreadyExistsException()
        }
    }

    fun deleteWorkplace(workplaceId: Long) {
        try {
            workplaceRepository.deleteById(workplaceId)
        } catch (e: Exception) {
            System.err.println(e.message)
            throw DoesNotExistException()
        }
    }

    fun updateWorkplace(workplace: Workplace) {
        workplaceRepository.save(workplace)
    }

}
