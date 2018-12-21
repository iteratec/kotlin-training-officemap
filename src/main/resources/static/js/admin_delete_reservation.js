var workplaces = null;
var userReservations = null;
var user = null;
var selectedReservations = [];

// get workplaces and build map on document ready
$(document).ready(function() {
	getWorkplaces(workplaceInitializer);
	submitFunction();

})

function submitFunction() {
    $("#search-form").submit(function() {
        loadUserData();
        return false;
    });
}



function initOnWorkplacesLoaded() {
	$('#reservationTable').html("");
    buildMap("/images/delete.png", false, false, false, workplaces);
	loadUserData();
}

function workplaceInitializer(workplaces) {
    buildMap("/images/delete.png", false, false, false, workplaces);
}

// if a user name is inserted gets his reservations and calls
// buildReservationTable
// else shows an error message
function loadUserData() {
	if (!($('#user').val() == '')) {
		$('#errorMessage').html("");
		$('#selectedUser').html("von " + $('#user').val());
		getUserReservations(buildReservationTable);
	} else {
		$('#selectedUser').html("");
		$('#errorMessage').html("Bitte einen Namen eintragen!");
		$('#reservationTable').html(
				"Bitte Mitarbeiter auswählen und anzeigen lassen!")
	}
}


// http request to get a list of all reservations for a user
function getUserReservations(callback) {
	var user = document.getElementById("user").value;
	$.ajax({
		url : "/api/getuserreservations?user=" + user,
		type : "GET",
		contentType : "text/plain; charset=utf-8",
		success : function(response) {
			userReservations = response;
			callback.call();
		}
	}).fail(function() {
		alert("error");
	});
}

// gets all checked checkboxes
// pushes the checked reservations to a list
// calls deleteReservations
function getCheckedReservations() {
	var tableInputs = $('.workplaceCheckbox');
	var checkboxList = [];
	var j = 0;
	for (var i = 0; i < tableInputs.length; i++) {
		if (tableInputs[i].checked == true) {
			checkboxList[j] = tableInputs[i].id;
			j++;
		}
	}
	if (checkboxList.length == 0) {
		$('#errorMessage').html("Bitte Reservierungen auswählen!");
	} else {
		for (var i = 0; i < checkboxList.length; i++) {
			var reservation = {
				workplaceId : userReservations[checkboxList[i]].workplace.id,
				startDate : userReservations[checkboxList[i]].date,
                endDate : userReservations[checkboxList[i]].endDate
			};
			selectedReservations.push(reservation);
		}
		deleteReservations();
	}
}

//http request to delete a list of reservations for a user
function deleteReservations() {
	$.ajax({
		type : "DELETE",
		url : "/api/deletereservations",
		beforeSend : function(xhr) {
			xhr.overrideMimeType("text/plain; charset=x-user-defined");
		},
		contentType : "application/json; charset=utf-8",
		data : JSON.stringify(selectedReservations)
	}).done(function() {
		getWorkplaces(initOnWorkplacesLoaded);
		loadUserData();
	});
}

// Builds the HTML table out of parsedjson
function buildReservationTable() {
	// Create dynamic table
	var table = document.createElement("table");
	table.setAttribute("class", "table table-striped");
	table.id = "tableId";

	// Create a table header row
	var header = table.createTHead();
	var headerRow = header.insertRow(0);

	var thWorkplace = document.createElement("th"); // TABLE HEADER.
	thWorkplace.innerHTML = "Arbeitsplatz";
	headerRow.appendChild(thWorkplace);

	var thDate = document.createElement("th");// TABLE HEADER.
	thDate.innerHTML = "Datum";
	headerRow.appendChild(thDate);

	var thDelete = document.createElement("th"); // TABLE HEADER.
	var thCheckbox = document.createElement('input');
	thCheckbox.id = "thCheckbox";
	thCheckbox.type = "checkbox";
	thCheckbox.onchange = function() {
		if ($('#thCheckbox').prop('checked')) {
			$('.workplaceCheckbox').prop('checked', true);
		} else {
			$('.workplaceCheckbox').prop('checked', false);
		}
	};
	thDelete.appendChild(thCheckbox);
	headerRow.appendChild(thDelete);

	// Create table body and fill it with userReservations data
	var body = table.createTBody();
	var today = new Date();
	today.setUTCHours(0, 0, 0, 0);
	var parsedToday = Date.parse(today);

	for (var i = 0; i < userReservations.length; i++) {
		if (userReservations[i].date >= parsedToday
				|| userReservations[i].endDate >= parsedToday) {
			tr = body.insertRow(-1);
			tr.classList.add("Reservierung");

			var workplaceCell = document.createElement("td");
			workplaceCell.innerHTML = userReservations[i].workplace.name;
			tr.appendChild(workplaceCell);

			var dateCell = document.createElement("td");
			var startDateString = userReservations[i].date;
			var endDateString = userReservations[i].endDate;
			var startDate = new Date(startDateString);
			var newStartDate = ('0' + startDate.getDate()).slice(-2) + '.'
					+ ('0' + (startDate.getMonth() + 1)).slice(-2) + '.'
					+ startDate.getFullYear();
			var endDate = new Date(endDateString);
			var newEndDate = ('0' + endDate.getDate()).slice(-2) + '.'
					+ ('0' + (endDate.getMonth() + 1)).slice(-2) + '.'
					+ endDate.getFullYear();
			if (startDateString < endDateString) {
				dateCell.innerHTML = newStartDate + " - " + newEndDate;
			} else {
				dateCell.innerHTML = newStartDate;
			}
			tr.appendChild(dateCell);

			var checkboxCell = document.createElement("td");
			var checkbox = document.createElement('input');
			checkbox.className += "workplaceCheckbox";
			checkbox.type = "checkbox";
			checkbox.id = i;
			checkboxCell.appendChild(checkbox);
			tr.appendChild(checkboxCell);
		}
	}

	// Append the created table to the div container
	var divContainer = document.getElementById("reservationTable");
	divContainer.innerHTML = "";
	divContainer.appendChild(table);

	defineAsDataTable();
}

// Integration of jQuery Datatables
function defineAsDataTable() {
	$('#tableId').DataTable({
		// Add german language plug-in
		"language" : {
			"url" : "/datatables/plug-ins/de_DE.json"
		},
		// deactivate sorting for checkboxes and set dates column datatype to
		// de_date
		"columnDefs" : [ {
			"orderable" : false,
			"targets" : 2
		}, {
			type : 'de_date',
			targets : 1
		} ],
		// add filter option "all"
		"lengthMenu" : [ [ 10, 25, 50, 100, -1 ], [ 10, 25, 50, 100, "Alle" ] ]
	});
}