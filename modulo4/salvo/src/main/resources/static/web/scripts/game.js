    $.getJSON("/api/games").done(function recurse(data) {
        if(data[0].player != null){
            document.getElementById("user-logged-in-game-view").innerText = data[0].player.name;
        } else {
            $("#logout-form-game-view").toggle();
            location.href="/web/games.html";
        }
    });

    $("#logout-form-game-view").click(function(){
            $.post("/api/logout")
            .done( function() {
                console.log("logged out");
                location.reload();
            }).fail( function(){
                console.log("There's something wrong about this. Try again.");
                alert("A frog has jumped out into our servers. It's a mess. Try again in a few minutes");
            });
        });

function getParameterByName(name, url) {
    if (!url) url = window.location.href;
    name = name.replace(/[\[\]]/g, '\\$&');
    var regex = new RegExp('[?&]' + name + '(=([^&#]*)|&|#|$)'),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, ' '));
}

// var prueba = document.getElementById("prueba");

// Construyo la URL que enviaré por parámetro a la llamada de AJAX
function makeUrl() {
  var gamePlayer_Id = getParameterByName('gp');
  return '/api/game_view/' + gamePlayer_Id;
}
//  console.log(makeUrl());

// Función AJAX para traer el JSON de cada gamePlayer
function ajaxCallURL() {
    var ajxCll = $.ajax({
    url: makeUrl(),
    type: 'GET'
    });
    return ajxCll;
}

ajaxCallURL().done(function(data) {

    var playersTag = document.getElementById("players"); // Traigo la etiqueta H3 del HTML
    var versus = ""; // Inicializo la variable para los Strings que luego volcaré dentro de la etiqueta anterior
    var gpViewId = getParameterByName('gp'); // Obtengo el parámetro que viaja en la variable gp de mi URL
//    console.log("listo");
//    console.log(data[0].ships);
    var salvoes4Player = data[0].salvoes;
// Itero los barcos del gamePlayer
    var ships4Player = data[0].ships;
    for(e in ships4Player){
//        console.log(ships4Player[e].locations);
// Itero las ubicaciones de cada barco
        var shLoc4Play = ships4Player[e].locations;
// Por cada String de ubicación del barco, busco la celda en la grilla y la pinto de un color
        for(i in shLoc4Play){
//            console.log(shLoc4Play[i]);
            var cellId = document.getElementById(shLoc4Play[i]);
            cellId.style.backgroundColor = "#660000";
// Itero los salvoes de cada jugador
            for(c in salvoes4Player){
                    if(gpViewId == salvoes4Player[c].player){  // si el id de la url es igual al id del gamePlayer...
                        for(d in salvoes4Player[c].locations){ // (itero las ubicaciones de los salvoes)
                        // ...obtengo las celdas de la segunda tabla, las pinto y les escribo "SHOT"
                            var cellSId = document.getElementById("S" + salvoes4Player[c].locations[d]);
                            cellSId.innerText = "SHOT";
                            cellSId.style.color = "#FFF";
                            cellSId.style.backgroundColor = "#000";
                        }
                    } else {  // si no es el mismo id (es el del otro gamePlayer)...
                        for(m in salvoes4Player[c].locations){ // (itero las ubicaciones de los salvoes de ese gamePlayer...
                            if(salvoes4Player[c].locations[m].indexOf(shLoc4Play[i]) != -1){ // y si esa ubicación aparece entre los barcos del otro gamePlayer...
                                // escribo en la celda correspondiente una "X" y el número de turno en que sucedió
                                cellId.innerText = "X" + "-T" + salvoes4Player[c].turn;
                                cellId.style.color = "#FFF";
                            }
                        }
                    }
                }
        }
    }
    var gpViewId = getParameterByName('gp');
//    console.log(gpViewId);
// Doble iteración para mostrar participantes
// Por un problema de ordenamiento del Array,
// no podía mostrar siempre en primer lugar al gp que está viendo el juego. Esta fue la solución:
    for(k in data[0].gamePlayers){
        var gppk = data[0].gamePlayers[k];
        if(data[0].gamePlayers[k].id == gpViewId){
            versus += gppk.player.email + " (you) vs ";
//            console.log("right");
        }
    }
    for(l in data[0].gamePlayers){
        var gppl = data[0].gamePlayers[l];
        if(data[0].gamePlayers[l].id != gpViewId){
            versus += gppl.player.email;
         }
        // versus += " vs ";
    }
    playersTag.innerHTML = versus; // los Strings guardados en versus se convierten en HTML de la etiqueta H3 de game.html
    getElementByClassName("cell").style.minWidth = "66px";
});