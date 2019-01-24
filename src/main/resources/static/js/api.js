/**
 * Helper function for a reservation: we need to convert the string dates like "2019-01-24" of reservations to their
 * JSJoda.LocalDate variants.
 * @returns The same reservation object (mutated), with `startDate` and `endDate` converted to LocalDates.
 */
function stringDatesToLocalDates(reservation) {
    // parse dates as LocalDate objects
    reservation.startDate = JSJoda.LocalDate.parse(reservation.startDate);
    reservation.endDate = JSJoda.LocalDate.parse(reservation.endDate);
    return reservation;
}

/**
 * Create multiple reservations.
 */
function postReservations(reservations) {
    return $.ajax({
        type: "POST",
        url: "/api/addreservations",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(reservations)
    });
}

/**
 * Loads all workplaces.
 */
function loadWorkplaces() {
    return $.get("/api/allworkplaces");
}
