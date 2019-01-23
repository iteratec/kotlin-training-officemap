//true if Weekdays can be chosen
var weekdaysShown = false;
//contains all workplaces
var workplaces = [];
//contains selected WorkplaceId
var selectedWorkplaceId = null;
//contains a date with the selected month and year of the datepicker
var selMonthYear = JSJoda.LocalDate.now();
var periodReservations = [];

var startDate = JSJoda.LocalDate.now();
var endDate = startDate;

var noWeekdayChecked = true;

var scrChangedToSmall = false;
var allReservationDays = [];
var workplaceReservationDays = [];
var workplaceReservations = [];
var selectedStatus = null;
var checkedWeekdays = [];
var weekdayDateList = [];

function initOnCalcDates() {
    getWorkplaceReservations(generateWorkplaceReservationDays);
}


// checks if the selected dates are valid for one day or a time period
// else returns a error message
function initialize() {
    getPeriodReservations();

    if (selectedWorkplaceId != null) {
        initOnCalcDates();
    }
}

function onPeriodReservationsLoaded(reservations) {
    periodReservations = reservations.map(stringDatesToLocalDates);
    $("#errorMessage").html("");
    getWorkplaces(buildPeriodMap);
}

function stringDatesToLocalDates(reservation) {
    // parse dates as LocalDate objects
    reservation.startDate = JSJoda.LocalDate.parse(reservation.startDate);
    reservation.endDate = JSJoda.LocalDate.parse(reservation.endDate);
    return reservation;
}

// on document ready load datepicker and set dates to today
$(document).ready(function () {
    $("#datepickerStart").datepicker({
        dateFormat: 'dd.mm.yy',
        minDate: 0,
        changeMonth: true,// this option for allowing user to select month
        changeYear: true, // this option for allowing user to select from year
        altField: '#actualDateStart', // altField for date used in JS
        altFormat: 'yy-mm-dd' // alt date format
    });
    $("#datepickerStart").datepicker().datepicker("setDate", new Date());

    $("#datepickerEnd").datepicker({
        dateFormat: 'dd.mm.yy',
        minDate: 0,
        changeMonth: true,// this option for allowing user to select month
        changeYear: true, // this option for allowing user to select from year
        altField: '#actualDateEnd', // altField for date used in JS
        altFormat: 'yy-mm-dd' // alt date format


    });
    $("#datepickerEnd").datepicker().datepicker("setDate", new Date());


    resizerWithPanel();


    showDate();
    defineDate();
    initCalendar();

    $('body').on('touchstart.dropdown', '.dropdown-menu', function (e) {
        e.stopPropagation();
    });
});

$(window).resize(function () {
    resizerWithPanel();
});

function resizerWithPanel() {
    if ($(this).width() < 997) {

        $('#ph').show();
        //$('#collapseOne').removeClass('in');
        $('.panel-heading').attr('data-toggle', 'collapse');
        scrChangedToSmall = true;

    } else {
        $('#collapseOne').addClass('in');
        $('.panel-heading').attr('data-toggle', 'show');
        $('#ph').hide();
        scrChangedToSmall = false;
    }
}


// gets selected start + end date from date picker
function defineDate() {
    startDate = JSJoda.LocalDate.parse($("#actualDateStart").val());
    endDate = JSJoda.LocalDate.parse($("#actualDateEnd").val());

    if (startDate.isAfter(endDate)) {
        endDate = startDate;
        $("#datepickerEnd").val($("#datepickerStart").val());
        $("#actualDateEnd").val($("#actualDateStart").val());
    }

    console.log("startDate = " + startDate + ", endDate = " + endDate);

    updateCheckedWeekdays();
    initialize();
}


// adds an reservation object for every day in a time period to a list
// and returns the list
function generateAllReservationDays() {
    var reservationDays = [];
    for (var i = 0; i < periodReservations.length; i++) {
        var periodReservation = periodReservations[i];

        // checks if reservation is for a time period or for one day and adds
        // the reservations to the list
        if (periodReservation.startDate.isBefore(periodReservation.endDate)) {
            for (var date = periodReservation.startDate;
                 date.isBefore(periodReservation.endDate) || date.isEqual(periodReservation.endDate);
                 date = date.plusDays(1)) {

                var reservation = {
                    "date": date,
                    "workplaceId": periodReservation.workplace.id
                };
                if (
                    (reservation.date.isEqual(startDate) || reservation.date.isAfter(startDate)) &&
                    (reservation.date.isEqual(endDate) || reservation.date.isBefore(endDate))
                ) {
                    reservationDays.push(reservation);
                }
            }
        } else {
            reservationDays.push({
                "date": periodReservation.startDate,
                "workplaceId": periodReservation.workplace.id
            });
        }
    }

    $('#monthYearPicker').datepicker("refresh");

    return reservationDays;
}

