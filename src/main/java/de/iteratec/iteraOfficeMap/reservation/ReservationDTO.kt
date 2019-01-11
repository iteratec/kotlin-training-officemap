package de.iteratec.iteraOfficeMap.reservation

import de.iteratec.iteraOfficeMap.workplace.Workplace

class ReservationDTO(
        val id: Long,
        val date: Long,
        val endDate: Long,
        val user: String,
        val workplace: Workplace
) {


    constructor(reservation: Reservation) : this(
            reservation.workplace.id!!,
            reservation.startDate,
            reservation.endDate,
            reservation.username,
            reservation.workplace
    )

}
