package de.iteratec.iteraOfficeMap.reservation

import de.iteratec.iteraOfficeMap.workplace.Workplace
import de.iteratec.iteraOfficeMap.workplace.WorkplaceRepository
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.empty
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.Clock
import java.time.LocalDate

@SpringBootTest
@ActiveProfiles("dev")
@ExtendWith(SpringExtension::class)
class ReservationServiceTest
@Autowired constructor(
        private val service: ReservationService,
        private val reservationRepository: ReservationRepository,
        private val workplaceRepository: WorkplaceRepository
) {

    private val workplace = Workplace(null, "myPlace", 0, 0, "mapId", "Zwei Bildschirme")
    private val clock = Clock.systemUTC()
    private val today = LocalDate.now(clock)
    private val tomorrow = today.plusDays(1)
    private val reservationToday = Reservation(null, today, today, "user", workplace)
    private val reservationTomorrow = Reservation(null, tomorrow, tomorrow, "user", workplace)

//    private fun createAndSaveReservations() {
//        reservationBefore = Reservation(50L, 50L, "john", workplace)
//        reservationLimit1 = Reservation(100L, 149L, "sam", workplace)
//        reservationMid = Reservation(150L, 199L, "john", workplace)
//        reservationLimit2 = Reservation(200L, 200L, "john", workplace)
//        reservationAfter = Reservation(250L, 299L, "john", workplace)
//        reservationRepository!!.saveAll(asList<Reservation>(reservationLimit1, reservationMid, reservationLimit2))
//    }

    @BeforeEach
    fun init() {
        reservationRepository.deleteAll()
        workplaceRepository.deleteAll()
        workplaceRepository.save(workplace)
    }

    @Nested
    @DisplayName("getAllReservations()")
    inner class GetAllReservations {

        @Test
        fun `no reservation`() {
            assertThat(service.getAllReservations(), empty())
        }

        @Test
        fun `one reservation`() {
            reservationRepository.save(reservationToday)
            assertThat(service.getAllReservations(), containsInAnyOrder(reservationToday))
        }

        @Test
        fun `multiple reservations`() {
            reservationRepository.saveAll(listOf(reservationToday, reservationTomorrow))
            assertThat(service.getAllReservations(), containsInAnyOrder(reservationToday, reservationTomorrow))
        }

    }

//
//    @Test
//    fun getDailyReservationsTest() {
//        val workplace2 = Workplace("secondPlace", 1, 2, "mapId", "Zwei Bildschirme")
//        workplaceRepository!!.save(workplace2)
//        val reservationBefore = Reservation(currentDay, currentDay, "Max", workplace)
//        val reservationAfter = Reservation(currentDay, currentDay, "Moritz", workplace2)
//        reservationRepository!!.saveAll(asList(reservationBefore, reservationAfter))
//        val reservationDTOList = reservationService!!.getDailyReservations(DateUtility.startOfDay(Date()))
//
//        assertEquals(2, reservationDTOList.size)
//        assertReservationEqualsReservationDTO(reservationBefore, reservationDTOList[0])
//        assertReservationEqualsReservationDTO(reservationAfter, reservationDTOList[1])
//    }
//
//
//    @Test
//    fun getDailyReservationsEmptyListTest() {
//        val reservationDTOList = reservationService!!.getDailyReservations(DateUtility.startOfDay(Date()))
//        assertEquals(0, reservationDTOList.size)
//    }
//
//    @Test
//    fun getPeriodReservationsTest() {
//        createAndSaveReservations()
//        val reservationDTOList = reservationService!!.getPeriodReservations(100L, 200L)
//
//        assertEquals(3, reservationDTOList.size)
//        assertReservationEqualsReservationDTO(reservationLimit1, reservationDTOList[0])
//        assertReservationEqualsReservationDTO(reservationMid, reservationDTOList[1])
//        assertReservationEqualsReservationDTO(reservationLimit2, reservationDTOList[2])
//    }
//
//    @Test
//    fun getPeriodReservationInvalidDatesTest() {
//        assertThrows(InvalidDatesException::class.java) {
//            createAndSaveReservations()
//            val reservationDTOList = reservationService!!.getPeriodReservations(200L, 100L)
//        }
//
//    }
//
//    @Test
//    fun getPeriodReservationsEmptyListTest() {
//        val reservationDTOList = reservationService!!.getPeriodReservations(100L, 180L)
//        assertEquals(0, reservationDTOList.size)
//    }
//
//    @Test
//    fun getPeriodReservationsEndDateinBetweenTest() {
//        createAndSaveReservations()
//        val reservationDTOList = reservationService!!.getPeriodReservations(100L, 180L)
//
//        assertEquals(2, reservationDTOList.size)
//        assertReservationEqualsReservationDTO(reservationLimit1, reservationDTOList[0])
//        assertReservationEqualsReservationDTO(reservationMid, reservationDTOList[1])
//    }
//
//    @Test
//    fun getPeriodReservationsStartDateinBetweenTest() {
//        createAndSaveReservations()
//        val reservationDTOList = reservationService!!.getPeriodReservations(120L, 199L)
//
//        assertEquals(2, reservationDTOList.size)
//        assertReservationEqualsReservationDTO(reservationLimit1, reservationDTOList[0])
//        assertReservationEqualsReservationDTO(reservationMid, reservationDTOList[1])
//    }
//
//
//    @Test
//    fun getPeriodReservationsStartDateAtLimitTest() {
//        createAndSaveReservations()
//        val reservationDTOList = reservationService!!.getPeriodReservations(200L, 210L)
//
//        assertEquals(1, reservationDTOList.size)
//        assertReservationEqualsReservationDTO(reservationLimit2, reservationDTOList[0])
//    }
//
//    @Test
//    fun getPeriodReservationsEndDateAtLimitTest() {
//        createAndSaveReservations()
//        val reservationDTOList = reservationService!!.getPeriodReservations(50L, 100L)
//
//        assertEquals(1, reservationDTOList.size)
//        assertReservationEqualsReservationDTO(reservationLimit1, reservationDTOList[0])
//    }
//
//    @Test
//    fun getPeriodReservationsNonExistingBeforeTest() {
//        createAndSaveReservations()
//        val reservationDTOList = reservationService!!.getPeriodReservations(20L, 99L)
//        assertEquals(0, reservationDTOList.size)
//    }
//
//
//    @Test
//    fun getPeriodReservationsNonExistingAfterTest() {
//        createAndSaveReservations()
//        val reservationDTOList = reservationService!!.getPeriodReservations(201L, 205L)
//        assertEquals(0, reservationDTOList.size)
//    }
//
//    @Test
//    fun getUserReservationsTest() {
//        createAndSaveReservations()
//        val reservationDTOList = reservationService!!.getUserReservations("john")
//
//        assertEquals(2, reservationDTOList.size)
//        assertEquals("john", reservationDTOList[0].user)
//        assertEquals("john", reservationDTOList[1].user)
//    }
//
//    @Test
//    fun getUserReservationsEmptyList() {
//        createAndSaveReservations()
//        val reservationDTOList = reservationService!!.getUserReservations("max")
//
//        assertEquals(0, reservationDTOList.size)
//    }
//    /*
//     *GetConflictingReservatons Methods Testing
//     */
//
//    @Test
//    fun getConflictingReservationSameDatesTest() {
//        createAndSaveReservations()
//        val conflictingReservationDTOList = reservationService!!.getConflictingReservations(150L, 199L, workplace!!.id!!)
//
//        assertEquals(1, conflictingReservationDTOList.size)
//        assertReservationEqualsReservationDTO(reservationMid, conflictingReservationDTOList[0])
//    }
//
//
//    @Test
//    fun getConflictingReservationEndDateBetweenTest() {
//        createAndSaveReservations()
//        val conflictingReservationDTOList = reservationService!!.getConflictingReservations(50L, 140L, workplace!!.id!!)
//
//        assertEquals(1, conflictingReservationDTOList.size)
//        assertReservationEqualsReservationDTO(reservationLimit1, conflictingReservationDTOList[0])
//    }
//
//    @Test
//    fun getConflictingReservationStartdateAtLimitEndDateBetweenTest() {
//        createAndSaveReservations()
//        val conflictingReservationDTOList = reservationService!!.getConflictingReservations(100L, 140L, workplace!!.id!!)
//
//        assertEquals(1, conflictingReservationDTOList.size)
//        assertReservationEqualsReservationDTO(reservationLimit1, conflictingReservationDTOList[0])
//    }
//
//    @Test
//    fun getConflictingReservationStartDateBetweenTest() {
//        createAndSaveReservations()
//        val conflictingReservationDTOList = reservationService!!.getConflictingReservations(160L, 250L, workplace!!.id!!)
//
//        assertEquals(2, conflictingReservationDTOList.size)
//        assertReservationEqualsReservationDTO(reservationMid, conflictingReservationDTOList[0])
//        assertReservationEqualsReservationDTO(reservationLimit2, conflictingReservationDTOList[1])
//    }
//
//    @Test
//    fun getConflictingReservationStartDateBetweenEndDateAtLimitTest() { // to be implemented
//        createAndSaveReservations()
//        val conflictingReservationDTOList = reservationService!!.getConflictingReservations(160L, 200L, workplace!!.id!!)
//
//        assertEquals(2, conflictingReservationDTOList.size)
//        assertReservationEqualsReservationDTO(reservationMid, conflictingReservationDTOList[0])
//        assertReservationEqualsReservationDTO(reservationLimit2, conflictingReservationDTOList[1])
//    }
//
//    @Test
//    fun getConflictingReservationNonExistingBeforeTest() { // to be implemented
//        createAndSaveReservations()
//        val conflictingReservationDTOList = reservationService!!.getConflictingReservations(10L, 20L, workplace!!.id!!)
//
//        assertEquals(0, conflictingReservationDTOList.size)
//    }
//
//    @Test
//    fun getConflictingReservationNonExistingAfterTest() { // to be implemented
//        createAndSaveReservations()
//        val conflictingReservationDTOList = reservationService!!.getConflictingReservations(210L, 220L, workplace!!.id!!)
//
//        assertEquals(0, conflictingReservationDTOList.size)
//    }
//
//    @Test
//    fun getConflictingReservationNonExistingInBetweenTest() {
//        createAndSaveReservations()
//        val conflictingReservationDTOList = reservationService!!.getConflictingReservations(210L, 240L, workplace!!.id!!)
//
//        assertEquals(0, conflictingReservationDTOList.size)
//    }
//
//    @Test
//    fun getConflictingReservationStartAndEndDateOutTest() {
//        createAndSaveReservations()
//        val conflictingReservationDTOList = reservationService!!.getConflictingReservations(50L, 250L, workplace!!.id!!)
//
//        assertEquals(3, conflictingReservationDTOList.size)
//        assertReservationEqualsReservationDTO(reservationLimit1, conflictingReservationDTOList[0])
//        assertReservationEqualsReservationDTO(reservationMid, conflictingReservationDTOList[1])
//        assertReservationEqualsReservationDTO(reservationLimit2, conflictingReservationDTOList[2])
//    }
//
//    /*
//     *getWorkplaceReservation Tests
//     */
//
//    @Test
//    fun getWorkPlaceReservationsTest() {
//        createAndSaveReservations()
//        val reservationDTOList = reservationService!!.getWorkplaceReservations(50L, 200L, workplace!!.id!!)
//        assertEquals(workplace, reservationDTOList[0].workplace)
//        assertEquals(workplace, reservationDTOList[1].workplace)
//        assertEquals(workplace, reservationDTOList[2].workplace)
//    }
//
//    @Test
//    fun getWorkplaceReservationsOfNullWorkplaceTest() {
//        assertThrows(DoesNotExistException::class.java) {
//            createAndSaveReservations()
//            reservationService!!.getWorkplaceReservations(50L, 200L, 54L)
//        }
//    }
//
//    @Test
//    fun getWorkplaceReservationsEmptyListTest() {
//        workplaceRepository!!.save(workplace!!)
//        val reservationDTOList = reservationService!!.getWorkplaceReservations(50L, 200L, workplace!!.id!!)
//
//        assertEquals(0, reservationDTOList.size)
//    }
//
//
//    /*
//     *addReservation Tests
//     */
//
//    @Test
//    fun addReservationTest() {
//        val reservation = Reservation(100L, 200L, "sam", workplace)
//        val addReservationDTO = AddReservationDTO(reservation)
//        val principal = { "sam" }
//        reservationService!!.addReservation(addReservationDTO, principal)
//        val reservationList = reservationRepository!!.findAll()
//
//        assertEquals(1, reservationList.size)
//        assertReservationEqualsAddReservationDTO(reservationList[0], addReservationDTO)
//    }
//
//
//    @Test
//    fun addReservationEndDateBeforeStartDateTest() {
//        assertThrows(InvalidReservationException::class.java) {
//            val reservation = Reservation(250L, 200L, "sam", workplace)
//            val addReservationDTO = AddReservationDTO(reservation)
//            val principal = { "sam" }
//            reservationService!!.addReservation(addReservationDTO, principal)
//        }
//    }
//
//
//    @Test
//    fun addReservationInvalidWorkplaceTest() {
//        assertThrows(InvalidWorkplaceException::class.java) {
//            workplaceRepository!!.deleteAll(workplaceRepository.findAll())
//            val reservation = Reservation(150L, 200L, "sam", workplace)
//            val addReservationDTO = AddReservationDTO(reservation)
//            val principal = { "sam" }
//            reservationService!!.addReservation(addReservationDTO, principal)
//        }
//    }
//
//    @Test
//    fun addConflictingReservationTest() {
//        assertThrows(AlreadyExistsException::class.java) {
//            createAndSaveReservations()
//            val reservation = Reservation(100L, 200, "sam", workplace)
//            val addReservationDTO = AddReservationDTO(reservation)
//            val principal = { "sam" }
//            reservationService!!.addReservation(addReservationDTO, principal)
//        }
//    }
//
//    @Test
//    fun addReservationsConflictingTest() {
//        assertThrows(AlreadyExistsException::class.java) {
//            createAndSaveReservations()
//            reservationRepository!!.deleteAll()
//            reservationRepository.save(reservationMid!!)
//            val principal = { "sam" }
//            val addReservationDTOList = asList(
//                    AddReservationDTO(reservationLimit1), AddReservationDTO(reservationMid), AddReservationDTO(reservationLimit2))
//            try {
//                reservationService!!.addReservations(addReservationDTOList, principal)
//            } catch (e: AlreadyExistsException) {
//                assertEquals(1, reservationRepository.findAll().size)
//                val (_, startDate, endDate, _, workplace1) = reservationRepository.findByWorkplaceAndStartDateEqualsAndEndDateEquals(reservationMid!!.workplace, reservationMid!!.startDate, reservationMid!!.endDate)
//
//                assertEquals(reservationMid!!.workplace.id, workplace1.id)
//                assertEquals(reservationMid!!.startDate, startDate)
//                assertEquals(reservationMid!!.endDate, endDate)
//
//                throw e
//            }
//
//            fail<Any>()
//        }
//
//    }
//
//
//    /*
//     *delete Reservation methods Testing
//     */
//
//    @Test
//    fun deleteReservationTest() {
//        createAndSaveReservations()
//        val deleteReservationDTOMid = DeleteReservationDTO(reservationMid)
//        val deleteReservationDTOLimit2 = DeleteReservationDTO(reservationLimit2)
//        reservationService!!.deleteReservation(deleteReservationDTOLimit2)
//        reservationService.deleteReservation(deleteReservationDTOMid)
//        val reservationDTOList = reservationService.getAllReservations()
//
//        assertEquals(1, reservationDTOList.size)
//        assertReservationEqualsReservationDTO(reservationLimit1, reservationDTOList[0])
//    }
//
//    @Test
//    fun deleteReservationWithInvalidWorkPlace() {
//        assertThrows(InvalidReservationException::class.java) {
//            workplaceRepository!!.deleteAll()
//            val reservation = Reservation(now, now, "Sascha", workplace)
//            val deleteReservationDTOMid = DeleteReservationDTO(reservation)
//            reservationService!!.deleteReservation(deleteReservationDTOMid)
//        }
//    }
//
//
//    @Test
//    fun deleteNonExistingReservationTest() {
//        assertThrows(DoesNotExistException::class.java) {
//            val reservation = Reservation(now, now, "Sascha", workplace)
//            val deleteReservationDTOMid = DeleteReservationDTO(reservation)
//            reservationService!!.deleteReservation(deleteReservationDTOMid)
//        }
//    }
//
//    @Test
//    fun deleteReservationsTest() {
//        createAndSaveReservations()
//        reservationService!!.deleteReservations(asList<T>(DeleteReservationDTO(reservationLimit1), DeleteReservationDTO(reservationMid)))
//        assertEquals(1, reservationRepository!!.findAll().size)
//        assertTrue(reservationRepository.findAll().contains(reservationLimit2))
//    }
//
//    @Test
//    fun deleteReservationsWithOneNonExistingTest() {
//        createAndSaveReservations()
//        reservationRepository!!.delete(reservationMid!!)
//        reservationService!!.deleteReservations(asList<T>(
//                DeleteReservationDTO(reservationMid), DeleteReservationDTO(reservationLimit2)
//        ))
//        assertEquals(1, reservationRepository.findAll().size)
//        assertTrue(reservationRepository.findAll().contains(reservationLimit1))
//    }

}
