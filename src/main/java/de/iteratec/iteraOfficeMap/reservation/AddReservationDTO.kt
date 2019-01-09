package de.iteratec.iteraOfficeMap.reservation

data class AddReservationDTO(val workplaceId: Long,
                             val startDate: Long,
                             val endDate: Long) {

    constructor(reservation: Reservation) : this(
            reservation.workplace.id!!,
            reservation.startDate,
            reservation.endDate
    )

}
