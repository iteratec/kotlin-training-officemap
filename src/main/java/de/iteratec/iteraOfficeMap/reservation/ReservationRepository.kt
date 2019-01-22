package de.iteratec.iteraOfficeMap.reservation

import de.iteratec.iteraOfficeMap.workplace.Workplace
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional

import java.time.LocalDate

interface ReservationRepository : JpaRepository<Reservation, Long> {

    fun findByStartDateLessThanEqualAndEndDateGreaterThanEqual(endDate: LocalDate, startDate: LocalDate): List<Reservation>

    fun findByStartDateLessThanEqualAndEndDateGreaterThanEqualAndWorkplace(
            endDate: LocalDate, startDate: LocalDate, workplace: Workplace): List<Reservation>

    fun findByUsernameEqualsOrderByStartDateAsc(username: String): List<Reservation>

    @Transactional
    fun deleteByWorkplaceAndStartDateAndEndDate(
            workplace: Workplace, startDate: LocalDate, endDate: LocalDate): List<Reservation>

}
