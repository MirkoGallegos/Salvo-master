

$.getJSON(relatedUrl("gp"), function(json) {
    var data = json;
    console.log(data);
    document.getElementById('state').innerText = data.state;
    printShipsGrid("shipTHead1", "#shipTBody1");
    printSalvoGrid("salvoTHead2", "#salvoTBody2");
    printShips(data);
    printSalvos(data);
    usersTitle(data);
    createHitsTable(data);
    winOrLose(data);
    startState(data);
    waitOppState(data);
    waitOppShipsState(data);
    putFirstSalvoState(data);
    salvoTurnState(data);
    waitOppSalvoState(data);
});

function reloadContent(OldStatus) {
    $.getJSON(relatedUrl("gp"), function (data) {
        if (OldStatus != data.state){
            window.location.reload();
        }
    });
}

function startState(data) {

    if (data.state === '1-start') {
        document.getElementById('state').innerText = 'You can place your ships';
        document.getElementById('createSalvo').setAttribute('class', 'hide');
    }
}

function waitOppState(data) {

    if (data.state === '2-waiting for opp') {
        document.getElementById('state').innerText = 'You must wait for an opponent';
        document.getElementById('shipsList').setAttribute('class', 'hide');
        document.getElementById('createShips').setAttribute('class', 'hide');
        document.getElementById('createSalvo').setAttribute('class', 'hide');
        setInterval(function () {
            reloadContent("2-waiting for opp");
        }, 7500);
    }
}

function waitOppShipsState(data) {

    if (data.state === '3-waiting opp place ships') {
        document.getElementById('state').innerText = 'You must wait for opponent ships';
        document.getElementById('shipsList').setAttribute('class', 'hide');
        document.getElementById('createShips').setAttribute('class', 'hide');
        document.getElementById('createSalvo').setAttribute('class', 'hide');
        setInterval(function () {
            reloadContent("3-waiting opp place ships");
        }, 7500);
    }
}

function putFirstSalvoState(data) {

    if (data.state === '4-you can start to add salvo') {
        document.getElementById('state').innerText = 'You can start to add salvo';
        document.getElementById('shipsList').setAttribute('class', 'hide');
        document.getElementById('createShips').setAttribute('class', 'hide');
        document.getElementById('tableSalvo').setAttribute('class', 'show');
        document.getElementById('hitsTable').setAttribute('class', 'show');
    }
}

function salvoTurnState(data) {

    if (data.state === '5-it is your turn to add salvo') {
        document.getElementById('state').innerText = 'It is your turn to add salvo';
        document.getElementById('shipsList').setAttribute('class', 'hide');
        document.getElementById('createShips').setAttribute('class', 'hide');
        document.getElementById('tableSalvo').setAttribute('class', 'show');
        document.getElementById('hitsTable').setAttribute('class', 'show');
        setInterval(function () {
            reloadContent("5-it is your turn to add salvo");
        }, 7500);
    }
}

function waitOppSalvoState(data) {

    if (data.state === '6-waiting for opp add salvo') {
        document.getElementById('state').innerText = 'You must wait to opponent salvo';
        document.getElementById('shipsList').setAttribute('class', 'hide');
        document.getElementById('createShips').setAttribute('class', 'hide');
        document.getElementById('createSalvo').setAttribute('class', 'hide');
        document.getElementById('tableSalvo').setAttribute('class', 'show');
        document.getElementById('hitsTable').setAttribute('class', 'show');
        setInterval(function () {
            reloadContent("6-waiting for opp add salvo");
        }, 7500);
    }
}



//Función para crear salvos

function createSalvo(salvoCreated){

    var gpId = getParameterByName("gp");

    $.post({
        url: "/games/players/" + gpId + "/salvos",
        data: JSON.stringify(salvoCreated),
        dataType: "JSON",
        contentType: "application/json"
    }).done(function(){
        location.reload();
        alert("salvo created SUCCESFULLY");
    }).fail(function(response){
        alert(response.responseJSON.error);
    })
}

$('#createSalvo').click(function() {

    if (infoSalvo.salvoLocation.length < 5){

        alert('The number of shots is too low. You must have 5 shots.');

    } else if (infoSalvo.salvoLocation.length > 5) {

        alert('The number of shots is too high. You must have 5 shots.');

    } else {
        createSalvo(infoSalvo);
    }
});

var infoSalvo = {"salvoLocation": []};

