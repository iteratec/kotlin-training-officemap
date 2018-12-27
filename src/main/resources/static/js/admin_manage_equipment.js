var workplaces = null;
var selWorkplace = null;

//on document ready call initialize
$(document).ready(function () {
    initialize();

})

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

function manageEquipment() {

}
//lets a workplace pulse on click and saves the ID to selWorkplace
function pulseOnClick(workplaceImg) {
    workplaceImg.click(function () {
        $('.pulsation').toggleClass('pulsation');
        var selWorkplaceName = $(this).data("workplaceName");
        document.getElementById("selectedWorkplace").innerHTML = selWorkplaceName;
        selWorkplace = $(this).data("workplaceId");
        $(this).addClass("pulsation");
    });
}
