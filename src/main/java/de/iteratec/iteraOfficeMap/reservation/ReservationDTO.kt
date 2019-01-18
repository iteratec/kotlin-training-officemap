package de.iteratec.iteraOfficeMap.reservation

import de.iteratec.iteraOfficeMap.workplace.Workplace

class ReservationDTO(
        val id: Long,
        val date: Long,
        val endDate: Long,
        val user: String,
        val workplace: Workplace
)
