/*---------------- VUE ------------------------*/
var app = new Vue({
  el: "#app",
  data: {
    grid: {
            "numbers": ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10"],
            "letters": ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"]
    },
    player_1: "",
    player_2: "",
    gameViewerSide: "",
    gameViewerShips: [],
    allShipPositions: [],
    viewerPlayerId: 0,
    viewerSalvoTurn: "",
    viewerGameState:""
  }
});

/*-------------------------------- carga de datos-------------------------------------------*/
$(function() {
    refreshData();
    loadData();
});

/*--------------Carga y actualizacion de datos durante el turno del oponente ------------------*/
var timerId;

function refreshData() {
    timerId = setInterval(function() { loadData(); }, 10000);
}

function stopRefreshing() {
    clearInterval(timerId);
}

/*----------------MAIN (metodos de cuando la pagina carga) - AJAX ---------------*/
function loadData() {
    var gamePlayerId = getQueryVariable("gp");
    console.log("GamePlayerID="+gamePlayerId);
    $.get("/api/game_view/"+gamePlayerId).done(function(gameDTO) {
    showPlayersByGamePlayerId(gamePlayerId, gameDTO);

        $("#audio-theme").html('<source src="css/audio/main_theme.mp3" type="audio/mp3">');

        if (gameDTO.ships.length === 0) {
            stopRefreshing();
            placeNewShips();
        } else {
            var grid = $('#grid').data('gridstack');
            if ( typeof grid != 'undefined' ) {
                grid.removeAll();
                grid.destroy(false);
            }
            app.gameViewerShips = gameDTO.ships;
            getAllShipLocations(app.gameViewerShips);
            placeShipsFromBackEnd();
            if ($("#salvo-col").hasClass("display-none")) {
                $("#salvo-col").removeClass("display-none");
                $("#salvo-col").addClass("display-block");
            }
            getCurrentTurn(gameDTO.salvoes);
            displaySalvoes(gamePlayerId, gameDTO);
        }

        if (app.viewerGameState === "WIN" || app.viewerGameState === "LOSE" || app.viewerGameState === "DRAW") {
            app.viewerSalvoTurn="GAME ENDED: YOU "+app.viewerGameState+"!"
            $("#fire-salvo-btn").hide();
            stopRefreshing();
        }
    })
    .fail(function () {
        console.log("Failed to get game view data... ");
    });
}

/*----------------Mostrar los nombres de los jugadores en las pantallas ---------------*/
function showPlayersByGamePlayerId(id, obj) {

    obj.gamePlayers.map(function (gamePlayer) {
        if (id == gamePlayer.gpid) {
            app.player_1 = gamePlayer.email + " (you)";
            app.viewerPlayerId = gamePlayer.id;
            app.viewerGameState = gamePlayer.gameState;
        } else if (id != gamePlayer.gpid) {
            app.player_2 = " vs " + gamePlayer.email;
        }
    });
}

/*----------------funcion para desloguearse---------------*/
$("#logout").click(function() {
    $.post("/api/logout")
    .done(function() {
        window.location.replace("/web/games.html");
    })
    .fail(function () {
        console.log("Failed to logout... ");
    })
});

/*-----------  getQuery toma de parametros ----------*/
function getQueryVariable(variable) {
   var query = window.location.search.substring(1);
   var vars = query.split("&");
   for (var i=0;i<vars.length;i++) {
       var pair = vars[i].split("=");
       if(pair[0] == variable){
           return pair[1];
       }
   }
   return(false);
}

/*------------ metodos del display salvo -----------*/
function displaySalvoes(gamePlayerId, gameDTO) {

   for (var i=0;i<gameDTO.gamePlayers.length;i++){

       if (gameDTO.gamePlayers[i].gpid == gamePlayerId) {
           var thisPlayerId = gameDTO.gamePlayers[i].id;
           gameDTO.salvoes.map(function (salvo) {
               if (salvo.player == thisPlayerId) {
                   var myTurn = salvo.turn;
                   for (var e=0;e<salvo.locations.length;e++){
                       var letterP1 = salvo.locations[e].substring(0, 1);
                       var numberP1 = salvo.locations[e].substring(1, 3);
                       $("#salvo-body>."+letterP1+" td:eq("+numberP1+")").addClass("bg-salvo").html(myTurn);
                   }
               } else if (salvo.player != thisPlayerId) {
                   var yourTurn = salvo.turn;
                   for (var h=0;h<salvo.locations.length;h++){
                       var letter = salvo.locations[h].substring(0, 1);
                       var number = salvo.locations[h].substring(1, 3);
                       if ($("#grid-body>."+letter+" td:eq("+number+")").hasClass("bg-ship")) {
                           $("#grid-body>."+letter+" td:eq("+number+")").addClass("bg-salvo").html(yourTurn);
                       }
                   }
               }
           });
       }
   }
}


