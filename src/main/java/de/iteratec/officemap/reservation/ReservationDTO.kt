package de.iteratec.officemap.reservation

import de.iteratec.officemap.workplace.Workplace

class ReservationDTO(
        val id: Long,
        val date: Long,
        val endDate: Long,
        val user: String,
        val workplace: Workplace
)
