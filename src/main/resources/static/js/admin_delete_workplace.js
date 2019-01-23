var workplaces = null;
var selWorkplace = null;

//on document ready call initialize
$(document).ready(function () {
    initialize();

});

$(window).resize(function () {
    console.log($(this).height() + " " + $(this).width())

});


// calls getWorkplaces and buildMap on callback
function initialize() {
    getWorkplaces(workplaceInitializer);

}

function workplaceInitializer(workplaces) {
    buildMap("/images/delete.png", false, true, true, workplaces);

}

//http request to delete one workplace from the database
function deleteWorkplace() {
    $.ajax({
        url: "/api/deleteworkplace",
        type: "DELETE",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify({
            id: selWorkplace
        }),
        success: function (response) {
            initialize();
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
        selWorkplace = $(this).data("workplaceId");
        var selWorkplaceName = $(this).data("workplaceName");
        document.getElementById("selectedWorkplace").innerHTML = selWorkplaceName;
        $(this).addClass("pulsation");
    });
}
