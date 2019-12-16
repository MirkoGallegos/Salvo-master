package salvo.salvo;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Score {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private Double points;
    private Date finishDate;

    @ManyToOne
    @JoinColumn(name="playerId")
    private Player player;

    @ManyToOne
    @JoinColumn(name="gameId")
    private Game game;


    public Score() {}

    public Score(Double points, Date finishDate) {
        this.points = points;
        this.finishDate = finishDate;
    }

    public Double getPoints() {
        return points;
    }

    public void setPoints(Double points) {
        this.points = points;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
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
