package com.example.salvo;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.ManyToAny;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.*;

@Entity
public class Salvo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private int turn;

    @ElementCollection
    private List<String> locations = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_id")
    private GamePlayer gamePlayer;

    /*===================       CONSTRUCTORES       =====================*/

    public Salvo() { }

    public Salvo (int turn,List<String> locations){
        this.turn = turn;
        this.locations = locations;
    }

    /*===================     MÉTODOS     =====================*/

    public long getId() { return id; }

    public void setId(long id) { this.id = id; }

    public int getTurn() { return turn; }

    public void setTurn(int turn) { this.turn = turn; }

    public List<String> getLocations() { return locations; }

    public void setLocations(List<String> locations) { this.locations = locations; }

    public GamePlayer getGamePlayer() { return gamePlayer; }

    public void setGamePlayer(GamePlayer gamePlayer) { this.gamePlayer = gamePlayer; }

    public Map<String, Object> salvoDto(){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("turn", this.getTurn());
        dto.put("player", this.getGamePlayer().getPlayer().getId());
        dto.put("locations", this.getLocations());
        return dto;
    }
}

