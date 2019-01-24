var workplaces = [];

$(document).ready(function () {
    updateWorkplaces();
});

function updateWorkplaces() {
    loadWorkplaces()
        .done(function (response) {
            workplaces = response;
            buildMap("/images/available.png", "office", true, false, workplaces);
            getCoordinates();
        });
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

// http request to add a workplace to the database
function addNewWorkplace() {
    var message = $("#message");
    message.html("");
    var workplace = {
        equipment: ($("#workplaceEquipment").val()),
        name: ($("#workplaceName").val()),
        x: ($("#xCoordinate").val() - 9),
        y: ($("#yCoordinate").val() - 9),
        mapId: "frankfurt_office" // TODO choose mapId
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
            updateWorkplaces();
        }
        //TODO show message if adding a new workplace failed
    })

}




