package com.example.salvo;


import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.Id;
import java.util.*;
import javax.persistence.CascadeType;
import java.util.stream.Collectors;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Player {

    //atributos (características de los objetos del tipo Player)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private String userName;
    private String firstName;
    private String lastName;
    private int xp;

    /*
    relación one to Many con Gameplayer
    nota: un player va a ser muchos game player || muchos gameplayer va a ser un player
     */
    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<GamePlayer> gamePlayers = new HashSet<>();

    /*
    relación one to Many con Score
    nota: un player va a ser muchos Scores || muchos Scores va a ser un player
     */
    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Score> scores = new HashSet<>();

    /*===================       CONSTRUCTORES       =====================*/
    //constructores (intrucciones para instanciar un objeto del tipo Player)
    public Player() {
    }

    public Player(String userName, String firstName, String lastName) {
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.xp = 0;
    }

    public Player(String userName, String firstName, String lastName, int xp) {
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.xp = xp;
    }

    /*===================     MÉTODOS     =====================*/

    //getters & setters
    public long getId(){
        return this.id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFirstName(){
        return this.firstName;
    }

    public void setFirstName(String firstName){
        this.firstName = firstName;
    }

    public String getLastName(){
        return this.lastName;
    }

    public void setLastName(String lastName){
        this.lastName = lastName;
    }

    public Set<GamePlayer> getGamePlayers(){
        return this.gamePlayers;
    }

    //método para establecer la relación entre un objeto Player y un objeto GamePlayer
    public void addGamePlayer(GamePlayer gamePlayer) {
        //se agrega el gamePlayer que ingresa como parámetro al set declarado en los atributos
        this.gamePlayers.add(gamePlayer);
        //al gamePlayer ingresado se le agrega este player mediante su setter en la clase GamePlayer
        gamePlayer.setPlayer(this);
    }

    public Set<Score> getScores (){
        return this.scores;
    }

    public void addScore(Score score){
        this.scores.add(score);
        score.setPlayer(this);
    }

    public Score getScoreByGame(Game game){
        return this.scores.stream()
                .filter(score -> score.getGame().getId() == game.getId())
                .findFirst()
                .orElse(null);
    }
    /*
    este codigo streamea (recorre) scores y busca puntajes del id == luego este busca por el id
     y devuelve el primer dato true que encuentre o sí no devuelve null. el stream actua como for y el find first or else
     como un if/else
    */

    //método que retorna todos los games relacionados con el player a partir de los gamePlayers
    @JsonIgnore
    public List<Game> getGames() {
        return this.gamePlayers.stream().map(x -> x.getGame()).collect(Collectors.toList());
    }

    //métodos (comportamientos de los objetos del tipo Player)
    public String greet(){
        return "Hi! my name is " + this.firstName;
    }

    public String completeName(){
        return this.firstName + " " + this.lastName;
    }

    public String getExperience(){
        if(this.xp < 5000){
            return "newbie";
        } else if(this.xp < 25000){
            return "amateur";
        } else if(this.xp < 50000){
            return "pro";
        } else{
            return "legend";
        }
    }

    //DTO (data transfer object) para administrar la info de Player
    public Map<String, Object> playerDTO(){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", this.getId());
        dto.put("username", this.getUserName());
        return dto;
    }

}