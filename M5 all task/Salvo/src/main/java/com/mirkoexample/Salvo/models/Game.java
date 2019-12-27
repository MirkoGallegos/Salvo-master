package com.mirkoexample.Salvo.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id_game;

    private Date creation_date;

    /*
        one to many un juego con varios gameplayer
     */
    @OneToMany(mappedBy="game", fetch=FetchType.EAGER)
    Set<GamePlayer> game_players;

    /*
    uan tu meni (? porque un juego varios scores
     */
    @OneToMany(mappedBy="game", fetch=FetchType.EAGER)
    Set<Score> scores;

    //constructor generando un creation date
    public Game() {
        this.creation_date = new Date();
    }

    //getter y setters (los necesarios)
    public long getId_game() {
        return id_game;
    }

    public Date getCreation_date() {
        return creation_date;
    }

    public void setCreation_date(Date creation_date) {
        this.creation_date = creation_date;
    }

    public Set<GamePlayer> getGame_players() {
        return game_players;
    }

    public Set<Score> getScores() {
        return scores;
    }

    public void setGame_players(Set<GamePlayer> game_players) {
        this.game_players = game_players;
    }

    public void setScores(Set<Score> scores) {
        this.scores = scores;
    }

    //añade gameplayer al game
    public void addGamePlayer(GamePlayer gameplayer) {
        gameplayer.setGame(this);
        game_players.add(gameplayer);
    }

    /*
    obtenemos la fecha del final de partida y sí es que la tiene
    en caso de no tenerlo traer el .orElse (osea en este caso trae "null")
     */
    public boolean getEndDate(Set<GamePlayer> gamePlayers) {

        GamePlayer gamePlayer = gamePlayers.stream().findFirst().orElse(null);
        if ( (null != gamePlayer) && (gamePlayer.getGameState() == GamePlayerState.WIN || gamePlayer.getGameState() == GamePlayerState.LOSE || gamePlayer.getGameState() == GamePlayerState.DRAW)) {
            return true;
        } else {
            return false;
        }
    }

}

