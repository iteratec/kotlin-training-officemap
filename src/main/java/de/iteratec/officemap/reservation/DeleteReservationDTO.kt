package de.iteratec.officemap.reservation

import java.time.LocalDate

class DeleteReservationDTO(
        val workplaceId: Long,
        val startDate: LocalDate,
        val endDate: LocalDate
)
