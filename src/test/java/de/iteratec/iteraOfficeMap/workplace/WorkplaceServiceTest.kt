package de.iteratec.iteraOfficeMap.workplace

import de.iteratec.iteraOfficeMap.exceptions.AlreadyExistsException
import de.iteratec.iteraOfficeMap.exceptions.DoesNotExistException
import de.iteratec.iteraOfficeMap.reservation.ReservationStatus
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension

@SpringBootTest
@ActiveProfiles("dev")
@ExtendWith(SpringExtension::class)
class WorkplaceServiceTest
@Autowired constructor(
        private val workplaceRepository: WorkplaceRepository,
        private val workplaceService: WorkplaceService
) {

    private val workplace1 = Workplace("First", 1, 1, "mapId", "Zwei Bildschirme")
    private val workplace2 = Workplace("Second", 2, 2, "mapId", "Zwei Bildschirme")
    private val workplace3 = Workplace("Third", 3, 3, "mapId", "Zwei Bildschirme")
    private val workplace4 = Workplace("Fourth", 4, 4, "mapId", "Zwei Bildschirme")

    @BeforeEach
    fun init() {
        workplaceRepository.deleteAll(workplaceRepository.findAll())
    }

    @Test
    fun getAllWorkplacesTest() {
        createAndSaveWorkplaces()
        val workplaceList = workplaceService.getAllWorkplaces()

        assertEquals(3, workplaceList.size)
        assertWorkplaceEqualsWorkplace(workplace1, workplaceList[0])
        assertWorkplaceEqualsWorkplace(workplace2, workplaceList[1])
        assertWorkplaceEqualsWorkplace(workplace3, workplaceList[2])
    }

    @Test
    fun getAllWorkplacesEmptyListTest() {
        assertEquals(0, workplaceService.getAllWorkplaces().size)
    }

    @Test
    fun addWorkPlaceTest() {
        workplace4 = Workplace("Fourth", 4, 4, "mapId", "Zwei Bildschirme")
        workplaceService.addNewWorkplace(workplace4)
        val workplaceList = workplaceService.getAllWorkplaces()

        assertEquals(1, workplaceList.size)
        assertWorkplaceEqualsWorkplace(workplace4, workplaceList[0])
    }

    @Test
    fun getStatusOfFreeWorkplaceTest() {
        workplace4 = Workplace("fourth", 4, 4, "mapId", "Zwei Bildschirme")
        workplaceRepository.save(workplace4)

        assertEquals(ReservationStatus.FREE, workplaceService.getStatus(workplace4.id!!))
    }

    @Test
    fun addAlreadyExistingWorkPlaceWithSameCoordinates() {
        Assertions.assertThrows(AlreadyExistsException::class.java) {
            workplace4 = Workplace("fourth", 4, 4, "mapId", "Zwei Bildschirme")
            workplace3 = Workplace("third", 4, 4, "mapId", "Zwei Bildschirme")
            workplaceService.addNewWorkplace(workplace3)
            workplaceService.addNewWorkplace(workplace4)
        }
    }

    @Test
    fun addAlreadyExistingWorkPlaceWithSameName() {
        Assertions.assertThrows(AlreadyExistsException::class.java) {
            workplace4 = Workplace("fourth", 4, 4, "mapId", "Zwei Bildschirme")
            workplace3 = Workplace("fourth", 3, 3, "mapId", "Zwei Bildschirme")
            workplaceService.addNewWorkplace(workplace4)
            workplaceService.addNewWorkplace(workplace3)
        }
    }

    @Test
    fun deleteWorkPlaceTest() {
        createAndSaveWorkplaces()
        workplaceService.deleteWorkplace(workplace1.id!!)
        val workplaceList = workplaceRepository.findAll()
        assertEquals(2, workplaceList.size)
        assertEquals(workplace2, workplaceList[0])
        assertEquals(workplace3, workplaceList[1])
    }

    @Test
    fun DeleteNonExistingWorkplaceTest() {
        Assertions.assertThrows(DoesNotExistException::class.java) {
            createAndSaveWorkplaces()
            workplaceRepository.deleteById(workplace1.id!!)
            workplaceService.deleteWorkplace(workplace1.id!!)

        }
    }
}
