package salvo.salvo;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long playerId;
    private String userName;
    private String password;

    @OneToMany(mappedBy="player", fetch=FetchType.EAGER)
    Set<GamePlayer> GamePlayers;

    @OneToMany(mappedBy="player", fetch=FetchType.EAGER)
    Set<Score> scores = new HashSet<>();


    public Player(){}

    public Player(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public long getId() {
        return playerId;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    //    public void addGamePlayer(GamePlayer gamePlayer) {
//        gamePlayer.setUserName(this);
//        gamePlayers.add(gamePlayer);
//    }

    public Set<Score> getScores() {
        return scores;
    }

    public void addScore(Score score) {
        score.setPlayer(this);
        scores.add(score);
    }

    public Score getScore(Game game) {

        return scores.stream()
                        .filter(score -> score.getGame().getId() == game.getId())
                        .findFirst()
                        .orElse(null);

    }

    public String toString() {
        return playerId + " " + "refers to " + userName;
    }
}
