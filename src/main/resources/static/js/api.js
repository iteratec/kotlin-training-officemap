function postReservations(reservations) {
    return $.ajax({
        type: "POST",
        url: "/api/addreservations",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(reservations)
    });
}
