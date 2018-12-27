//shows the selected start and end date in the status bar
function showDate() {
	var selStartDate = $("#datepickerStart").val();
	$("#selectedStartDate").html(selStartDate);

	var selEndDate = $("#datepickerEnd").val();
	$("#selectedEndDate").html(selEndDate);
	
	if(weekdaysShown)
		showWeekdays(parsedStartDate, parsedEndDate);
}

// creates a list with all dates which are in the time period and fit on of the
// selected weekdays
function searchWeekdays(parsedStartDate, parsedEndDate, checkedWeekdays) {
	// search for all weekdaysChecked in range of parsedStartDate to parsedEndDate
	var j = 0;
	for (var i = 0; i < checkedWeekdays.length; i++) {
		
		
		var startDateIterator = parsedStartDate;
		var endDateIterator = parsedStartDate + MILLISPERDAY - 1;
		
		var timeOffset = ((endDateIterator - startDateIterator + 1) / MILLISPERDAY);
		
		while (startDateIterator <= parsedEndDate) {
			var timeOffset = ((endDateIterator - startDateIterator + 1) / MILLISPERDAY);
			var d = new Date(startDateIterator);
			if (d.getDay() == checkedWeekdays[i]) {
				weekdayDateList[j] = { parsedStartDate:startDateIterator, parsedEndDate:endDateIterator};
				j++;
			}
			startDateIterator = startDateIterator + MILLISPERDAY;
			endDateIterator = endDateIterator + MILLISPERDAY;
			
			// for summer or wintertime difference
			if(new Date(startDateIterator).getHours()==23){
				startDateIterator += 3600000;
			}
			
		}
	}
	
	
	return weekdayDateList;
}

// checks how many weekdays are in the selected time period and shows them
function showWeekdays(parsedStartDate, parsedEndDate) {
	// IDs of the Elements in HTML file
	var weekdays = [ "SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT" ];
	
	// Check if the selected timespan is more or equal than 7 days
	if (((parsedEndDate - parsedStartDate + 1) / MILLISPERDAY ) >= 7) {
		$(".weekday").show();
	} else { // only show weekdays in the selected timspan
		$(".weekday").hide();
		for (var i = parsedStartDate; i <= parsedEndDate; i = i + MILLISPERDAY) {
			var date = new Date(i);
			var weekday = date.getDay();
			$("#" + weekdays[weekday]).show();
		}
	}
}

function buildMap(imageSrc, imageID, wpID, pulse, workplaces){

    var backgroundImage = $('<img>');
    backgroundImage.attr("src", "/images/westhafentower.png");
    backgroundImage.attr("class", "officemap");
    if(imageID){
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

        if(wpID){
            workplaceImg.data("workplaceId", workplaces[i].id);
        }
        if(pulse){
            pulseOnClick(workplaceImg);
        }
        $('#image').append(workplaceImg);
    }
}

// http request to get a list of all workplaces
function getWorkplaces(callback) {
    $.get("/api/allworkplaces", function(data) {
        workplaces = data;
        callback(data);
    }).fail(function() {
        alert("error");
    });
}