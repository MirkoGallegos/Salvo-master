package salvo.salvo;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Entity
public class Ship {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;
    private String shipType;

    @ManyToOne
    @JoinColumn(name="gamePlayerId")
    private GamePlayer gamePlayer;

    @ElementCollection
    @Column(name="shipLocations")
    private List<String> shipLoc = new ArrayList<>();

    public Ship(){}

    public Ship (String shipType, List<String> shipLoc) {

        this.shipType = shipType;
        this.shipLoc = shipLoc;
    }

    public long getId() {
        return id;
    }

    public String getShipType() {
        return shipType;
    }

    public void setShipType(String shipType) {
        this.shipType = shipType;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public List<String> getShipLoc() {
        return shipLoc;
    }

    public void setShipLoc(List<String> shipLoc) {
        this.shipLoc = shipLoc;
    }

}
