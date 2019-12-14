package com.example.salvo;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import org.hibernate.annotations.GenericGenerator;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.*;

@Entity
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;

    private LocalDateTime finishDate;

    private float points;

/*
varios puntajes (por cada partida que haya jugado) un jugador
 */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="player_id")
    private Player player;
/*
varios puntajes un juego
 */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="game_id")
    private Game game;

    /*===================       CONSTRUCTORES       =====================*/

    public Score(){ }

    public  Score (float points, Game game, Player player, LocalDateTime finishDate){
        this.points = points;
        this.game = game;
        this.player = player;
        this.finishDate = finishDate;
    }

    /*===================     MÉTODOS     =====================*/
    //getters & setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(LocalDateTime finishDate) {
        this.finishDate = finishDate;
    }

    public float getPoints() {
        return points;
    }

    public void setPoints(float  points) {
        this.points = points;
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
}

