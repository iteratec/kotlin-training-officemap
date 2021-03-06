//true if Weekdays can be chosen
var weekdaysShown = false;
//contains all workplaces
var workplaces = [];
//contains selected WorkplaceId
var selectedWorkplaceId = null;
//contains a date with the selected month and year of the datepicker
var selMonthYear = LocalDate.now();
var periodReservations = [];

var startDate = LocalDate.now();
var endDate = startDate;

var noWeekdayChecked = true;

var scrChangedToSmall = false;
var allReservationDays = [];
var workplaceReservationDays = [];
var workplaceReservations = [];
var selectedStatus = null;
var checkedWeekdays = [];
var weekdayDateList = [];

function updateWorkplaces() {
    loadWorkplaces()
        .done(function (response) {
            workplaces = response;
            buildPeriodMap();
        })
}

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


// lets a workplace pulse and calls initCalendar() and updates all on click
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
            if ($(this).data("status") === "available") {
                selectedStatus = "available";
            } else {
                selectedStatus = "reserved";
            }
            initCalendar();
            updateAll();
            document.getElementById("accordion").scrollIntoView({behavior: 'smooth'});
        });


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