function salvoClick(e) {

    var idCell = e.target.getAttribute('id');
    console.log(idCell);

    if (e.target.getAttribute('data-salvo') === "true") {

        e.target.setAttribute('data-salvo', 'false');
        document.getElementById(idCell).style.backgroundColor = "";
        infoSalvo.salvoLocation.pop();

        console.log(infoSalvo);

    } else if (e.target.getAttribute('data-salvo2') === "created") {

        alert('You already have a shot in this cell');

    } else {

        e.target.setAttribute('data-salvo', 'true');
        document.getElementById(idCell).style.backgroundColor = "red";
        infoSalvo.salvoLocation.push(idCell);

    }
}

//Funcion para que el usuario cree barcos desde el frontend

function createShips(shipsCreated){

    var gpId = getParameterByName("gp");

    $.post({
        url: "/games/players/" + gpId + "/ships",
        data: JSON.stringify(shipsCreated),
        dataType: "JSON",
        contentType: "application/json"

    }).done(function(){
        alert("ships created SUCCESFULLY");
        location.reload();

    }).fail(function(response){
        alert(response.responseJSON.error);
    })
}

$('#createShips').click(function(){

    var totalLocations = warningOverlapShip(infoShips);
    var shipsInGrid = detectOutGrid(infoShips);

    var allLocations = getAllLocations(infoShips);

    if (allLocations.length < 17){

        alert("Error in number of ships");

    } else if (totalLocations === true) {

        alert("There are ships overlap");

    } else if (shipsInGrid === true) {

        alert("There are ships out grid");

    } else {
        createShips(infoShips);

    }

});

//Funcion inicial del drag and drop

var infoShips = [{"shipType": "destroyer", "shipLoc": []},
    {"shipType": "submarine", "shipLoc": []},
    {"shipType": "battleship", "shipLoc": []},
    {"shipType": "aircraft", "shipLoc": []},
    {"shipType": "patrolboat", "shipLoc": []}];

var dragged;
function dragStart(e) {

    dragged = e.target;
    console.log("observando lo que me trae el dragg; ",dragged);
    e.dataTransfer.setData("Data", e.target.id);
    e.dataTransfer.effectAllowed = 'move';
    e.target.style.opacity = '0.4';
    e.target.style.backgroundColor = '';

    console.log("drag START");
}

function dragOver(e) {

    e.preventDefault();

}

function dragEnter(e) {


    e.target.style.border = '3px dotted #555';
}

function dragLeave(e) {
    e.target.style.border = '';
}

function dragEnd(e) {
    e.target.style.opacity = '';  // Restaura la opacidad del elemento
    e.dataTransfer.clearData("Data");
}

