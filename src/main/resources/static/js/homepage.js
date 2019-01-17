var MILLISPERDAY = 86400000;

//true if Weekdays can be chosen
var weekdaysShown = false;
//contains all workplaces
var workplaces = null;
//contains selected WorkplaceId
var selWorkplace = null;
//contains a date with the selected month and year of the datepicker
var selMonthYear = new Date();
// contain all reservations in period or day
var dailyReservations = null;
var periodReservations = null;
// start and end Date of the selected Month
var startDate = null;
var endDate = null;
var noWeekdayChecked = true;

var scrChangedToSmall = false;
var parsedStartDate = null;
var parsedEndDate = null;
var monthStartDate = null;
var monthEndDate = null;
var allReservationDays = [];
var workplaceReservationDays = [];
var selectedWorkplaceReservationDays = [];
var workplaceReservations = null;
var selectedStatus = null;
var checkedWeekdays = [];
var weekdayDateList = [];


function inputChanged() {

    getDays(initOnCalcDates);
}

function initOnCalcDates() {
    getWorkplaceReservations(generateWorkplaceReservationDays);
}

function getDays(callback) {
    var startDate = new Date(selMonthYear.getFullYear(), selMonthYear
        .getMonth(), 1);
    monthStartDate = Date.parse(startDate);
    var endDate = new Date(selMonthYear.getFullYear(),
        selMonthYear.getMonth() + 1, 0, 23, 59, 59, 999);
    monthEndDate = Date.parse(endDate);
    callback.call();
}


// checks if the selected dates are valid for one day or a time period
// else returns a error message
function initialize() {

    if (parsedStartDate < parsedEndDate) {
        getPeriodReservations(initOnMapBuilded);
    } else if (parsedStartDate > parsedEndDate) {
        $("#errorMessage").html(
            "Das Enddatum muss größer oder gleich dem Startdatum sein!");
    } else if (weekdaysShown) {
        showWeekdays(parsedStartDate, parsedEndDate);
    }
    if (selWorkplace)
        inputChanged();


}

function initOnMapBuilded() {
    $("#errorMessage").html("");
    getWorkplaces(initOnWorkplaces);


}

