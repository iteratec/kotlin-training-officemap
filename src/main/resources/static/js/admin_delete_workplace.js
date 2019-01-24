var workplaces = [];
var selectedWorkplaceId = null;

//on document ready call initialize
$(document).ready(function () {
    updateWorkplaces();
});


function updateWorkplaces() {
    loadWorkplaces().done(function (response) {
        workplaces = response;

        buildMap("/images/delete.png", false, true, true, workplaces);
    });
}

//http request to delete one workplace from the database
function deleteWorkplace() {
    $.ajax({
        url: "/api/deleteworkplace",
        type: "DELETE",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify({
            id: selectedWorkplaceId
        }),
        success: function (response) {
            updateWorkplaces();
        }
    }).fail(function () {
        alert("error");
    });
}

// shows pop up to confirm deletion
function clickedDelete() {
    if (confirm("Soll der Arbeitsplatz endgültig gelöscht werden?")) {
        deleteWorkplace();
    } else {
        return false;
    }
}


//lets a workplace pulse on click and saves the ID to selectedWorkplaceId
function pulseOnClick(workplaceImg) {
    workplaceImg.click(function () {
        $('.pulsation').toggleClass('pulsation');
        selectedWorkplaceId = $(this).data("workplaceId");
        var selectedWorkplaceName = $(this).data("workplaceName");
        document.getElementById("selectedWorkplace").innerHTML = selectedWorkplaceName;
        $(this).addClass("pulsation");
    });
}
