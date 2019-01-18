package de.iteratec.iteraOfficeMap.workplace

import de.iteratec.iteraOfficeMap.exceptions.AlreadyExistsException
import de.iteratec.iteraOfficeMap.exceptions.DoesNotExistException
import de.iteratec.iteraOfficeMap.reservation.ReservationRepository
import de.iteratec.iteraOfficeMap.reservation.ReservationStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class WorkplaceService @Autowired constructor(
        private val workplaceRepository: WorkplaceRepository,
        private val reservationRepository: ReservationRepository
) {

    fun getAllWorkplaces(): List<Workplace> = workplaceRepository.findAll()

    fun findById(workplaceId: Long): Workplace {
        return workplaceRepository
                .findById(workplaceId)
                .orElseThrow { DoesNotExistException() }
    }

    /**
     * checks the status of the reservation
     */
    fun getStatus(workplaceId: Long): ReservationStatus {
        val workplace = findById(workplaceId)

        val reservation = reservationRepository.findByStartDateEqualsAndWorkplace(LocalDate.now(), workplace)

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
