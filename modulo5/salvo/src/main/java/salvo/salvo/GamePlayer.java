package salvo.salvo;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.*;

@Entity
public class GamePlayer {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;
    private Date joinDate;

    @ManyToOne
    @JoinColumn(name="playerId")
    private Player player;

    @ManyToOne
    @JoinColumn(name="gameId")
    private Game game;

    @OneToMany(mappedBy="gamePlayer", fetch=FetchType.EAGER)
    Set<Ship> ships = new HashSet<>();

    @OneToMany(mappedBy="gamePlayer", fetch=FetchType.EAGER)
    Set<Salvo> salvos = new HashSet<>();

    public GamePlayer() {}

    public GamePlayer(Player player, Game game, Date joinDate) {

        this.player = player;
        this.game = game;
        this.joinDate = joinDate;

    }

    public long getId() {
        return id;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player= player;
    }

    @JsonIgnore
    public Game getGame() {
        return game;
    }
    public void setGame(Game game) {
        this.game = game;
    }

    @JsonIgnore
    public Date getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }

    public Set<Ship> getShips() {
        return ships;
    }

    public void addShip(Ship ship) {
        ship.setGamePlayer(this);
        ships.add(ship);
    }

    public Set<Salvo> getSalvos() {
        return salvos;
    }

    public void addSalvo(Salvo salvo) {
        salvo.setGamePlayer(this);
        salvos.add(salvo);
    }

//    public Score getScore(){
//        return player.getScore(game);
//    }

}
