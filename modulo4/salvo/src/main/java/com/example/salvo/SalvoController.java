package com.example.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import java.util.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SalvoController {
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    /*
    al momento de probara este el nombre de la clase a poner despues del rest en el localhost:8080
    ej de GAME sería: localhost:8080/rest/games
    lo mismo aplica para el resto
     */

    @RequestMapping("/games")
    public List<Map<String, Object>> getGames(){
        return gameRepository.findAll().stream().map(Game::gameDTO).collect(Collectors.toList());
    }

    @RequestMapping("/game_view/{gamePlayerId}")
    public Map<String,Object> getGameView(@PathVariable long gamePlayerId){
        return this.gameViewDTO(gamePlayerRepository.findById(gamePlayerId).orElse(null));
    }

    private Map<String,Object> gameViewDTO(GamePlayer gamePlayer){
        Map<String,Object> dto = new LinkedHashMap<>();

        if(gamePlayer != null){
            dto.put("id", gamePlayer.getGame().getId());
            dto.put("creationDate", gamePlayer.getGame().getCreationDate());

            dto.put("gamePlayer", gamePlayer.getGame().getGamePlayers().stream().map(GamePlayer::gamePlayerDTO));
            dto.put("player", gamePlayer.getPlayer().getUserName());
            dto.put("ships", gamePlayer.getShips().stream().map(Ship::shipDTO));
            dto.put("salvoes", gamePlayer.getGame().getGamePlayers()
                    .stream().flatMap(gp -> gp.getSalvoes() //flatMap es un recorrido que no recorre como el map
                    .stream().map(salvo -> salvo.salvoDto()))
            );

        }else{
            dto.put("error", "no such game");
        }

        return dto;

    }

}