// creates the office map for a time period
function buildPeriodMap() {
    allReservationDays = generateAllReservationDays();

    // creates and adds the office background to the div container
    var backgroundImage = $('<img>');
    backgroundImage.attr("src", "/images/westhafentower.png");
    backgroundImage.attr("class", "officemap");

    var divContainer = $("#image");
    divContainer.html("");
    divContainer.append(backgroundImage);

    // places workplaces on the background
    for (var i = 0; i < workplaces.length; i++) {
        var workplace = workplaces[i];
        var image = "/images/available.png";
        var status = "available";
        var workplaceImg = $('<img>');
        workplaceImg.attr("class", "workplace");

        var reservedDays = getReservedDays(workplace);

        if (noWeekdayChecked) {
            if (reservedDays >= 1) {
                status = "reserved";
                if (startDate.daysUntil(endDate) + 1 >= reservedDays) {
                    image = "/images/reserved.png";
                } else {
                    image = "/images/partial.png";
                }

            }
        } else {
            if (reservedDays >= 1) {
                status = "reserved";
                if (weekdayDateList.length === reservedDays) {
                    image = "/images/reserved.png";
                } else {
                    image = "/images/partial.png";
                }
            }
        }

        if (selectedWorkplaceId === workplace.id) {
            if (status === "available") {
                workplaceImg.attr("class", "workplace pulsation");
                selectedStatus = "available";
            } else {
                selectedStatus = "reserved";
            }
        }
        workplaceImg.attr("src", image);
        workplaceImg.attr("title", workplace.name);
        workplaceImg.data("status", status);
        workplaceImg.css("left", workplace.x + "px");
        workplaceImg.css("top", workplace.y + "px");
        workplaceImg.attr("id", workplace.id);
        workplaceImg.data("workplaceId", workplace.id);
        workplaceImg.data("workplaceName", workplace.name);
        workplaceImg.data("workplaceEquipment", workplace.equipment);
        workplaceImg.data("x", workplace.x);
        workplaceImg.data("y", workplace.y);
        workplaceImg.data("workplaceId", workplace.id);

        workplaceImg.attr("data-toggle", "tooltip");

        pulseOnClick(workplaceImg);

        $('#image').append(workplaceImg);
    }
}

// calculates number of reserved days for a workplace
function getReservedDays(workplace) {
    var reservationDaysOfWorkplace = allReservationDays.filter(function (reservationDay) {
        return reservationDay.workplaceId === workplace.id;
    });

    if (noWeekdayChecked) {
        // no weekdays selected, so count every reservation with a matching workplaceId
        return reservationDaysOfWorkplace.length;

    } else {
        // weekdays selected
        var reservedDays = 0;
        for (var i = 0; i < reservationDaysOfWorkplace.length; i++) {
            var currentReservationDay = reservationDaysOfWorkplace[i];
            for (var j = 0; j < weekdayDateList.length; j++) {
                var weekdayDate = weekdayDateList[j];
                if (weekdayDate.startDate === currentReservationDay.date) {
                    reservedDays++;
                }
            }
        }
        return reservedDays;
    }
}


//lets a workplace pulse and calls initCalendar() and initOnCalcDates() on click
function pulseOnClick(workplaceImg) {

    workplaceImg
        .click(function () {
            selectedWorkplaceId = $(this).data("workplaceId");
            $('.pulsation').toggleClass('pulsation');
            var selWorkplaceName = $(this).data("workplaceName");
            var selWorkplaceEquipment = $(this).data("workplaceEquipment");
            document.getElementById("selectedWorkplace").innerHTML = selWorkplaceName;
            document.getElementById("selectedWorkplaceEquipment").innerHTML = selWorkplaceEquipment;
            $(this).addClass("pulsation");
            if ($(this).data("status") == "available") {
                selectedStatus = "available";
            } else {
                selectedStatus = "reserved";
            }
            initCalendar();
            initOnCalcDates();
            document.getElementById("accordion").scrollIntoView({behavior: 'smooth'});
        });


}

// http request to get a list of all workplaces
function getWorkplaces(callback) {
    $.get("/api/allworkplaces", function (data) {
        workplaces = data;
        callback.call();
    }).fail(function () {
        alert("error");
    });
}

function getPeriodReservations() {
    $.ajax(
        {
            url: "/api/getperiodreservations?startDate=" + startDate.toString() + "&endDate=" + endDate.toString(),
            type: "GET",
            success: function (response) {
                onPeriodReservationsLoaded(response);
            }
        }).fail(function () {
        alert("error");
    });
}

//-------------------------------------------------CALENDER ONLY-------------------------------------------------------------------------------------

