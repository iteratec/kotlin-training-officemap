var userReservations = [];

function username() {
    return $('#user').val();
}

function loadUserData() {
    if (username() !== '') {
        $('#errorMessage').html("");
        $('#selectedUser').html("von " + username());

        loadReservationsByUsername(username())
            .done(function (response) {
                userReservations = response.map(stringDatesToLocalDates);
                buildReservationTable();
            });
    } else {
        $('#selectedUser').html("");
        $('#errorMessage').html("Bitte einen Namen eintragen!");
        $('#reservationTable').html("Bitte Mitarbeiter auswählen und anzeigen lassen!")
    }
}

function updateWorkplaces() {
    loadWorkplaces()
        .done(function (response) {
            workplaces = response;

            $('#reservationTable').html("");
            buildMap("/images/neutral.png", false, false, false, workplaces);
            loadUserData();
        })
}

function deleteCheckedReservations() {
    var allCheckboxes = $('.workplaceCheckbox');
    var checkedReservationIds = [];
    for (var i = 0; i < allCheckboxes.length; i++) {
        var checkbox = allCheckboxes[i];
        if (checkbox.checked === true) {
            checkedReservationIds.push(checkbox.userReservation.id);
        }
    }

    if (checkedReservationIds.length === 0) {
        $('#errorMessage').html("Bitte Reservierungen auswählen!");
    } else {
        deleteReservations(checkedReservationIds)
            .always(function () {
                updateWorkplaces();
            });
    }
}

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
    thCheckbox.onchange = function () {
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

    getActiveOrFutureUserReservations().forEach(function (userReservation) {
        var tr = body.insertRow(-1);
        tr.classList.add("Reservierung");

        var workplaceCell = document.createElement("td");
        workplaceCell.innerHTML = userReservation.workplace.name;
        tr.appendChild(workplaceCell);

        var dateCell = document.createElement("td");

        var pattern = DateTimeFormatter.ofPattern('dd.MM.yyyy');
        if (userReservation.startDate.isEqual(userReservation.endDate)) {
            dateCell.innerHTML = userReservation.startDate.format(pattern);
        } else {
            dateCell.innerHTML = userReservation.startDate.format(pattern) + " - " + userReservation.endDate.format(pattern);
        }
        tr.appendChild(dateCell);

        var checkboxCell = document.createElement("td");
        var checkbox = document.createElement('input');
        checkbox.className += "workplaceCheckbox";
        checkbox.type = "checkbox";

        checkbox.userReservation = userReservation;

        checkboxCell.appendChild(checkbox);
        tr.appendChild(checkboxCell);
    });

    // Append the created table to the div container
    var divContainer = document.getElementById("reservationTable");
    divContainer.innerHTML = "";
    divContainer.appendChild(table);

    defineAsDataTable();
}

function getActiveOrFutureUserReservations() {
    var today = LocalDate.now();
    return userReservations.filter(function (userReservation) {
        return !userReservation.endDate.isBefore(today);
    });
}

// Integration of jQuery Datatables
function defineAsDataTable() {
    $('#tableId').DataTable({
        // Add german language plug-in
        "language": {
            "url": "/datatables/plug-ins/de_DE.json"
        },
        // deactivate sorting for checkboxes and set dates column datatype to
        // de_date
        "columnDefs": [{
            "orderable": false,
            "targets": 2
        }, {
            type: 'de_date',
            targets: 1
        }],
        // add filter option "all"
        "lengthMenu": [[10, 25, 50, 100, -1], [10, 25, 50, 100, "Alle"]]
    });
}
