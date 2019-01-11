package de.iteratec.iteraOfficeMap.reservation

class DeleteReservationDTO(
        var workplaceId: Long,
        var startDate: Long,
        var endDate: Long
) {

    constructor(reservation: Reservation) : this(
            reservation.workplace.id!!,
            reservation.startDate,
            reservation.endDate
    )
}