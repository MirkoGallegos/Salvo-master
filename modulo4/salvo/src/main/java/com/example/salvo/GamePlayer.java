package com.example.salvo;

/*
clase que sirve como conexión entre player y game.
 */

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import org.hibernate.annotations.GenericGenerator;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.*;

@Entity
public class GamePlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private LocalDateTime joinDate;

    /*
    relación Many to One con Player.
    nota:muchos gameplayer de un player || un player puede ser muchos gamePlayers.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="player_id")
    private Player player;

    /*relación Many to One con Game.
    nota: varios gameplayer dentro de un solo game || un juego va a tener varios gameplayer*/
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="game_id")
    private Game game;

    /*
    relacion one to many
    nota: un gameplayer va a tener varios ships || van a haber varios ships en un gameplayer
     */
    @OneToMany(mappedBy="gamePlayer", fetch=FetchType.EAGER, cascade= CascadeType.ALL)
    private Set<Ship> ships = new HashSet<>();

    /*
    relacion one to many
    nota: un gameplayer va a tener varios salvos || van a haber varios salvos de parte de un gameplayer
     */
    @OneToMany(mappedBy="gamePlayer", fetch=FetchType.EAGER, cascade= CascadeType.ALL)
    private Set<Salvo> Salvoes = new HashSet<>();

    /*===================       CONSTRUCTORES       =====================*/

    //Metodo constructor vacio
    public GamePlayer() { }

    public GamePlayer(Game game,Player player,LocalDateTime joinDate) {
        this.game = game;
        this.player = player;
        this.joinDate = joinDate;
    }

    /*===================     MÉTODOS     =====================*/
    //getters & setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDateTime joinDate) {
        this.joinDate = joinDate;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    //Metodo para añadir los ships (avisa que da)
    public void addShip(Ship ship){
        this.ships.add(ship);
        ship.setGamePlayer(this);
    }

    public Set<Ship> getShips(){
        return this.ships;
    }

    //añade los tiros por turno
    public void addSalvo(Salvo salvo){
        this.Salvoes.add(salvo);
        salvo.setGamePlayer(this);
    }

    public Set<Salvo> getSalvoes(){
        return this.getSalvoes();
    }


    //DTO (data transfer object) para administrar la info del GamePlayer
    public Map<String, Object> gamePlayerDTO(){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", this.getId());
        dto.put("player", this.getPlayer().playerDTO());

        Score score =  this.getPlayer().getScoreByGame(this.getGame());
        if(score != null)
            dto.put("score",score.getFinishDate()); //Leer README para mas info del codigo
        else
            dto.put("score", null);

        return dto;
    }



}

