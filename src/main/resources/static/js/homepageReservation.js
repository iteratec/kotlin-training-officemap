

var reservationList = [];

//shows Weekdayboxes to check 
function onClickWeekdays() {
	if(!weekdaysShown){
		showWeekdays(parsedStartDate, parsedEndDate);
		weekdaysShown = true;
		updateCheckedWeekdays();
		$("#selectWeekdays").val("keine Wochentage");
	} else {
		checkedWeekdays = [];
		$(".weekday").hide();
		weekdaysShown = false;
		initialize();
		$("#selectWeekdays").val("Wochentage auswählen");
	}
}

//emptys checkedWeekdays and adds the selected ones
function updateCheckedWeekdays() {
	checkedWeekdays = [];
	$('.weekday:checked').each(function(i) {
		checkedWeekdays[i] = $(this).val();
	});
	
	weekdayDateList = [];
	weekdayDateList = searchWeekdays(parsedStartDate, parsedEndDate,
			checkedWeekdays);
	
	noWeekdayChecked = true;
	for(var i=0; i<weekdayDateList.length; i++){
		if(checkedWeekdays.indexOf(new Date(weekdayDateList[i].parsedStartDate).getDay().toString()) != -1)
			noWeekdayChecked = false;
	}
	
	initialize();
}

//if all required data is provided add reservation objects to reservationList
//and call postReservation()
//else return error message
function calcDays() {
	// today at 0am
	var actualDate = new Date();
	actualDate.setHours(0);
	actualDate.setMinutes(0);
	actualDate.setSeconds(0);
	actualDate.setMilliseconds(0);
	
	// check if user is selected
	if ($('#user').val() == '') {
		// check if worplace is selceted
		if (selWorkplace == null) {
			$("#errorMessage").html(
					"Bitte einen Namen und Arbeitsplatz angeben!");
		} else {
			$("#errorMessage").html("Bitte einen Namen angeben!");
		}
	} else if (selWorkplace == null) { // check if workplace is selcted
		$("#errorMessage").html("Bitte einen Arbeitsplatz auswählen!");
	} else if (selectedStatus == "reserved") { // check if workplace is already reserved
		$("#errorMessage").html("Arbeitsplatz ist schon vergeben!");
	} else if(noWeekdayChecked && weekdaysShown){ // check if weekdays are selected 
		$("#errorMessage").html("Kein Wochentag ausgewählt!");
	} else if (parsedStartDate < actualDate){ // checke if date is in past time
			$("#errorMessage").html("Liegt in der Vergangenheit!");
	} else {
		var option = $("#option").val();
		if (weekdaysShown) {
			for (var i = 0; i < weekdayDateList.length; i++) {
				var reservation = {};
				reservation.workplaceId = selWorkplace;
				reservation.startDate = weekdayDateList[i].parsedStartDate;
				reservation.endDate = weekdayDateList[i].parsedEndDate;
				reservationList.push(reservation);
			}
			postReservation();
		} else {
			var reservation = {};
			reservation.workplaceId = selWorkplace;
			reservation.startDate = parsedStartDate;
			reservation.endDate = parsedEndDate;
			reservationList.push(reservation);
			postReservation();
		} 
	}
}
	
	


//http request to add the reservationList to the database
function postReservation() {
	$.ajax({
		type : "POST",
		url : "/api/addreservations",
		beforeSend : function(xhr) {
			xhr.overrideMimeType("text/plain; charset=x-user-defined");
		},
		contentType : "application/json; charset=utf-8",
		data : JSON.stringify(reservationList)
	}).done(function() {
		$('#successMessage').html("Reservierung erfolgreich!");
		$('#successMessage').show(0).delay(2000).fadeOut(400);
		initialize();
		reservationList = [];
	});
}

