package de.iteratec.officemap.reservation

import java.time.LocalDate

data class AddReservationDTO(val workplaceId: Long,
                             val startDate: LocalDate,
                             val endDate: LocalDate)
