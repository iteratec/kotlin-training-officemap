//true if Weekdays can be chosen
var weekdaysShown = false;
//contains all workplaces
var workplaces = null;
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
var workplaceReservations = null;
var selectedStatus = null;
var checkedWeekdays = [];
var weekdayDateList = [];

function initOnCalcDates() {
    getWorkplaceReservations(generateWorkplaceReservationDays);
}


// checks if the selected dates are valid for one day or a time period
// else returns a error message
function initialize() {
    if (startDate < endDate) {
        getPeriodReservations();
    } else if (startDate > endDate) {
        $("#errorMessage").html("Das Enddatum muss größer oder gleich dem Startdatum sein!");
    } else if (weekdaysShown) {
        showWeekdays();
    }
    if (selectedWorkplaceId) {
        initOnCalcDates();
    }
}

function onPeriodReservationsLoaded(reservations) {
    $("#errorMessage").html("");
    getWorkplaces(initOnWorkplaces);
}

// checks if the selected dates are for one day or a time period
// and calls their function to build the map
function initOnWorkplaces() {
    if (startDate < endDate) {
        buildPeriodMap();
    }
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
    var reservations = [];
    for (var i = 0; i < periodReservations.length; i++) {
        // checks if reservation is for a time period or for one day and adds
        // the reservations to the list
        if (periodReservations[i].startDate < periodReservations[i].endDate) {
            for (var currentDate = periodReservations[i].startDate;
                 currentDate <= periodReservations[i].endDate;
                 currentDate = currentDate.plusDays(1)) {

                var reservation = {
                    "date": currentDate,
                    "workplaceId": periodReservations[i].workplace.id
                };
                if (reservation.date >= startDate && reservation.date <= endDate) {
                    reservations.push(reservation);
                }
            }
        } else {
            reservations.push({
                "date": periodReservations[i].date,
                "workplaceId": periodReservations[i].workplace.id
            });
        }
    }

    $('#monthYearPicker').datepicker("refresh");

    return reservations;
}

// creates the office map for a time period
function buildPeriodMap() {
    var allReservationDays = generateAllReservationDays();

    // creates and adds the office background to the div container
    var backgroundImage = $('<img>');
    backgroundImage.attr("src", "/images/westhafentower.png");
    backgroundImage.attr("class", "officemap");

    var divContainer = $("#image");
    divContainer.html("");
    divContainer.append(backgroundImage);

    // places workplaces on the background
    for (var i = 0; i < workplaces.length; i++) {

        var image = "/images/available.png";
        var status = "available";
        var workplaceImg = $('<img>');
        workplaceImg.attr("class", "workplace");

        var counter = calcCounter(i);

        // if the counter is GTE one, the workplace has a reservation
        // if the counter is equal to the number of days in the time period the
        // workplace is reserved the whole time
        // else its reservation is partial
        if (noWeekdayChecked) {
            if (counter >= 1) {
                status = "reserved";
                if (((parsedEndDate - parsedStartDate + 1) / MILLISPERDAY) == counter) {
                    image = "/images/reserved.png";
                } else {
                    image = "/images/partial.png";
                }

            }
        } else {
            if (counter >= 1) {
                status = "reserved";
                if (weekdayDateList.length == counter) {
                    image = "/images/reserved.png";
                } else {
                    image = "/images/partial.png";
                }
            }
        }

        if (selectedWorkplaceId == workplaces[i].id) {
            if (status == "available") {
                workplaceImg.attr("class", "workplace pulsation");
                selectedStatus = "available";
            } else {
                selectedStatus = "reserved";
            }
        }
        workplaceImg.attr("src", image);
        workplaceImg.attr("title", workplaces[i].name);
        workplaceImg.data("status", status);
        workplaceImg.css("left", workplaces[i].x + "px");
        workplaceImg.css("top", workplaces[i].y + "px");
        workplaceImg.attr("id", workplaces[i].id);
        workplaceImg.data("workplaceId", workplaces[i].id);
        workplaceImg.data("workplaceName", workplaces[i].name);
        workplaceImg.data("workplaceEquipment", workplaces[i].equipment);
        workplaceImg.data("x", workplaces[i].x);
        workplaceImg.data("y", workplaces[i].y);
        workplaceImg.data("workplaceId", workplaces[i].id);

        workplaceImg.attr("data-toggle", "tooltip");

        pulseOnClick(workplaceImg);

        $('#image').append(workplaceImg);

    }
}

// calculates counter for adding one of the three images to a workplace
function calcCounter(i) {
    var counter = 0;

    if (noWeekdayChecked) {
        // no weekdays selected
        for (var j = 0; j < allReservationDays.length; j++) {
            if (workplaces[i].id == allReservationDays[j].workplaceId) {
                counter++;
            }
        }

    } else {
        // weekdays selected
        for (var j = 0; j < weekdayDateList.length; j++) {
            for (var k = 0; k < allReservationDays.length; k++) {
                if (workplaces[i].id == allReservationDays[k].id
                    && weekdayDateList[j].parsedStartDate == allReservationDays[k].date) {
                    counter++;
                }
            }
        }

    }

    return counter;
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
                // if date is in selected Timespan

                if (localDate.isAfter(startDate) && localDate.isBefore(endDate)) {
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

//http request to get a list of all reservations for the selected workplace in given time period
function getWorkplaceReservations(callback) {
    var startOfMonth = selMonthYear.withDayOfMonth(1);
    var endOfMonth = startOfMonth.plusMonths(1).minusDays(1);
    $.ajax(
        {
            url: "/api/getworkplacereservations?startDate=" + startOfMonth.toString()
                + "&endDate=" + endOfMonth.toString() + "&workplaceId=" + selectedWorkplaceId,
            type: "GET",
            success: function (response) {
                workplaceReservations = response;
                callback.call();
            }
        }).fail(function () {
        alert("error loading workplace reservations");
    });
}

function generateWorkplaceReservationDays() {
    workplaceReservationDays = [];
    for (var i = 0; i < workplaceReservations.length; i++) {
        // checks if reservation is for a time period or for one day and adds
        // the reservations to the list
        if (workplaceReservations[i].date < workplaceReservations[i].endDate) {
            for (var j = workplaceReservations[i].date; j <= workplaceReservations[i].endDate; j = j
                + MILLISPERDAY) {
                var reservation = {};
                var tempDate = new Date(j);
                var utcDate = new Date(tempDate.getFullYear(), tempDate
                    .getMonth(), tempDate.getDate());
                reservation.date = utcDate.toString();
                reservation.user = workplaceReservations[i].user;
                workplaceReservationDays[utcDate] = reservation;
                workplaceReservationDays.length += 1;
            }
        } else {
            var reservation = {};
            var tempDate = new Date(workplaceReservations[i].date);
            var utcDate = new Date(tempDate.getFullYear(), tempDate.getMonth(),
                tempDate.getDate());
            reservation.date = utcDate.toString();
            reservation.user = workplaceReservations[i].user;
            workplaceReservationDays[utcDate] = reservation;
            workplaceReservationDays.length += 1;
        }
    }
    // refreshes the calendar to show reservations
    $('#monthYearPicker').datepicker("refresh");
}