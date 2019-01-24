package de.iteratec.officemap.reservation

import de.iteratec.officemap.workplace.Workplace
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface ReservationRepository : JpaRepository<Reservation, Long> {

    fun findByStartDateLessThanEqualAndEndDateGreaterThanEqual(endDate: LocalDate, startDate: LocalDate): List<Reservation>

    fun findByStartDateLessThanEqualAndEndDateGreaterThanEqualAndWorkplace(
            endDate: LocalDate, startDate: LocalDate, workplace: Workplace): List<Reservation>

    fun findByUsernameEqualsOrderByStartDateAsc(username: String): List<Reservation>

}
