function onClickWeekdays() {
    if (!weekdaysShown) {
        weekdaysShown = true;
        showWeekdays();
        updateCheckedWeekdays();
        $("#selectWeekdays").val("keine Wochentage");
    } else {
        weekdaysShown = false;
        checkedWeekdays = [];
        $(".weekday").hide();
        initialize();
        $("#selectWeekdays").val("Wochentage auswählen");
    }
}

// empties checkedWeekdays and adds the selected ones
function updateCheckedWeekdays() {
    checkedWeekdays = [];
    $('.weekday:checked').each(function (i) {
        checkedWeekdays[i] = $(this).val();
    });

    weekdayDateList = searchWeekdays(checkedWeekdays);

    noWeekdayChecked = true;
    for (var i = 0; i < weekdayDateList.length; i++) {
        if (checkedWeekdays.indexOf(weekdayDateList[i].date.dayOfWeek()) !== -1) {
            noWeekdayChecked = false;
        }
    }

    initialize();
}

//if all required data is provided add reservation objects to reservationList
//and call postReservation()
//else return error message
function calcDays() {
    // check if user is selected
    if ($('#user').val() === '') {
        // check if workplace is selected
        if (selectedWorkplaceId == null) {
            $("#errorMessage").html("Bitte einen Namen und Arbeitsplatz angeben!");
        } else {
            $("#errorMessage").html("Bitte einen Namen angeben!");
        }
    } else if (selectedWorkplaceId == null) { // check if workplace is selected
        $("#errorMessage").html("Bitte einen Arbeitsplatz auswählen!");
    } else if (selectedStatus === "reserved") { // check if workplace is already reserved
        $("#errorMessage").html("Arbeitsplatz ist schon vergeben!");
    } else if (noWeekdayChecked && weekdaysShown) { // check if weekdays are selected
        $("#errorMessage").html("Kein Wochentag ausgewählt!");
    } else if (startDate < JSJoda.LocalDate.now()) { // check if date is in past time
        $("#errorMessage").html("Liegt in der Vergangenheit!");
    } else {
        var reservations = [];
        if (weekdaysShown) {
            for (var i = 0; i < weekdayDateList.length; i++) {
                reservations.push({
                    "workplaceId": selectedWorkplaceId,
                    "startDate": weekdayDateList[i].date.toString(),
                    "endDate": weekdayDateList[i].date.toString()
                });
            }
        } else {
            reservations.push({
                "workplaceId": selectedWorkplaceId,
                "startDate": startDate.toString(),
                "endDate": endDate.toString()
            });
        }
        postReservations(reservations);
    }
}

function postReservations(reservations) {
    $.ajax({
        type: "POST",
        url: "/api/addreservations",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(reservations)
    }).done(function () {
        var successMessage = $('#successMessage');
        successMessage.html("Reservierung erfolgreich!");
        successMessage.show(0).delay(2000).fadeOut(400);
        initialize();
    });
}