function drop(e) {
    // this / e.target is current target element.
    if (e.stopPropagation) {
        e.stopPropagation(); // stops the browser from redirecting.
    }

    e.preventDefault();

    var data = e.dataTransfer.getData("Data");
    e.target.appendChild(document.getElementById(data));

    // Posicion del elemento sobre el que se arrastra

    posXHorizontal = $(e.target).position().left;
    posYHorizontal = $(e.target).position().top;

    var dataStyle = document.getElementById(data).style;

    dataStyle.position = "absolute";
    dataStyle.backgroundColor = 'lightgreen';
    dataStyle.marginTop="-16px";
    // dataStyle.left = posXHorizontal + 25 + "px";
    /* dataStyle.top = posYHorizontal - 50 + "px";
    e.target.style.border = '';

   if (e.target === 'aircraft') {
         dataStyle.left = posXHorizontal + 15 + "px";
     } else if (e.target === 'battleship') {
         dataStyle.left = posXHorizontal + 10 + "px";
     } else if (e.target === 'destroyer' ||
         e.target === 'submarine') {
         dataStyle.left = posXHorizontal + 5 + "px";
     } else if (e.target === 'patrolboat') {
         dataStyle.left = posXHorizontal + "px";
     }
  */
     document.getElementById(data).setAttribute('data-firstCell', e.target.id);

     createShipLoc(data, infoShips, e.target.id);

     if(warningOverlapShip(infoShips) === true) {

         alert('There is a ship overlap');

     }

     if(detectOutGrid(infoShips) === true) {
         alert('There is a ship out of grid');
     }

 }

 function detectOutGrid(shipArray) {

     for (var i=0; i<shipArray.length; i++) {
         for (var j=0; j<shipArray[i].shipLoc.length; j++){
             var shipLocLetter = shipArray[i].shipLoc[j].substring(0, 1);
             var shipLocLetterAsci = shipLocLetter.charCodeAt(0);
             var shipLocNumber = shipArray[i].shipLoc[j].substring(1);

             if (shipLocLetterAsci > 74 || shipLocNumber > 10) {

                 return true;
             }
         }
     }
 }


 Array.prototype.unique=function(a){

     return function(){return this.filter(a)}}(function(a,b,c){return c.indexOf(a,b+1)<0
 });

 function getAllLocations(shipsArray) {

     var allLocations = [];

     for (var i=0; i<shipsArray.length; i++) {

         for (var j=0; j<shipsArray[i].shipLoc.length; j++) {

             allLocations.push(shipsArray[i].shipLoc[j]);

         }
     }

     return allLocations;
 }

 function warningOverlapShip(shipArray) {

     var allLocations = getAllLocations(shipArray);
     var allLocationsSize = allLocations.length;
     var locationsWithoutRepeted = allLocations.unique();
     var locationsWithoutRepetedSize = locationsWithoutRepeted.length;

     if (locationsWithoutRepetedSize < allLocationsSize) {

         return true;

     } else {

         return false;
     }
 }

 function createDragElements(idShip) {
     document.getElementById(idShip).addEventListener('dragstart', dragStart, false);
     document.getElementById(idShip).addEventListener('dragend', dragEnd, false);
 }

 createDragElements('aircraft');
 createDragElements('battleship');
 createDragElements('destroyer');
 createDragElements('submarine');
 createDragElements('patrolboat');

 function createShipLoc(idShip, shipsArray, firstCell) {

     var dataClass = document.getElementById(idShip).getAttribute('class');

     for (var i=0; i<shipsArray.length; i++) {

         if (idShip === shipsArray[i].shipType) {

             if (dataClass == 'aircraft') {

                 createHorizontalShipLocArray(5, shipsArray[i].shipLoc, firstCell);

             }

             if (dataClass == 'battleship') {

                 createHorizontalShipLocArray(4, shipsArray[i].shipLoc, firstCell);

             }

             if (dataClass == 'destroyer' || dataClass == 'submarine') {

                 createHorizontalShipLocArray(3, shipsArray[i].shipLoc, firstCell);

             }

             if (dataClass == 'patrolboat') {

                 createHorizontalShipLocArray(2, shipsArray[i].shipLoc, firstCell);

             }

             if (dataClass == 'verticalAircraft') {

                 createVerticalShipLocArray(5, shipsArray[i].shipLoc, firstCell);

             }

             if (dataClass == 'verticalBattleship') {

                 createVerticalShipLocArray(4, shipsArray[i].shipLoc, firstCell);

             }

             if (dataClass == 'verticalDestroyer' || dataClass == 'verticalSubmarine') {

                 createVerticalShipLocArray(3, shipsArray[i].shipLoc, firstCell);

             }
             if (dataClass == 'verticalPatrolboat') {

                 createVerticalShipLocArray(2, shipsArray[i].shipLoc, firstCell);

             }
         }
     }
     console.log("info que obtengo ",infoShips);
 }

 function createHorizontalShipLocArray(numberOfRepetitions, shipLocArray, firstCell) {

     var shipLoc = firstCell;
     var shipLocLetter = shipLoc.substring(0, 1);
     var shipLocNumber = shipLoc.substring(1);
     var shipLocNumberInt = parseInt(shipLocNumber);

     if (shipLocArray.length === 0) {

         for (var i=0; i<numberOfRepetitions; i++) {

             shipLocArray.push(shipLocLetter+(shipLocNumberInt+i));
         }

         return shipLocArray;

     }else {

         shipLocArray.length = 0;

         for (var i=0; i<numberOfRepetitions; i++) {

             shipLocArray.push(shipLocLetter+(shipLocNumberInt+i));
         }

         return shipLocArray;
     }
 }

 function createVerticalShipLocArray(numberOfRepetitions, shipLocArray, firstCell) {

     var shipLoc = firstCell;
     var shipLocLetter = shipLoc.substring(0, 1);
     var shipLocLetterAsci = shipLocLetter.charCodeAt(0);
     var shipLocNumber = shipLoc.substring(1);

     if (shipLocArray.length === 0) {

         for (var i=0; i<numberOfRepetitions; i++) {

             shipLocArray.push((String.fromCharCode(shipLocLetterAsci+i)+shipLocNumber));
         }

         return shipLocArray;

     }else {
         shipLocArray.length = 0;

         for (var i=0; i<numberOfRepetitions; i++) {

             shipLocArray.push((String.fromCharCode(shipLocLetterAsci+i)+shipLocNumber));
         }

         return shipLocArray;
     }
 }

 function changeShipPosition (idShip, verticalAttribute, shipsArray) {

     var ship = document.getElementById(idShip);

     ship.addEventListener('click', function(){

         var firstCell = ship.getAttribute('data-firstCell');
         var shipPosition = ship.getAttribute('class');

         if (shipPosition != verticalAttribute) {

             ship.setAttribute('class', verticalAttribute);

             if (firstCell != 0) {

                 createShipLoc(idShip, shipsArray, firstCell);

                 if(warningOverlapShip(infoShips) === true) {
                     alert('There is a ship overlap');
                 }

                 if(detectOutGrid(infoShips) === true) {
                     alert('There is a ship out of grid');
                 }

             }

         }

         if (shipPosition != idShip) {

             ship.setAttribute('class', idShip);

             if (firstCell != 0) {

                 createShipLoc(idShip, shipsArray, firstCell);

                 if(warningOverlapShip(infoShips) === true) {
                     alert('There is a ship overlap');
                 }

                 if(detectOutGrid(infoShips) === true) {
                     alert('There is a ship out of grid');
                 }

             }

         }

     });
 }

 changeShipPosition('aircraft', 'verticalAircraft', infoShips);
 changeShipPosition('battleship', 'verticalBattleship', infoShips);
 changeShipPosition('destroyer', 'verticalDestroyer', infoShips);
 changeShipPosition('submarine', 'verticalSubmarine', infoShips);
 changeShipPosition('patrolboat', 'verticalPatrolboat', infoShips);


 /*funciones para devolver el api correspondiente en función del gp seleccionado*/