/*------------------ postear ships - AJAX -----------------*/
function postShips(shipTypeAndCells) {
    var gamePlayerId = getQueryVariable("gp");
    $.post({
      url: "/api/games/players/"+gamePlayerId+"/ships",
      data: JSON.stringify(shipTypeAndCells),
      dataType: "text",
      contentType: "application/json"
    })
    .done(function (response) {
      refreshData();
      loadData();
      console.log( "Ships added: " + response );
    })
    .fail(function () {
      console.log("Failed to add ships... ");
    })
}

/*-------------------------- ON CLICK BATTLE - POST NUEVOS BARCOS ---------------------------*/
$("#placed-ships-btn").click(function(){
    var shipTypeAndCells = [];

    for (var i=1; i<=5; i++) {
        var ship = new Object();
        var cellsArray = [];

        var h = parseInt($("#grid .grid-stack-item:nth-child("+i+")").attr("data-gs-height"));
        var w = parseInt($("#grid .grid-stack-item:nth-child("+i+")").attr("data-gs-width"));
        var posX = parseInt($("#grid .grid-stack-item:nth-child("+i+")").attr("data-gs-x"));
        var posY = parseInt($("#grid .grid-stack-item:nth-child("+i+")").attr("data-gs-y"))+64;

        if (w>h) {
            for (var e=1; e<=w; e++) {
                var HHH = String.fromCharCode(posY+1)+(posX+e);
                cellsArray.push(HHH);
                ship.type = $("#grid .grid-stack-item:nth-child("+i+")").children().attr("alt");
                ship.shipLocations = cellsArray;
            }
        } else if (h>w) {
            for (var d=1; d<=h; d++) {
                var VVV = String.fromCharCode(posY+d)+(posX+1);
                cellsArray.push(VVV);
                ship.type = $("#grid .grid-stack-item:nth-child("+i+")").children().attr("alt");
                ship.shipLocations = cellsArray;
            }
        }
        console.log(ship.type,ship.shipLocations);
        shipTypeAndCells.push(ship);
    }
    postShips(shipTypeAndCells);
})

