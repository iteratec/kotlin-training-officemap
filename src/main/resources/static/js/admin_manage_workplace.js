var workplaces = null;
var selWorkplace = null;
var selWorkplaceId = null;


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
    buildMap("/images/equipment.png", false, true, true, workplaces);
}

function setChanges() {
    var message = $("#message");
    message.html("");

    if(!selWorkplaceId){
        message.html("Bitte einen Arbeitsplatz auswählen!");
        return;
    }

    var workplace = {
        id: selWorkplaceId,
        name: ($("#workplaceName").val()),
        x: ($("#workplaceX").val()),
        y: ($("#workplaceY").val()),
        mapId: "frankfurt_office",
        equipment: ($("#workplaceEquipment").val())
    };

    if(workplace.name == ""){
        message.html("Der Name eines Arbeitsplatzes darf nicht leer sein!");
        return;
    }

    if(isNaN(workplace.x) || isNaN(workplace.y) || workplace.x == "" || workplace.y == ""){
        message.html("Die Koordinaten eines Arbeitsplatzes müssen eine Nummer sein!");
        return;
    }

    $.ajax({
        url: "/api/updateworkplace",
        type: "PUT",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(workplace),
        success: function (response) {
            initialize();
        }
    }).fail(function () {
        alert("error");
    });
}
//lets a workplace pulse on click and saves the ID to selectedWorkplaceId
function pulseOnClick(workplaceImg) {
    workplaceImg.click(function () {
        $('.pulsation').toggleClass('pulsation');
        var selWorkplaceName = $(this).data("workplaceName");
        var selWorkplaceEquipment = $(this).data("workplaceEquipment");
        var selWorkplaceX = $(this).data("x");
        var selWorkplaceY = $(this).data("y");
        selWorkplaceId = $(this).data("workplaceId");
        document.getElementById("workplaceName").value = selWorkplaceName;
        document.getElementById("workplaceEquipment").value = selWorkplaceEquipment;
        document.getElementById("workplaceX").value = selWorkplaceX;
        document.getElementById("workplaceY").value = selWorkplaceY;
        $(this).addClass("pulsation");
    });
}