//initializes the calendar to show workplace reservations
function initCalendar() {

    $('#monthYearPicker').datepicker(
        {
            changeMonth: true,
            changeYear: true,
            // if a workplace is selected highlight its reservation days by applying class event to it
            beforeShowDay: function (date) {
                var localDate = JSJoda.LocalDate.ofInstant(JSJoda.Instant.ofEpochMilli(date.getTime()));
                if (selectedWorkplaceId != null) {
                    //check if selected workplace has a reservation for this date
                    var highlight = workplaceReservationDays[localDate];
                    if (highlight) {
                        return [
                            true,
                            "event",
                            "Reserviert von: "
                            + workplaceReservationDays[localDate].user];
                    }
                }
                // if date is in selected time period

                if (localDate.isAfter(startDate) && !localDate.isAfter(endDate)) {
                    if (weekdaysShown) {
                        // for all selected weekdays: check if they match the given date
                        for (var i = 0; i < weekdayDateList.length; i++) {
                            if (weekdayDateList[i].startDate === localDate) {
                                return [
                                    true,
                                    "eventChosen",
                                    "Verfügbar"
                                ]
                            }
                        }
                        return [
                            true,
                            "",
                            "Verfügbar"
                        ]
                    } else {
                        return [
                            true,
                            "eventChosen",
                            "Verfügbar"
                        ]
                    }
                } else {
                    return [true, '', 'Verfügbar'];
                }

                return [true, '', 'Verfügbar'];
            },
            onChangeMonthYear: function (year, month) {
                selMonthYear = JSJoda.LocalDate.of(year, month, 1);
                if (selectedWorkplaceId != null) {
                    initOnCalcDates();
                }
            }
        });
}

//shows the selected start and end date in the status bar
function showDate() {
    var selStartDate = $("#datepickerStart").val();
    $("#selectedStartDate").html(selStartDate);

    var selEndDate = $("#datepickerEnd").val();
    $("#selectedEndDate").html(selEndDate);

    if (weekdaysShown) {
        showWeekdays();
    }
}

// creates a list with all dates which are in the time period and fit on of the
// selected weekdays
function searchWeekdays(checkedWeekdays) {
    var weekdays = [];
    for (var i = 0; i < checkedWeekdays.length; i++) {
        for (var date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            if (date.dayOfWeek() === checkedWeekdays[i]) {
                weekdayDateList.append({"date": date});
            }
        }
    }

    return weekdays;
}

// checks how many weekdays are in the selected time period and shows them
function showWeekdays() {
    // IDs of the Elements in HTML file
    var weekdays = ["SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"];

    // Check if the selected time period is more or equal than 7 days
    if (startDate.until(endDate, JSJoda.ChronoUnit.DAYS) >= 7) {
        $(".weekday").show();
    } else { // only show weekdays in the selected time period
        $(".weekday").hide();
        for (var date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            $("#" + weekdays[date.dayOfWeek() - 1]).show();
        }
    }
}

function buildMap(imageSrc, imageID, wpID, pulse, workplaces) {

    var backgroundImage = $('<img>');
    backgroundImage.attr("src", "/images/westhafentower.png");
    backgroundImage.attr("class", "officemap");
    if (imageID) {
        backgroundImage.attr("id", "office");
    }

    var divContainer = $("#image");
    divContainer.html("");
    divContainer.append(backgroundImage);

    for (var i = 0; i < workplaces.length; i++) {
        var workplace = workplaces[i];
        var image = imageSrc;
        var workplaceImg = $('<img>');
        var title = workplace.name;
        workplaceImg.attr("title", title);
        workplaceImg.attr("src", image);
        workplaceImg.attr("class", "workplace");
        workplaceImg.css("left", workplace.x + "px");
        workplaceImg.css("top", workplace.y + "px");
        workplaceImg.data("workplaceName", workplace.name);
        workplaceImg.data("workplaceEquipment", workplace.equipment);
        workplaceImg.data("x", workplace.x);
        workplaceImg.data("y", workplace.y);

        if (wpID) {
            workplaceImg.data("workplaceId", workplace.id);
        }

        if (pulse) {
            pulseOnClick(workplaceImg);
        }
        $('#image').append(workplaceImg);
    }
}

//http request to get a list of all reservations for the selected workplace in given time period
function getWorkplaceReservations(callback) {
    var startOfMonth = selMonthYear.withDayOfMonth(1);
    var endOfMonth = startOfMonth.plusMonths(1).minusDays(1);
    $.ajax(
        {
            url: "/api/getworkplacereservations?startDate=" + startOfMonth.toString()
                + "&endDate=" + endOfMonth.toString() + "&workplaceId=" + selectedWorkplaceId,
            type: "GET",
            success: function (reservations) {
                workplaceReservations = reservations.map(stringDatesToLocalDates);
                callback.call();
            }
        }).fail(function () {
        alert("error loading workplace reservations");
    });
}

function generateWorkplaceReservationDays() {
    workplaceReservationDays = {};
    for (var i = 0; i < workplaceReservations.length; i++) {
        var workplaceReservation = workplaceReservations[i];

        // adds each day separately
        for (var date = workplaceReservation.startDate; !date.isAfter(workplaceReservation.endDate); date = date.plusDays(1)) {
            workplaceReservationDays[date] = {
                "date": date,
                "user": workplaceReservation.user
            };
        }
    }
    // refreshes the calendar to show reservations
    $('#monthYearPicker').datepicker("refresh");
}

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
        postReservations(reservations)
            .done(function () {
                var successMessage = $('#successMessage');
                successMessage.html("Reservierung erfolgreich!");
                successMessage.show(0).delay(2000).fadeOut(400);
                initialize();
            });
    }
}
