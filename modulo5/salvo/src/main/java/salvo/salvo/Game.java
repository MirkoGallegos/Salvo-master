package salvo.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


@Entity
public class Game {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long gameId;
    private Date creationDate;

    @JsonIgnore
    @OneToMany(mappedBy="game", fetch=FetchType.EAGER)
    Set<GamePlayer> GamePlayers;

    @OneToMany(mappedBy="game", fetch=FetchType.EAGER)
    Set<Score> scores = new HashSet<>();

    public Game() {}

    public Game(Date date) {
        this.creationDate = date;
    }

    public long getId() {
        return gameId;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date date) {
        this.creationDate = date;
    }

    public Set<GamePlayer> getGamePlayers() {
        return GamePlayers;
    }

    public void addScores(Score score) {
        score.setGame(this);
        scores.add(score);
    }

    public Set<Score> getScores() {
        return scores;
    }

    public void setScores(Set<Score> scores) {
        this.scores = scores;
    }

    //    public void addGamePlayer(GamePlayer gamePlayer) {
//        gamePlayer.setGame(this);
//        GamePlayers.add(gamePlayer);
//    }

//    public String toString() {
//        return "" + creationDate ;
//    }

}