function getParameterByName(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
        results = regex.exec(location.search);

    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}

function relatedUrl(locationData) {

    return "https://shipgamesalvo.herokuapp.com/api/game_view/" + getParameterByName(locationData);//https://shipgamesalvo.herokuapp.com
}

/*creacion del grid*/

function printShipsGrid(elementTHead, elementTBody){

    var columnsTitle = ["", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"];
    var rowsTitle = ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"];
    var printGamePlayerGrid1 = document.getElementById(elementTHead);

    var tableHead = document.createElement('tr');

    for(var i = 0; i < columnsTitle.length; i++){

        var columnsTitle1 = columnsTitle[i];

        var rowTitle2 = document.createElement('th');

        rowTitle2.setAttribute('scope', 'col');

        rowTitle2.append(columnsTitle1);
        tableHead.append(rowTitle2);

        var row = "";

        for(var j = 0; j<rowsTitle.length; j++) {
            var rowsTitle1 = rowsTitle[j];
            var emptyCell = "";

            row += "<tr>" + '<td class="letters">' + rowsTitle1 + '</td>';

            for (var k = 0; k < rowsTitle.length; k++) {
                var idCells = rowsTitle[j] + columnsTitle[k+1];

                row += '<td id=' + idCells + " " + 'class="column"' + " " + 'ondrop=' + "drop(event)" + " " + 'ondragleave=' + "dragLeave(event)" + " " + 'ondragenter=' + "dragEnter(event)" + " " + 'ondragover=' + "dragOver(event)" + '>' + emptyCell + '</td>';

            }
            row += "</tr>";
        }

        $(elementTBody).html(row);

    }

    printGamePlayerGrid1.append(tableHead);

}

function printSalvoGrid(elementTHead, elementTBody){

    var columnsTitle = ["", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"];
    var rowsTitle = ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"];
    var printGamePlayerGrid1 = document.getElementById(elementTHead);

    var tableHead = document.createElement('tr');

    for(var i = 0; i < columnsTitle.length; i++){

        var columnsTitle1 = columnsTitle[i];

        var rowTitle2 = document.createElement('th');

        rowTitle2.setAttribute('scope', 'col');

        rowTitle2.append(columnsTitle1);
        tableHead.append(rowTitle2);

        var row = "";

        for(var j = 0; j<rowsTitle.length; j++) {
            var rowsTitle1 = rowsTitle[j];
            var emptyCell = "";

            row += "<tr>" + '<td class="letters">' + rowsTitle1 + '</td>';

            for (var k = 0; k < rowsTitle.length; k++) {
                var idCells = rowsTitle[j] + columnsTitle[k+1];

                row += '<td id=' + 'salvo' + idCells + " " + 'class="column"' + " " + 'onclick=' + "salvoClick(event)" + '>' + emptyCell + '</td>';

            }
            row += "</tr>";
        }

        $(elementTBody).html(row);

    }

    printGamePlayerGrid1.append(tableHead);

}
//Función para printar los salvos del player

function printSalvos(data) {
    for (var i = 0; i < data.gamePlayer.length, i<data.salvoes.length; i++) {

        var gpId = data.gamePlayer[i].id;
        var idUser = data.gamePlayer[i].player.id;
        var gpIdUrl = getParameterByName('gp');
        var salvos = data.salvoes[i];
        var salvosPlayers = Object.keys(salvos);

        if (gpId == gpIdUrl) {

            var turn = salvos[salvosPlayers];

            for (var j = 0; j<turn.length; j++){

                var turns = turn[j];
                var turnsNumber = Object.keys(turns);
                var valueTurn = turns[turnsNumber];

                for (var k=0; k<valueTurn.length; k++) {

                    var valueTurnPosition = valueTurn[k];

                    $(".table2 td").each(function(){
                        var cellId = $(this).attr('id');

                        if (cellId === valueTurnPosition){
                            $(this).css('background-color', 'yellow');
                            document.getElementById(cellId).setAttribute('data-salvo2', 'created');
                            $(this).html(turnsNumber);
                        }
                    })
                }
            }
        }
    }

    if (data.gamePlayer.length === 2) {

        var hits = data.hitsSink.hits;

        for (var k=0; k<hits.length; k++) {

            var hitsLoc = hits[k];

            for (var l=0; l<hitsLoc.length; l++) {

                var hitsLocPosition = hitsLoc[l];

                $(".table2 td").each(function(){

                    var cellHitId = $(this).attr('id');

                    if (cellHitId === "salvo"+hitsLocPosition){
                        $(this).css('background-color', 'red');
                        $(this).html("hit");
                    }
                })
            }
        }
    }
}

//función para printar los barcos y los salvo hits del oponente

function printShips(data) {
    var ships = data.ships;

    for (var i = 0; i<ships.length; i++) {

        var shipsLoc = ships[i].locations;

        for (var j = 0; j<shipsLoc.length; j++) {

            var shipsLocations = shipsLoc[j];
            // $('#'+ shipsLocations).html('gggggg').addClass('cellWithShip');

            $(".table1 td").each(function(){
                var cellId = $(this).attr('id');
                if(cellId === shipsLocations){
                    $(this).css('background-color', 'palegreen');
                }
            })
        }
    }

    for (var m = 0; m < data.gamePlayer.length, m<data.salvoes.length; m++) {

        var gpId = data.gamePlayer[m].id;
        // var idUser = data.gamePlayer[m].player.id;
        var gpIdUrl = getParameterByName('gp');
        var salvos = data.salvoes[m];
        var salvosPlayers = Object.keys(salvos);

        if (gpId != gpIdUrl) {

            var turn = salvos[salvosPlayers];

            for (var l = 0; l<turn.length; l++){

                var turns = turn[l];
                var turnsNumber = Object.keys(turns);
                var valueTurn = turns[turnsNumber];

                for (var n=0; n<valueTurn.length; n++) {
                    var valueTurnPosition = valueTurn[n];

                    $(".table1 td").filter(function(){
                        return $(this).attr('style');
                    })
                        .each(function(){
                        var cellId = $(this).attr('id');
                        var salvoId = valueTurnPosition.substring(5);

                        if(cellId === salvoId) {

                            $(this).css('background-color', 'red');
                            $(this).html(turnsNumber);

                        }
                    })

                }
            }
        }
    }
}

//Función para printar el usuario vs el oponente

function usersTitle(data) {

    for (var i = 0; i < data.gamePlayer.length; i++) {

        var gpId = data.gamePlayer[i].id;
        var emailUser = data.gamePlayer[i].player.email;
        var gpIdUrl = getParameterByName('gp');

        if (gpId == gpIdUrl) {
            $('#userPlayer').html(emailUser);
        } else {
            $('#userOpponent').html(emailUser);
        }

    }

    if (data.gamePlayer.length === 1) {

        $('#userPlayer').html(emailUser);
        $('#userOpponent').html("WAITING FOR OPPONENT!!").css('color', 'red');
    }

}

function createHitsTable(data) {

    if (data.gamePlayer.length === 2) {

        var printHitsTable = document.getElementById('hitTBody3');

        var hits = data.hitsSink.hits;
        var myHits = data.hitsSink.historial;
        var oppHits = data.hitsSinkOnMe.historial;

        var row = document.createElement('tr');
        var turnCell = document.createElement('td');
        var myHitsCell = document.createElement('td');
        var myLeftCell = document.createElement('td');
        var oppHitsCell = document.createElement('td');
        var oppLeftCell = document.createElement('td');

        turnCell.append(hits.length);
        row.appendChild(turnCell);

        var noSunkNumber = [];

        for (var j=0; j<myHits.length; j++) {

            var myHitsAll = myHits[j];
            var myHitsShipType = myHitsAll.ship;
            var myHitsCounted = myHitsAll.hitsCounted;
            var myHitsSunk = myHitsAll.sunk;

            var space1 = document.createElement('br');
            var space2 = document.createElement('br');

            if (myHitsSunk === false) {

                myHitsCell.append(myHitsShipType, space1, myHitsCounted, space2);
                noSunkNumber.push(myHitsSunk);

            } else {

                myHitsCell.append(myHitsShipType, space1, "Sunk!!", space2);

            }

            row.appendChild(myHitsCell);
        }

        myLeftCell.append(noSunkNumber.length);
        row.appendChild(myLeftCell);

        var noOppSunkNumber = [];

        for (var k=0; k<oppHits.length; k++) {

            var oppHitsAll = oppHits[k];
            var oppHitsShipType = oppHitsAll.ship;
            var oppHitsCounted = oppHitsAll.hitsCounted
            var oppHitsSunk = oppHitsAll.sunk;

            var space3 = document.createElement('br');
            var space4 = document.createElement('br');

            if (oppHitsSunk === false) {

                oppHitsCell.append(oppHitsShipType, space3, oppHitsCounted, space4);
                noOppSunkNumber.push(oppHitsSunk);

            } else {

                oppHitsCell.append(oppHitsShipType, space3, "Sunk!!", space4);
            }

            row.appendChild(oppHitsCell);
        }

        oppLeftCell.append(noOppSunkNumber.length);
        row.appendChild(oppLeftCell);

        printHitsTable.appendChild(row);
    }
}

function winOrLose(data) {

    var printMessage = document.getElementById('winLoose');
    var state = data.state;

    document.getElementById('state').innerText= '';

    if (state == "you win") {

        var winMessage = document.createElement('h2');

        winMessage.setAttribute('class', 'winLose');

        document.getElementById('winLoose').setAttribute('class', 'show');

        winMessage.textContent = 'GAME OVER: CONGRATULATIONS YOU WIN';
        printMessage.appendChild(winMessage);
        document.getElementById('game').setAttribute('class', 'gameOver');
        document.getElementById('hitsTable').setAttribute('class', 'show');


    } else if (state == "you lose"){

        var looseMessage = document.createElement('h2');

        looseMessage.setAttribute('class', 'winLose');

        document.getElementById('winLoose').setAttribute('class', 'show');

        looseMessage.textContent = 'GAME OVER: OOOHHHH!!!! YOU LOSE';
        printMessage.appendChild(looseMessage);
        document.getElementById('game').setAttribute('class', 'gameOver');
        document.getElementById('hitsTable').setAttribute('class', 'show');

    } else if (state == "you tie"){

        var tieMessage = document.createElement('h2');

        tieMessage.setAttribute('class', 'winLose');

        document.getElementById('winLoose').setAttribute('class', 'show');

        tieMessage.textContent = 'GAME OVER: YOU TIE';
        printMessage.appendChild(tieMessage);
        document.getElementById('game').setAttribute('class', 'gameOver');
        document.getElementById('hitsTable').setAttribute('class', 'show');

    } else {

        var currentMessage = document.createElement('h2');

        currentMessage.setAttribute('class', 'winLose');

        currentMessage.textContent = 'GAME IN COURSE';
        printMessage.appendChild(currentMessage);
    }
}







