package de.iteratec.officemap.reservation

import de.iteratec.officemap.workplace.Workplace
import java.time.LocalDate

class ReservationDTO(
        val id: Long,
        val startDate: LocalDate,
        val endDate: LocalDate,
        val user: String,
        val workplace: Workplace
)
