package com.mirkoexample.Salvo.models;

import com.mirkoexample.Salvo.SalvoController;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class Salvo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id_salvo;

    /*
    muchos salvos para un gameplayer
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gamePlayerID")
    private GamePlayer gameplayer;

    private long turn;

    /*
    array que guarda las locaciones de los tiros
     */
    @ElementCollection
    @Column(name="salvoLocations")
    private List<String> locations;

    /*
    constructores
     */
    public Salvo() {
    }

    public Salvo(GamePlayer gameplayer, long turn, List<String> locations) {
        this.gameplayer = gameplayer;
        this.turn = turn;
        this.locations = locations;
    }

    /*
    Metodos
     */

    public GamePlayer getGameplayer() {
        return gameplayer;
    }

    public void setGameplayer(GamePlayer gameplayer) {
        this.gameplayer = gameplayer;
    }

    public long getTurn() {
        return turn;
    }

    public void setTurn(long turn) {
        this.turn = turn;
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    /*
    verifica donde se ejecutaron los tiros y si hubo coincidencia con las ubicaiones de los ships del oponente
     */
    public List<String> getHits(List <String> currentSalvoLocations, Set<Ship> opponentShips) {
        return currentSalvoLocations
                .stream()
                .filter(location -> opponentShips
                        .stream()
                        .anyMatch(ship -> ship.getShipLocations().contains(location)))
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getSinks(long turn, Set <Salvo> mySalvos, Set<Ship> opponentShips) {
        List<String> allShots = new ArrayList<>();
        /*recorre el set de mis salvos y filtra aquellos cuyo turno es menor o igual al turno actual
        luego se agrega a allShots las ubicaciones de esos salvos*/
        mySalvos.stream()
                .filter(salvo -> salvo.getTurn() <= turn)
                .forEach(salvo -> allShots.addAll(salvo.getLocations()));
        //aqui retornamos la coleccion de mapas de los barcos del oponente que hayan sido tocados en todas sus locations.
        return opponentShips.stream()
                .filter(ship -> allShots.containsAll(ship.getShipLocations()))
                .map(ship -> new SalvoController().makeShipDto(ship))
                .collect(Collectors.toList());
    }

}
