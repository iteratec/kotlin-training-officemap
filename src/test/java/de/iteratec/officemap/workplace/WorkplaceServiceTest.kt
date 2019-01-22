package de.iteratec.officemap.workplace

import de.iteratec.officemap.exceptions.AlreadyExistsException
import de.iteratec.officemap.exceptions.DoesNotExistException
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.empty
import org.junit.jupiter.api.Assertions.assertThrows
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

    private val workplace1 = Workplace(null, "First", 1, 1, "mapId", "Zwei Bildschirme")
    private val workplace2 = Workplace(null, "Second", 2, 2, "mapId", "Zwei Bildschirme")
    private val workplace3 = Workplace(null, "Third", 3, 3, "mapId", "Zwei Bildschirme")
    private val workplace4 = Workplace(null, "Fourth", 4, 4, "mapId", "Zwei Bildschirme")

    @BeforeEach
    fun deleteAllWorkplaces() {
        workplaceRepository.deleteAll(workplaceRepository.findAll())
    }

    @Test
    fun `when no workplaces exist none can be found`() {
        assertThat(workplaceService.getAllWorkplaces(), empty())
    }

    @Test
    fun `when one workplace exists it can be found`() {
        workplaceRepository.save(workplace1)
        assertThat(workplaceService.getAllWorkplaces(), containsInAnyOrder(workplace1))
    }

    @Test
    fun `when 4 workplaces exist all can be found`() {
        workplaceRepository.saveAll(listOf(workplace1, workplace2, workplace3, workplace4))
        assertThat(workplaceService.getAllWorkplaces(), containsInAnyOrder(workplace1, workplace2, workplace3, workplace4))
    }

    @Test
    fun `add 1 workplace`() {
        assertThat(workplaceService.getAllWorkplaces(), empty())
        workplaceService.addNewWorkplace(workplace1)
        assertThat(workplaceService.getAllWorkplaces(), containsInAnyOrder(workplace1))
    }

    @Test
    fun `add second workplace at same coordinates throws AlreadyExistsException`() {
        workplaceService.addNewWorkplace(workplace1)
        assertThrows(AlreadyExistsException::class.java) {
            workplaceService.addNewWorkplace(workplace1.copy(name = "otherName"))
        }
    }

    @Test
    fun `add second workplace with same name but different coordinates throws AlreadyExistsException`() {
        workplaceService.addNewWorkplace(workplace1)
        assertThrows(AlreadyExistsException::class.java) {
            workplaceService.addNewWorkplace(workplace1.copy(x = 123, y = 123))
        }
    }

    @Test
    fun `delete last workplace`() {
        val workplace = workplaceRepository.save(workplace1)
        val id = requireNotNull(workplace.id)

        assertThat(workplaceService.getAllWorkplaces(), containsInAnyOrder(workplace))
        workplaceService.deleteWorkplace(id)
        assertThat(workplaceService.getAllWorkplaces(), empty())
    }

    @Test
    fun `delete one of two workplaces`() {
        val workplaces = workplaceRepository.saveAll(listOf(workplace1, workplace2))
        val (first, second) = workplaces

        val firstId = requireNotNull(first.id)

        assertThat(workplaceService.getAllWorkplaces(), containsInAnyOrder(first, second))
        workplaceService.deleteWorkplace(firstId)
        assertThat(workplaceService.getAllWorkplaces(), containsInAnyOrder(second))
    }

    @Test
    fun `delete a non-existing workplace throws a DoesNotExistException`() {
        assertThrows(DoesNotExistException::class.java) {
            workplaceService.deleteWorkplace(1234)
        }
    }

}
