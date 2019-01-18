package de.iteratec.iteraOfficeMap.reservation

data class AddReservationDTO(val workplaceId: Long,
                             val startDate: Long,
                             val endDate: Long)
