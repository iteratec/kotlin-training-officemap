package de.iteratec.officemap.reservation

data class AddReservationDTO(val workplaceId: Long,
                             val startDate: Long,
                             val endDate: Long)
