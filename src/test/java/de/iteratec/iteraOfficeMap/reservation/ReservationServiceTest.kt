package de.iteratec.iteraOfficeMap.reservation

import de.iteratec.iteraOfficeMap.exceptions.AlreadyExistsException
import de.iteratec.iteraOfficeMap.exceptions.DoesNotExistException
import de.iteratec.iteraOfficeMap.exceptions.InvalidDatesException
import de.iteratec.iteraOfficeMap.exceptions.InvalidReservationException
import de.iteratec.iteraOfficeMap.workplace.Workplace
import de.iteratec.iteraOfficeMap.workplace.WorkplaceRepository
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.empty
import org.junit.jupiter.api.*
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
    private val yesterday = today.minusDays(1)
    private val username = "user"
    private val reservationYesterday = Reservation(null, yesterday, yesterday, username, workplace)
    private val reservationToday = Reservation(null, today, today, username, workplace)
    private val reservationTomorrow = Reservation(null, tomorrow, tomorrow, username, workplace)


    @BeforeEach
    fun init() {
        reservationRepository.deleteAll()
        workplaceRepository.deleteAll()
        workplaceRepository.save(workplace)
    }


    @Nested
    @DisplayName("get all reservations")
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


    @Nested
    @DisplayName("add reservations")
    inner class AddReservations {

        @Test
        fun `one reservation`() {
            assertThat(reservationRepository.findAll(), empty())
            service.addReservations(listOf(reservationToday))
            assertThat(reservationRepository.findAll(), containsInAnyOrder(reservationToday))
        }

        @Test
        fun `multiple reservations`() {
            assertThat(reservationRepository.findAll(), empty())
            service.addReservations(listOf(reservationToday, reservationTomorrow))
            assertThat(reservationRepository.findAll(), containsInAnyOrder(reservationToday, reservationTomorrow))
        }

        @Test
        fun `adding a reservation with end date before start date shall throw an exception`() {
            assertThrows<InvalidReservationException> {
                service.addReservations(listOf(
                        reservationToday.copy(
                                startDate = tomorrow,
                                endDate = today
                        )))
            }
        }


        @Test
        fun `adding the same reservation twice shall throw an exception`() {
            service.addReservations(listOf(reservationToday))
            assertThrows<AlreadyExistsException> {
                service.addReservations(listOf(reservationToday.copy(id = null)))
            }
        }

        @Test
        fun `adding multiple reservations with one conflicting does not change anything and throws an exception`() {
            service.addReservations(listOf(reservationToday))
            assertThat(reservationRepository.findAll(), containsInAnyOrder(reservationToday))
            assertThrows<AlreadyExistsException> {
                service.addReservations(listOf(reservationToday.copy(id = null), reservationTomorrow))
            }
            assertThat(reservationRepository.findAll(), containsInAnyOrder(reservationToday))
        }

    }


    @Nested
    @DisplayName("conflicting reservation")
    inner class ConflictingReservations {

        @Test
        fun `tomorrows reservation does not have conflict today`() {
            reservationRepository.save(reservationTomorrow)
            assertThat(service.getConflictingReservations(today, today, workplace), empty())
        }

        @Test
        fun `today's reservation does not have conflict tomorrow`() {
            reservationRepository.save(reservationToday)
            assertThat(service.getConflictingReservations(tomorrow, tomorrow, workplace), empty())
        }

        @Test
        fun `today's reservation is conflicting today`() {
            reservationRepository.save(reservationToday)
            assertThat(
                    service.getConflictingReservations(today, today, workplace),
                    containsInAnyOrder(reservationToday))
        }

        @Test
        fun `today's reservation is conflicting last week up until today`() {
            reservationRepository.save(reservationToday)
            assertThat(
                    service.getConflictingReservations(today.minusWeeks(1), today, workplace),
                    containsInAnyOrder(reservationToday))
        }

        @Test
        fun `today's reservation is conflicting next week beginning today`() {
            reservationRepository.save(reservationToday)
            assertThat(
                    service.getConflictingReservations(today, today.plusWeeks(1), workplace),
                    containsInAnyOrder(reservationToday))
        }

        @Test
        fun `today's reservation has no conflict with last week excluding today`() {
            reservationRepository.save(reservationToday)
            assertThat(
                    service.getConflictingReservations(yesterday.minusWeeks(1), yesterday, workplace),
                    empty())
        }

        @Test
        fun `today's reservation has no conflict with next week excluding today`() {
            reservationRepository.save(reservationToday)
            assertThat(
                    service.getConflictingReservations(tomorrow, tomorrow.plusWeeks(1), workplace),
                    empty())
        }

    }


    @Nested
    @DisplayName("delete reservations")
    inner class DeleteReservations {

        @Test
        fun `delete reservation of non-existing workplace shall throw exception`() {
            assertThrows<DoesNotExistException> {
                service.deleteReservations(listOf(DeleteReservationRequest(123, today, today)))
            }
        }

        @Test
        fun `delete non-existing reservation should not throw`() {
            service.deleteReservations(listOf(DeleteReservationRequest(workplace.id!!, today, today)))
        }

        @Test
        fun `delete non-existing reservation should not touch other reservations`() {
            reservationRepository.saveAll(listOf(reservationYesterday, reservationTomorrow))
            service.deleteReservations(listOf(DeleteReservationRequest(workplace.id!!, today, today)))
            assertThat(reservationRepository.findAll(), containsInAnyOrder(reservationYesterday, reservationTomorrow))
        }

        @Test
        fun `delete existing reservation`() {
            reservationRepository.save(reservationToday)
            assertThat(reservationRepository.findAll(), containsInAnyOrder(reservationToday))
            service.deleteReservations(listOf(DeleteReservationRequest(
                    workplace.id!!,
                    today,
                    today
            )))
            assertThat(reservationRepository.findAll(), empty())
        }

    }


    @Nested
    @DisplayName("get reservations by time period")
    inner class PeriodicReservations {

        @Test
        fun `no reservation exists`() {
            assertThat(service.getPeriodReservations(today, today), empty())
        }

        @Test
        fun `only match today's reservation`() {
            reservationRepository.saveAll(listOf(reservationYesterday, reservationToday, reservationTomorrow))
            assertThat(service.getPeriodReservations(today, today), containsInAnyOrder(reservationToday))
        }

        @Test
        fun `match 2 reservations`() {
            reservationRepository.saveAll(listOf(reservationYesterday, reservationToday, reservationTomorrow))
            assertThat(
                    service.getPeriodReservations(yesterday, today),
                    containsInAnyOrder(reservationYesterday, reservationToday))
        }

        @Test
        fun `match all reservations`() {
            reservationRepository.saveAll(listOf(reservationYesterday, reservationToday, reservationTomorrow))
            assertThat(
                    service.getPeriodReservations(yesterday, tomorrow),
                    containsInAnyOrder(reservationYesterday, reservationToday, reservationTomorrow))
        }

        @Test
        fun `match all reservations given a long time period`() {
            reservationRepository.saveAll(listOf(reservationYesterday, reservationToday, reservationTomorrow))
            assertThat(
                    service.getPeriodReservations(today.minusMonths(1), today.plusMonths(1)),
                    containsInAnyOrder(reservationYesterday, reservationToday, reservationTomorrow))
        }

        @Test
        fun `when period is invalid throw an exception`() {
            assertThrows<InvalidDatesException> {
                reservationRepository.save(reservationToday)
                service.getPeriodReservations(tomorrow, today)
            }
        }

    }


    @Nested
    @DisplayName("get reservations by username")
    inner class UserReservations {

        @Test
        fun `get all reservations by user`() {
            reservationRepository.saveAll(listOf(reservationToday, reservationTomorrow))
            assertThat(service.getUserReservations(username), containsInAnyOrder(reservationToday, reservationTomorrow))
        }

        @Test
        fun `when user does not have any reservations return none`() {
            reservationRepository.saveAll(listOf(reservationToday, reservationTomorrow))
            assertThat(service.getUserReservations("user-without-reservations"), empty())
        }

    }


    @Nested
    @DisplayName("get reservations by workplace")
    inner class WorkplaceReservations {

        private val bossWorkplace = Workplace(null, "Chefsessel", 42, 42, "mapId", "")
        private val bossReservation = Reservation(null, today, today, "chef", bossWorkplace)

        @BeforeEach
        fun saveBossWorkplace() {
            workplaceRepository.save(bossWorkplace)
        }

        @Test
        fun `get all reservations of one user`() {
            reservationRepository.saveAll(listOf(reservationYesterday, reservationToday, reservationTomorrow, bossReservation))
            assertThat(
                    service.getWorkplaceReservations(yesterday, tomorrow, workplace.id!!),
                    containsInAnyOrder(reservationYesterday, reservationToday, reservationTomorrow))
        }

        @Test
        fun `return reservation of another user`() {
            reservationRepository.saveAll(listOf(reservationYesterday, reservationToday, reservationTomorrow, bossReservation))
            assertThat(
                    service.getWorkplaceReservations(yesterday, tomorrow, bossWorkplace.id!!),
                    containsInAnyOrder(bossReservation))
        }

        @Test
        fun `when workplace does not exist throw exception`() {
            assertThrows<DoesNotExistException> {
                service.getWorkplaceReservations(today, today, 11111)
            }
        }

    }

}
