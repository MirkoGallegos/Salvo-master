package salvo.salvo;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Salvo {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;
    private int turn;

    @ManyToOne
    @JoinColumn(name="gamePlayerId")
    private GamePlayer gamePlayer;

    @ElementCollection
    @Column(name="salvoLocation")
    private List<String> salvoLocation = new ArrayList<>();

    public Salvo(){}

    public Salvo(int turn, List<String> salvoLocation) {
        this.turn = turn;
        this.salvoLocation = salvoLocation;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public List<String> getSalvoLocation() {
        return salvoLocation;
    }

    public void setSalvoLocation(List<String> salvoLocation) {
        this.salvoLocation = salvoLocation;
    }


}
