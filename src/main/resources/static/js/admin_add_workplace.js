var workplaces = null;
var selWorkplace = null;

// on document ready call initialize
$(document).ready(function () {
    initialize();
});


// calls getWorkplaces and initOnWorkplaces on callback
function initialize() {
    getWorkplaces(initOnWorkplaces);
}

// calls buildMap and getCoordinates
function initOnWorkplaces(workplaces) {
    buildMap("/images/available.png", "office", true, false, workplaces);
    getCoordinates();
}


// gets the coordinates of a click on the office map
function getCoordinates() {
    $("#office")
        .click(
            function (e) {
                var posX = Math.round(e.pageX - $(this).offset().left), posY = Math
                    .round(e.pageY - $(this).offset().top);
                $("#xCoordinate").val(posX);
                $("#yCoordinate").val(posY);
            });
}

function submitFunction() {
    $("#add-form").submit(function () {
        return false;
    });
}

// http request to add a workplace to the database
function addNewWorkplace() {
    var message = $("#message");
    message.html("");
    var workplace = {
        equipment: ($("#workplaceEquipment").val()),
        name: ($("#workplaceName").val()),
        x: ($("#xCoordinate").val() - 9),
        y: ($("#yCoordinate").val() - 9),
        mapId: "frankfurt_office" //TODO choose mapId
    };

    if (workplace.name === "") {
        message.html("Bitte einen Arbeitsplatznamen eingeben!");
        return;
    }

    $.ajax({
        url: "/api/addworkplace",
        type: "POST",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(workplace),
        success: function (response) {
            message.html("Arbeitsplatz erfolgreich erstellt!");
            initialize();
        }
        //TODO show message if adding a new workplace failed
    })

}




