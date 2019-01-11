package de.iteratec.iteraOfficeMap.reservation

import de.iteratec.iteraOfficeMap.workplace.Workplace

class ReservationDTO(
        var id: Long,
        var date: Long,
        var endDate: Long,
        var user: String,
        var workplace: Workplace
) {


    constructor(reservation: Reservation) : this(
            reservation.workplace.id!!,
            reservation.startDate,
            reservation.endDate,
            reservation.username,
            reservation.workplace
    )

}