/*------------------------------- ROTAR BARCOS ---------------------------------*/
function setListener(grid) {
    $(".grid-stack-item").dblclick(function() {
        var h = parseInt($(this).attr("data-gs-height"));
        var w = parseInt($(this).attr("data-gs-width"));
        var posX = parseInt($(this).attr("data-gs-x"));
        var posY = parseInt($(this).attr("data-gs-y"));

        // mecanicas de rotacion...
        if (w>h) {
            if ( grid.isAreaEmpty(posX, posY+1, h, w-1) && posX+h<=10 && posY+w<=10 ) {
                grid.update($(this), posX, posY, h, w);
            } else if ( grid.isAreaEmpty(posX, posY-w+1, h, w-1) && posX+h<=10 && posY-w+1>=0 ) {
                grid.update($(this), posX, posY-w+1, h, w);
            } else {
                searchSpaceAndRotate($(this));
            }
        } else if (h>w) {
            if ( grid.isAreaEmpty(posX+1, posY, h-1, w) && posX+h<=10 ) {
                grid.update($(this), posX, posY, h, w);
            } else if ( grid.isAreaEmpty(posX+1, posY+1, h-1, w) && posX+h<=10 ) {
                grid.update($(this), posX, posY+1, h, w);
            } else if ( grid.isAreaEmpty(posX+1, posY+2, h-1, w) && posX+h<=10 ) {
                grid.update($(this), posX, posY+2, h, w);
            } else if ( grid.isAreaEmpty(posX, posY-1, h, w) && posX+h<=10 && posY>0) {
                grid.update($(this), posX, posY-1, h, w);
            } else if ( grid.isAreaEmpty(posX, posY-2, h, w) && posX+h<=10 && posY>1) {
                grid.update($(this), posX, posY-2, h, w);
            } else {
                searchSpaceAndRotate($(this));
            }
        }
        // Cuando no haya espacio ir a la primera parte del grid donde haya...
        function searchSpaceAndRotate(widget) {
            for (var j=0; j<10; j++) {
                var found = false;
                for (var i=0; i<10; i++) {
                    if ( grid.isAreaEmpty(i, j, h, w) && i+h<=10 && j+w<=10 ) {
                        grid.update(widget, i, j, h, w);
                        found = true;
                        break;
                    }
                }
                if (found===true){break;}
            }
        }
        // Rotacion visual de los barcos...
        var shipImgId = $(this).children().attr("id");
        switch (shipImgId) {
            case "submarineV-img-v":
                $(this).children().attr("id", "submarineH-img-h").attr("src", "css/images/icons/submarineH.png");
                break;
            case "submarineH-img-h":
                $(this).children().attr("id", "submarineV-img-v").attr("src", "css/images/icons/submarineV.png");
                break;
            case "battleshipH-img-h":
                $(this).children().attr("id", "battleshipV-img-v").attr("src", "css/images/icons/battleshipV.png");
                break;
            case "battleshipV-img-v":
                $(this).children().attr("id", "battleshipH-img-h").attr("src", "css/images/icons/battleshipH.png");
                break;
            case "destroyerV-img-v":
                $(this).children().attr("id", "destroyerH-img-h").attr("src", "css/images/icons/destroyerH.png");
                break;
            case "destroyerH-img-h":
                $(this).children().attr("id", "destroyerV-img-v").attr("src", "css/images/icons/destroyerV.png");
                break;
            case "carrierH-img-h":
                $(this).children().attr("id", "carrierV-img-v").attr("src", "css/images/icons/carrierV.png");
                break;
            case "carrierV-img-v":
                $(this).children().attr("id", "carrierH-img-h").attr("src", "css/images/icons/carrierH.png");
                break;
            case "patrol_boatV-img-v":
                $(this).children().attr("id", "patrol_boatH-img-h").attr("src", "css/images/icons/patrol_boatH.png");
                break;
            case "patrol_boatH-img-h":
                $(this).children().attr("id", "patrol_boatV-img-v").attr("src", "css/images/icons/patrol_boatV.png");
                break;
            default:
                $(this).children().attr("id", "default-img").attr("src", "css/images/batallanaval.png");
        }
    })
}

/*--------------------------- Colocando barcos - GRIDSTACK ----------------------------*/
function placeNewShips() {
    $("#place-ships-card").show();

    var options = {
        //grilla de 10 x 10
        width: 10,
        height: 10,
        //separacion entre elementos (les llaman widgets)
        verticalMargin: 0,
        //altura de las celdas
        cellHeight: 35,
        cellWidth: 35,
        //desabilitando el resize de los widgets
        disableResize: true,
        //widgets flotantes
        float: true,
        //removeTimeout: 100,
        //permite que el widget ocupe mas de una columna
        disableOneColumnMode: true,
        //false permite mover, true impide
        staticGrid: false,
        //activa animaciones (cuando se suelta el elemento se ve más suave la caida)
        animate: true
    }
    //se inicializa el grid con las opciones
    $('.grid-stack').gridstack(options);
    var grid = $('#grid').data('gridstack');

    grid.addWidget($('<div><img id="submarineV-img-v" class="grid-stack-item-content" src="css/images/icons/submarineV.png" alt="submarine"></div>'),
    1, 0, 1, 5, false);
    grid.addWidget($('<div><img id="battleshipV-img-v" class="grid-stack-item-content" src="css/images/icons/battleshipV.png" alt="battleship"></div>'),
    8, 2, 1, 4, false);
    grid.addWidget($('<div><img id="destroyerV-img-v" class="grid-stack-item-content" src="css/images/icons/destroyerV.png" alt="destroyer"></div>'),
    4, 2, 1, 3, false);
    grid.addWidget($('<div><img id="carrierH-img-h" class="grid-stack-item-content" src="css/images/icons/carrierH.png" alt="carrier"></div>'),
    1, 7, 3, 1, false);
    grid.addWidget($('<div><img id="patrol_boatV-img-v" class="grid-stack-item-content" src="css/images/icons/patrol_boatV.png" alt="patrol_boat"></div>'),
    6, 7, 1, 2, false);

    setListener(grid);
}


