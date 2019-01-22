package de.iteratec.officemap.workplace

import de.iteratec.officemap.exceptions.AlreadyExistsException
import de.iteratec.officemap.exceptions.DoesNotExistException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class WorkplaceService @Autowired constructor(private val workplaceRepository: WorkplaceRepository) {

    fun getAllWorkplaces(): List<Workplace> = workplaceRepository.findAll()

    fun findById(workplaceId: Long): Workplace {
        return workplaceRepository
                .findById(workplaceId)
                .orElseThrow { DoesNotExistException() }
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