// checks if the selected dates are for one day or a time period
// and calls their function to build the map
function initOnWorkplaces() {

    if (parsedStartDate < parsedEndDate) {
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


// gets the selected start and end date
// parses their values to millis since 1970
function defineDate() {
    // get Values from Datepicker
    startDate = $("#actualDateStart").val();
    endDate = $("#actualDateEnd").val();
    console.log(startDate);
    // parse to actual Time Zone. Start at 0am and end at 23:59:59:999pm
    var realTimezoneStartDate = new Date(startDate);
    console.log("Date: " + realTimezoneStartDate);
    var realTimezoneEndDate = new Date(endDate);

    if (realTimezoneStartDate > realTimezoneEndDate) {
        $("#datepickerEnd").val($("#datepickerStart").val());
        $("#actualDateEnd").val($("#actualDateStart").val());
        endDate = $("#actualDateEnd").val();
        realTimezoneEndDate = new Date(endDate);
    }
    var timezoneOffset = realTimezoneStartDate.getTimezoneOffset();
    realTimezoneStartDate.setMinutes(realTimezoneStartDate.getMinutes() + timezoneOffset);
    realTimezoneEndDate.setMinutes(realTimezoneEndDate.getMinutes() + timezoneOffset);
    realTimezoneEndDate.setMilliseconds(realTimezoneEndDate.getMilliseconds() + MILLISPERDAY - 1);
    //parse to Milliseconds
    parsedStartDate = realTimezoneStartDate.getTime();
    parsedEndDate = realTimezoneEndDate.getTime();


    //check if summer or wintertime
    var timeOffset = ((parsedEndDate - parsedStartDate + 1) / MILLISPERDAY);
    if (timeOffset % 1 > 0) {
        parsedEndDate -= 3600000;
    } else if (timeOffset % 1 < 0) {
        parsedStartDate -= 3600000;
    }


    // Update
    updateCheckedWeekdays();
    initialize();
}


// adds an reservation object for every day in a time period to a list
// and returns the list
function generateAllReservationDays() {
    allReservationDays = [];
    for (var i = 0; i < periodReservations.length; i++) {
        // checks if reservation is for a time period or for one day and adds
        // the reservations to the list
        if (periodReservations[i].date < periodReservations[i].endDate) {
            for (var j = periodReservations[i].date; j <= periodReservations[i].endDate; j = j
                + MILLISPERDAY) {


                var reservation = {};
                reservation.date = j;
                reservation.workplaceId = periodReservations[i].workplace.id;
                if (reservation.date >= parsedStartDate
                    && reservation.date <= parsedEndDate) {
                    allReservationDays.push(reservation);
                }
            }
        } else {


            var reservation = {};
            reservation.date = periodReservations[i].date;
            reservation.workplaceId = periodReservations[i].workplace.id;
            allReservationDays.push(reservation);
        }
    }

    $('#monthYearPicker').datepicker("refresh");


    return allReservationDays;
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

        if (selWorkplace == workplaces[i].id) {
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

//caclulates counter for adding one of the three images to a workplace
function calcCounter(i) {
    var counter = 0;

    //Weekdays selected
    if (!noWeekdayChecked) {

        for (var j = 0; j < weekdayDateList.length; j++) {
            for (var k = 0; k < allReservationDays.length; k++) {
                if (workplaces[i].id == allReservationDays[k].id
                    && weekdayDateList[j].parsedStartDate == allReservationDays[k].date) {
                    counter++;
                }
            }
        }

    } else { //no weekdays selected

        for (var j = 0; j < allReservationDays.length; j++) {
            if (workplaces[i].id == allReservationDays[j].workplaceId) {
                counter++;
            }
        }

    }

    return counter;
}


//lets a workplace pulse and calls initCalendar() and inputChanged() on click
function pulseOnClick(workplaceImg) {

    workplaceImg
        .click(function () {
            selWorkplace = $(this).data("workplaceId");
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
            inputChanged();
            document.getElementById("accordion").scrollIntoView({ behavior: 'smooth'});
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

//http request to get all reservation between parsedStartDate and parsedEndDate
function getPeriodReservations(callback) {
    $.ajax(
        {
            url: "/api/getperiodreservations?startDate=" + parsedStartDate
                + "&endDate=" + parsedEndDate,
            type: "GET",
            beforeSend: function (xhr) {
                xhr.overrideMimeType("text/plain; charset=x-user-defined");
            },
            contentType: "application/json; charset=utf-8",
            success: function (response) {
                periodReservations = JSON.parse(response);
                callback.call();
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
                if (selWorkplace != null) {
                    //check if selected workplace has a reservation for this date
                    var highlight = workplaceReservationDays[date];
                    if (highlight) {
                        return [
                            true,
                            "event",
                            "Reserviert von: "
                            + workplaceReservationDays[date].user];
                    }
                }
                // if date is in selected Timespan

                if (date.getTime() >= parsedStartDate && date.getTime() <= parsedEndDate) {
                    if (weekdaysShown) {
                        // for all selected weekdays: check if they match the given date
                        for (var i = 0; i < weekdayDateList.length; i++) {
                            if ((weekdayDateList[i].parsedStartDate) == date.getTime() || (weekdayDateList[i].parsedStartDate) == (date.getTime() + 3600000)) {
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
                selMonthYear = new Date(year, month - 1);
                if (selWorkplace != null) {
                    inputChanged();
                }
            }
        });
}

//http request to get a list of all reservations for the selected workplace between
//parsedStartDate and parsedEndDate
function getWorkplaceReservations(callback) {
    $.ajax(
        {
            url: "/api/getworkplacereservations?startDate=" + monthStartDate
                + "&endDate=" + monthEndDate + "&workplaceId="
                + selWorkplace,
            type: "GET",
            beforeSend: function (xhr) {
                xhr.overrideMimeType("text/plain; charset=x-user-defined");
            },
            contentType: "application/json; charset=utf-8",
            success: function (response) {
                workplaceReservations = JSON.parse(response);
                callback.call();
            }
        }).fail(function () {
        alert("error");
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