/*-------------------Carga de los barcos desde el backend - GRIDSTACK --------------------*/
function placeShipsFromBackEnd() {
    $("#place-ships-card").hide();

    var options = {
        width: 10,
        height: 10,
        verticalMargin: 0,
        cellHeight: 35,
        cellWidth: 35,
        disableResize: true,
        float: true,
        disableOneColumnMode: true,
        staticGrid: true,
        animate: true
    }
    $('.grid-stack').gridstack(options);
    var grid = $('#grid').data('gridstack');

    app.gameViewerShips.map(function(ship) {
        var searchChar = ship.locations[0].slice(0, 1);
        var secondChar = ship.locations[1].slice(0, 1);
        if ( searchChar === secondChar ) {
            //sí es la misma letra es porque es horizontal
            ship.position = "Horizontal";
        } else {
            ship.position = "Vertical";
        }

        for (var i=0; i < ship.locations.length; i++) {
            ship.locations[i] = ship.locations[i].replace(/A/g, '0');
            ship.locations[i] = ship.locations[i].replace(/B/g, '1');
            ship.locations[i] = ship.locations[i].replace(/C/g, '2');
            ship.locations[i] = ship.locations[i].replace(/D/g, '3');
            ship.locations[i] = ship.locations[i].replace(/E/g, '4');
            ship.locations[i] = ship.locations[i].replace(/F/g, '5');
            ship.locations[i] = ship.locations[i].replace(/G/g, '6');
            ship.locations[i] = ship.locations[i].replace(/H/g, '7');
            ship.locations[i] = ship.locations[i].replace(/I/g, '8');
            ship.locations[i] = ship.locations[i].replace(/J/g, '9');
        }

        var yInGrid = parseInt(ship.locations[0].slice(0, 1));
        var xInGrid = parseInt(ship.locations[0].slice(1, 3)) - 1;

        if (ship.type.toLowerCase() === "submarine") {
            if (ship.position === "Horizontal") {
                grid.addWidget($('<div><img id="submarineH-img-h" class="grid-stack-item-content" src="css/images/icons/submarineH.png" alt="submarine"></div>'),
                xInGrid, yInGrid, 5, 1, false);
            } else if (ship.position === "Vertical") {
                grid.addWidget($('<div><img id="submarineV-img-v" class="grid-stack-item-content" src="css/images/icons/submarineV.png" alt="submarine"></div>'),
                xInGrid, yInGrid, 1, 5, false);
            }
        } else if (ship.type.toLowerCase() === "battleship") {
            if (ship.position === "Horizontal") {
                grid.addWidget($('<div><img id="battleshipH-img-h" class="grid-stack-item-content" src="css/images/icons/battleshipH.png" alt="battleship"></div>'),
                xInGrid, yInGrid, 4, 1, false);
            } else if (ship.position === "Vertical") {
                grid.addWidget($('<div><img id="battleshipV-img-v" class="grid-stack-item-content" src="css/images/icons/battleshipV.png" alt="battleship"></div>'),
                xInGrid, yInGrid, 1, 4, false);
            }
        } else if (ship.type.toLowerCase() === "destroyer") {
            if (ship.position === "Horizontal") {
                grid.addWidget($('<div><img id="destroyerH-img-h" class="grid-stack-item-content" src="css/images/icons/destroyerH.png" alt="destroyer"></div>'),
                xInGrid, yInGrid, 3, 1, false);
            } else if (ship.position === "Vertical") {
                grid.addWidget($('<div><img id="destroyerV-img-v" class="grid-stack-item-content" src="css/images/icons/destroyerV.png" alt="destroyer"></div>'),
                xInGrid, yInGrid, 1, 3, false);
            }
        } else if (ship.type.toLowerCase() === "carrier") {
            if (ship.position === "Horizontal") {
                grid.addWidget($('<div><img id="carrierH-img-h" class="grid-stack-item-content" src="css/images/icons/carrierH.png" alt="carrier"></div>'),
                xInGrid, yInGrid, 3, 1, false);
            } else if (ship.position === "Vertical") {
                grid.addWidget($('<div><img id="carrierV-img-v" class="grid-stack-item-content" src="css/images/icons/carrierV.png" alt="carrier"></div>'),
                xInGrid, yInGrid, 1, 3, false);
            }

        } else if (ship.type.toLowerCase() === "patrol_boat") {
              if (ship.position === "Horizontal") {
                  grid.addWidget($('<div><img id="patrol_boatH-img-h" class="grid-stack-item-content" src="css/images/icons/patrol_boatH.png" alt="patrol_boat"></div>'),
                  xInGrid, yInGrid, 2, 1, false);
              } else if (ship.position === "Vertical") {
                  grid.addWidget($('<div><img id="patrol_boatV-img-v" class="grid-stack-item-content" src="css/images/icons/patrol_boatV.png" alt="patrol_boat"></div>'),
                  xInGrid, yInGrid, 1, 2, false);
              }
        }
    })
}

