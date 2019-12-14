package com.example.salvo;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import org.hibernate.annotations.GenericGenerator;
import java.time.LocalDateTime;
import java.util.*;
import javax.persistence.CascadeType;
import java.util.stream.Collectors;


//anotación para Spring. Le dice que debe crear una Tabla en la base de datos para esta clase.
@Entity
public class Game {

    //propiedad para identificar a cada instancia de la clase en la base de datos
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    //propiedades de la clase Game
    private LocalDateTime creationDate;

    /*relación One to Many con Player a través de la instancia intermedia GamePlayer
      nota: One to many, mas de un jugador en un game    (games)
     */
    @OneToMany(mappedBy="game", fetch=FetchType.EAGER, cascade= CascadeType.ALL)
    private Set<GamePlayer> gamePlayers = new HashSet<>();

    @OneToMany(mappedBy="game", fetch=FetchType.EAGER, cascade= CascadeType.ALL)
    private Set<Score> scores = new HashSet<>();

    /*===================       CONSTRUCTORES       =====================*/

    //constructor vacío
    public Game() {}

    public Game(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    /*===================     MÉTODOS     =====================*/

    //getters & setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    //método para establecer la relación entre un objeto Game y un objeto GamePlayer
    public void addGamePlayer(GamePlayer gamePlayer) {
        //se agrega el gamePlayer que ingresa como parámetro al set declarado en los atributos
        this.gamePlayers.add(gamePlayer);
        //al gamePlayer ingresado se le agrega este game mediante su setter en la clase GamePlayer
        gamePlayer.setGame(this);
    }

    //método que retorna todos los players relacionados con el game a partir de los gamePlayers
    public List<Player> getPlayers() {
        return this.gamePlayers.stream().map(gp -> gp.getPlayer()).collect(Collectors.toList());
    }

    //DTO (data transfer object) para administrar la info de Game
    public Map<String, Object> gameDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", this.getId());
        dto.put("created", this.getCreationDate());
        dto.put("gamePlayers", this.getGamePlayers().stream().map(GamePlayer::gamePlayerDTO).collect(Collectors.toList()));
        return dto;
    }

}

