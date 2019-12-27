package com.mirkoexample.Salvo.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator="native")
    @GenericGenerator(name="native", strategy = "native")
    private long id;

    /*
    mas de un score para un game
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gameID")
    private Game game;

    /*
    varios score para player
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="playerID")
    private Player player;

    private Double score;

    private Date finishDate;

    public Score() {
    }

    public Score(Game game, Player player, Double score, Date finishDate) {
        this.game = game;
        this.player = player;
        this.score = score;
        this.finishDate = finishDate;
    }
     /*
     metodos
      */
    //Getters y Setters
    public long getId() {
        return id;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }
}