/*----------- get current turn ---------------------*/
function getCurrentTurn(arrayOfSalvos) {
    $("#fire-card").removeClass("display-none").addClass("display-block");
    var allTurnsFromViewer = [];
    var allTurnsFromOpponent = [];
    arrayOfSalvos.map(function(salvo) {
        if (salvo.player === app.viewerPlayerId) {
            allTurnsFromViewer.push(parseInt(salvo.turn));
        }else{
            allTurnsFromOpponent.push(parseInt(salvo.turn));
        }
    })
    var currentTurn = function(){if(allTurnsFromViewer.length===0){
                                    return 1;
                                 }else{
                                    return Math.max(...allTurnsFromViewer)+1;
                                 }
                      };
    $("#turn-number").html(currentTurn);
    app.viewerSalvoTurn=app.viewerGameState;
}




/*------------------------- postear salvos - AJAX -------------------------*/
function postSalvos(salvoJSON) {
    var gamePlayerId = getQueryVariable("gp");
    console.log("Mi salvoJSON: ",salvoJSON);
    $.post({
        url: "/api/games/players/"+gamePlayerId+"/salvos",
        data: JSON.stringify(salvoJSON),
        dataType: "text",
        contentType: "application/json"
    })
    .done(function (response) {
        if ($("#salvo-action").hasClass("game-play-alert")) {
            $("#salvo-action").removeClass("game-play-alert");
        }
        refreshData();
        loadData();
        console.log( "Salvo added: " + response );

    })
    .fail(function (jqXHR, textStatus, error) {
        console.log("Failed to add salvo... " + jqXHR.responseText);
        app.viewerSalvoTurn=jqXHR.responseText;
    })
}


    /*---------------------- mira de los salvos -------------------------*/
    $("#salvo-body > tr > td").click(function() {
        if ( $(this).hasClass("bg-salvo") ) {
            return;
        } else if ( $(this).children().length > 0 ) {
            $(this).html("");
        } else if ( $(".aim-img").length < 5 ) {
            var letter = $(this).parent().attr("class");
            var number = $(this).attr("class");
            var cell = letter+number;

            $(this).html("<img data-cell='"+cell+"'class='aim-img' src='css/images/aim.png'>");
        }
    })
    /*------------ click de fuego -----------------*/
    $("#salvo-col").on("click", "#fire-salvo-btn", function(){

        if ( $(".aim-img").length != 0 && $(".aim-img").length <= 5 ) {
            playFireSound();
            var salvoJSON = {};
            var turn = $("#turn-number").text();
            var locations = [];
            $(".aim-img").each(function() {
               locations.push($(this).data("cell"));
            })
            salvoJSON.turn = turn;
            salvoJSON.locations = locations;
            postSalvos(salvoJSON);

        } else {
            if (!$("#salvo-action").hasClass("game-play-alert")) {
                $("#salvo-action").addClass("game-play-alert");
            }
        }
    })


