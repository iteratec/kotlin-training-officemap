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
 * Creates multiple reservations.
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
 * Deletes a workplace, including all its reservations.
 */
function deleteWorkplace(workplaceId) {
    return $.ajax({
        url: "/api/workplaces/" + workplaceId,
        type: "DELETE"
    });
}

/**
 * Loads all workplaces.
 */
function loadWorkplaces() {
    return $.get("/api/workplaces");
}

/**
 * Creates a new workplace.
 */
function createWorkplace(workplace) {
    return $.ajax({
        url: "/api/workplaces",
        type: "POST",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(workplace)
    });
}

/**
 * Updates an existing workplace.
 */
function updateWorkplace(workplace) {
    return $.ajax({
        url: "/api/workplaces/" + workplace.id,
        type: "PUT",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(workplace)
    });
}

/**
 * Loads all reservations of a username.
 */
function loadReservationsByUsername(username) {
    return $.get("/api/getuserreservations?user=" + username);
}

/**
 * Loads reservations by time period.
 */
function loadReservationsByPeriod(startDate, endDate) {
    return $.get("/api/getperiodreservations?startDate=" + startDate.toString() + "&endDate=" + endDate.toString());
}

/**
 * Loads reservations by workplace ID and time period.
 */
function loadReservationsByWorkplaceAndPeriod(workplaceId, startDate, endDate) {
    return $.get("/api/getworkplacereservations?startDate=" + startDate.toString() + "&endDate=" + endDate.toString() + "&workplaceId=" + workplaceId);
}

/**
 * Deletes reservations specified by their IDs.
 * @param reservationIds A list of reservation IDs that should be deleted.
 */
function deleteReservations(reservationIds) {
    return $.ajax({
        type: "DELETE",
        url: "/api/deletereservations",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify({
            "reservationIds": reservationIds
        })
    });
}

/**
 * gets the username of the currently loggedin user
 *
 */
function loadUsername() {
    return $.get("/api/user");
}
