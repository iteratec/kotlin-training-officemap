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
        var currentDate = startDate;

        while (currentDate <= endDate) {
            if (currentDate.dayOfWeek() === checkedWeekdays[i]) {
                weekdayDateList.append({date: currentDate});
            }
            currentDate = currentDate.plusDays(1)
        }
    }

    return weekdays;
}

// checks how many weekdays are in the selected time period and shows them
function showWeekdays() {
    // IDs of the Elements in HTML file
    var weekdays = ["SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"];

    // Check if the selected timespan is more or equal than 7 days
    if (startDate.until(endDate, JSJoda.ChronoUnit.DAYS) >= 7) {
        $(".weekday").show();
    } else { // only show weekdays in the selected timspan
        $(".weekday").hide();
        for (var date = startDate; date <= endDate; date = date.plusDays(1)) {
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
        var image = imageSrc;
        var workplaceImg = $('<img>');
        var title = workplaces[i].name;
        workplaceImg.attr("title", title);
        workplaceImg.attr("src", image);
        workplaceImg.attr("class", "workplace");
        workplaceImg.css("left", workplaces[i].x + "px");
        workplaceImg.css("top", workplaces[i].y + "px");
        workplaceImg.data("workplaceName", workplaces[i].name);
        workplaceImg.data("workplaceEquipment", workplaces[i].equipment);
        workplaceImg.data("x", workplaces[i].x);
        workplaceImg.data("y", workplaces[i].y);

        if (wpID) {
            workplaceImg.data("workplaceId", workplaces[i].id);
        }

        if (pulse) {
            pulseOnClick(workplaceImg);
        }
        $('#image').append(workplaceImg);
    }
}

// http request to get a list of all workplaces
function getWorkplaces(callback) {
    $.get("/api/allworkplaces", function (data) {
        workplaces = data;
        callback(data);
    }).fail(function () {
        alert("error");
    });
}