/*--------------------- pantalla de salvos -------------------------*/
function displaySalvoes(gamePlayerId, gameDTO) {
    console.log("Ingrese a display salvoes");
   for (var i=0;i<gameDTO.gamePlayers.length;i++){

       if (gameDTO.gamePlayers[i].gpid == gamePlayerId) {
           var thisPlayerId = gameDTO.gamePlayers[i].id;
           gameDTO.salvoes.map(function (salvo) {

               if (salvo.player == thisPlayerId) {
               /*---------------- fallos y aciertos-------------------------*/
                console.log("los salvos de este jugador:",salvo.locations);
                   //var myTurn = salvo.turn;
                   for (var e=0;e<salvo.locations.length;e++){
                       var letterP1 = salvo.locations[e].substring(0, 1);
                       var numberP1 = salvo.locations[e].substring(1, 3);

                       if (salvo.hits.indexOf(salvo.locations[e]) != -1) {
                            $("#salvo-body>."+letterP1+" td:eq("+numberP1+")").html('<img class="spark-salvo" src="css/images/spark.png">');
                       } else {
                            $("#salvo-body>."+letterP1+" td:eq("+numberP1+")").html('<img class="spark-salvo" src="css/images/cross.png">');
                       }
                   }
                   /*----------------- hundimientos --------------------------------*/
                   for (var ss=0;ss<salvo.sinks.length;ss++) {

                        for (var s=0;s<salvo.sinks[ss].locations.length;s++) {

                             var sinkLetter = salvo.sinks[ss].locations[s].substring(0, 1);
                             var sinkNumber = salvo.sinks[ss].locations[s].substring(1, 3);
                             var sinkCell = $("#salvo-body>."+sinkLetter+" td:eq("+sinkNumber+")");

                             if (!sinkCell.hasClass("bg-salvo")) {
                                 sinkCell.addClass("bg-salvo");
                             }
                        }

                        switch (salvo.sinks[ss].type) {
                            case "submarine":
                                $("#submarineV-img-v2").attr("src", "css/images/icons/submarineV.png");
                                break;
                            case "battleship":
                                $("#battleshipV-img-v2").attr("src", "css/images/icons/battleshipV.png");
                                break;
                            case "destroyer":
                                $("#destroyerV-img-v2").attr("src", "css/images/icons/destroyerV.png");
                                break;
                            case "carrier":
                                $("#carrierV-img-h2").attr("src", "css/images/icons/carrierV.png");
                                break;
                            case "patrol_boat":
                                $("#patrol_boatV-img-v2").attr("src", "css/images/icons/patrol_boatV.png");
                                break;
                            default:
                                break;
                            }
                   }

               } else if (salvo.player != thisPlayerId) {
                 /*------------------- fallos y aciertos ---------------------------*/
                   for (var h=0;h<salvo.locations.length;h++){
                       var letter = salvo.locations[h].substring(0, 1);
                       var number = salvo.locations[h].substring(1, 3)-1;

                       switch(letter) {
                            case "A":letter = 0;break;
                            case "B":letter = 1;break;
                            case "C":letter = 2;break;
                            case "D":letter = 3;break;
                            case "E":letter = 4;break;
                            case "F":letter = 5;break;
                            case "G":letter = 6;break;
                            case "H":letter = 7;break;
                            case "I":letter = 8;break;
                            case "J":letter = 9;break;
                            default:letter = 0;break;
                       }

                       if ( app.allShipPositions.indexOf(salvo.locations[h]) != -1 ) {
                           $('#grid').append('<div style="position:absolute; top:'+letter*35+'px; left:'+number*35+'px;"><img class="spark" src="css/images/spark.png"></div>');
                       } else {
                           $('#grid').append('<div style="position:absolute; top:'+letter*35+'px; left:'+number*35+'px;"><img class="spark" src="css/images/cross.png"></div>');
                       }

                   }
               }
           });
       }
   }
}

/*------------------- audio del disparo ---------------------*/
function playFireSound() {
   var fireAudio = document.getElementById("fire-audio");
   fireAudio.play();
}

/*--------------- obtener las localizaciones de los barcos para comparar con los salvos ------------------------*/
function getAllShipLocations(set) {
    set.map(function(ship) {
        for (var i=0; i<ship.locations.length; i++){
            app.allShipPositions.push(ship.locations[i]);
        }
    });
    console.log(app.allShipPositions);
    return;
}
