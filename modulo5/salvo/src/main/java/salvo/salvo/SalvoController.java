package salvo.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class SalvoController {

    @Autowired
    private  GameRepository repoGames;
    @Autowired
    private PlayerRepository repoPlayers;
    @Autowired
    private GamePlayerRepository repoGamePlayer;
    @Autowired
    private ScoreRepository repoScore;


    @RequestMapping(path="/api/games")
    public Map<String, Object> playerLogged(Authentication auth) {

        Map<String, Object> playerLogged = new HashMap<>();

        if (isGuest(auth)) {
            playerLogged.put("player", "null");
            playerLogged.put("games", gameList());
        } else {
            String name = auth.getName();
            Player playerLog = repoPlayers.findOneByUserName(name);

            playerLogged.put("player", makeAuthenticationDTO(playerLog));
            playerLogged.put("games", gameList());
        }
        return playerLogged;
    }

    public Map<String, Object> makeAuthenticationDTO(Player player) {
        Map<String, Object> dto = new HashMap<>();

        dto.put("id", player.getId());
        dto.put("name", player.getUserName());

        return dto;
    }

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }

    public ArrayList<Map<String, Object>> gameList() {

        ArrayList<Map<String, Object>> gameList = new ArrayList<>();
        List<Game> listGames = repoGames.findAll();

        for (int j = 0; j < listGames.size(); j++) {

            Map<String, Object> eachGame = new HashMap<>();

            eachGame.put("id", listGames.get(j).getId());
            eachGame.put("created", listGames.get(j).getCreationDate());
            eachGame.put("gamePlayers", listGames.get(j).getGamePlayers()
                                                                        .stream()
                                                                        .map(gp -> makeGamePlayerDTO(gp))
                                                                        .collect(Collectors.toList()));

            gameList.add(eachGame);

        }
        return gameList;
    }

    public Map<String, Object> makeGamePlayerDTO(GamePlayer gamePlayer) {
        Map<String, Object> dto = new HashMap<>();

        dto.put("id", gamePlayer.getId());
        dto.put("player", makePlayerDTO(gamePlayer));

        return dto;
    }

    public Map<String, Object> makePlayerDTO(GamePlayer gp) {
        Map<String, Object> dto = new HashMap<>();

        dto.put("id", gp.getPlayer().getId());
        dto.put("email", gp.getPlayer().getUserName());

        Score score = gp.getPlayer().getScore(gp.getGame());

        if (score != null) {
            dto.put("score", score.getPoints());
        } else {
            dto.put("score", "null");
        }
        return dto;
    }

//    Función para crear nuevo juego

    @RequestMapping(path= "/api/games", method= RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createNewGame (Authentication auth){

        if (isGuest(auth)) {
            return new ResponseEntity<>(makeMap("error", "You can't create games if you're not logged"), HttpStatus.UNAUTHORIZED);
        }

        String name = auth.getName();
        Player playerLog = repoPlayers.findOneByUserName(name);
        Game newGame = repoGames.save(new Game(new Date()));
        GamePlayer newGp = repoGamePlayer.save(new GamePlayer(playerLog, newGame, new Date()));

        return new ResponseEntity<>(makeMap("gpId", newGp.getId()), HttpStatus.CREATED);

    }

    //Función para que un jugador pueda unirse a un juego ya creado

    @RequestMapping(path="/api/game/{gameId}/players", method=RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> joinGame(Authentication auth, @PathVariable Long gameId) {

        if (isGuest(auth)) {
            return new ResponseEntity<>(makeMap("error", "You can't join to games if you're not logged"), HttpStatus.UNAUTHORIZED);
        }

        Game gameToJoin = repoGames.findOne(gameId);

        if (gameToJoin == null){
            return new ResponseEntity<>(makeMap("error", "This game doesn't exists"), HttpStatus.FORBIDDEN);
        }

        if (gameToJoin.getGamePlayers().size()>1){
            return new ResponseEntity<>(makeMap("error", "Game is full"), HttpStatus.FORBIDDEN);
        }

        String name = auth.getName();
        Player playerLog = repoPlayers.findOneByUserName(name);
        GamePlayer gpExists = repoGamePlayer.findGamePlayerByGame(gameToJoin);

        if (gpExists.getPlayer().getUserName() == playerLog.getUserName()) {
            return new ResponseEntity<>(makeMap("error", "You're just in this game"), HttpStatus.UNAUTHORIZED);
        }

        GamePlayer gpToJoin = repoGamePlayer.save(new GamePlayer(playerLog, gameToJoin, new Date()));

        return new ResponseEntity<>(makeMap("gpId", gpToJoin.getId()), HttpStatus.CREATED);

    }

    @RequestMapping("/api/game_view/{gamePlayer_Id}")
    public ResponseEntity<Map<String, Object>> gpGame(@PathVariable Long gamePlayer_Id,
                                                      Authentication auth) {

        String name = auth.getName();
        Player playerLog = repoPlayers.findOneByUserName(name);
        GamePlayer listGamePlayers = repoGamePlayer.findOne(gamePlayer_Id);

        if(playerLog.getUserName() != listGamePlayers.getPlayer().getUserName()) {
            return new ResponseEntity<>(makeMap("error", "this is not your game"), HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(gameView(gamePlayer_Id), HttpStatus.OK);
    }

    public Map<String, Object> gameView(Long gamePlayer_Id) {

        GamePlayer listGamePlayers = repoGamePlayer.findOne(gamePlayer_Id);

        Map<String, Object> eachGameView = new HashMap<>();

        eachGameView.put("id", listGamePlayers.getGame().getId());
        eachGameView.put("creation", listGamePlayers.getGame().getCreationDate());
        eachGameView.put("gamePlayer", listGamePlayers.getGame().getGamePlayers().stream()
                .map(gamePlayer -> makeGamePlayerDTO(gamePlayer))
                .collect(Collectors.toList()));
        eachGameView.put("ships", listGamePlayers.getShips().stream()
                .map(ship -> makeShipDTO(ship))
                .collect(Collectors.toList()));
        eachGameView.put("salvoes", listGamePlayers.getGame().getGamePlayers().stream()
                .map(gp -> makeSalvoPlayerDTO(gp))
                .collect(Collectors.toList()));
        eachGameView.put("state", stateDTO(listGamePlayers));

        if (listGamePlayers.getGame().getGamePlayers().size() == 2) {

            eachGameView.put("hitsSink", makeOpponentDTO(listGamePlayers));
            eachGameView.put("hitsSinkOnMe", makeOpponentDTO(gpOpponent(listGamePlayers)));

        }

        return eachGameView;
    }

    public String stateDTO (GamePlayer gamePlayer) {

        Set<Ship> ships = gamePlayer.getShips();

        if (ships.size() == 0) {
            return "1-start";

        } else if (gamePlayer.getGame().getGamePlayers().size() < 2){
            return "2-waiting for opp";

        } else if (gpOpponent(gamePlayer).getShips().size() == 0){
            return "3-waiting opp place ships";

        } else if (gamePlayer.getId() < gpOpponent(gamePlayer).getId() &&
                   gpOpponent(gamePlayer).getShips().size() == 5 &&
                   gamePlayer.getSalvos().size() == 0) {
            return "4-you can start to add salvo";

        } else if (gamePlayer.getSalvos().size() < gpOpponent(gamePlayer).getSalvos().size()) {

            return "5-it is your turn to add salvo";

        } else if (gamePlayer.getSalvos().size() > gpOpponent(gamePlayer).getSalvos().size()) {

            return "6-waiting for opp add salvo";

        } else if (gamePlayer.getSalvos().size() == gpOpponent(gamePlayer).getSalvos().size()) {

            ArrayList<ArrayList<String>> hits = getHits(gamePlayer);

            if (allHits(gamePlayer).size() < 17 &&
                    allHits(gpOpponent(gamePlayer)).size() < 17 &&
                    gamePlayer.getId() < gpOpponent(gamePlayer).getId()) {

                return "5-it is your turn to add salvo";

            }
            if (allHits(gamePlayer).size() < 17 &&
                    allHits(gpOpponent(gamePlayer)).size() < 17 &&
                    gamePlayer.getId() > gpOpponent(gamePlayer).getId()) {

                return "6-waiting for opp add salvo";

            }
            if (allHits(gamePlayer).size() == 17 &&
                    allHits(gpOpponent(gamePlayer)).size() < 17) {

                if (gamePlayer.getPlayer().getScore(gamePlayer.getGame()) == null) {

                    Date fd1 = new Date();
                    Score winScore = new Score(1.0, fd1);

                    addScore(winScore, gamePlayer);
                }

                return "you win";

            }
            if (allHits(gamePlayer).size() < 17 &&
                    allHits(gpOpponent(gamePlayer)).size() == 17) {

                if (gamePlayer.getPlayer().getScore(gamePlayer.getGame()) == null) {

                    Date fd1 = new Date();
                    Score loseScore = new Score(0.0, fd1);

                    addScore(loseScore, gamePlayer);
                }

                return "you lose";

            }
            if (allHits(gamePlayer).size() == 17 &&
                    allHits(gpOpponent(gamePlayer)).size() == 17) {

                if (gamePlayer.getPlayer().getScore(gamePlayer.getGame()) == null) {

                    Date fd1 = new Date();
                    Score tieScore = new Score(0.0, fd1);

                    addScore(tieScore, gamePlayer);
                }

                return "you tie";

            }
            if (hits.size() == 10 &&
                    (allHits(gamePlayer).size() < 17 ||
                            allHits(gpOpponent(gamePlayer)).size() < 17)) {

                if (gamePlayer.getPlayer().getScore(gamePlayer.getGame()) == null) {

                    Date fd1 = new Date();
                    Score loseScore = new Score(0.0, fd1);

                    addScore(loseScore, gamePlayer);
                }

                return "you lose";
            }

        }
        return "";
    }

    public void addScore(Score score, GamePlayer gamePlayer) {

        Player player = gamePlayer.getPlayer();
        Game game = gamePlayer.getGame();

        player.addScore(score);
        game.addScores(score);

        repoPlayers.save(player);
        repoGames.save(game);
        repoScore.save(score);

    }

    public ArrayList<String> allHits(GamePlayer gamePlayer) {
        ArrayList<ArrayList<String>> hits = getHits(gamePlayer);

        ArrayList<String> allHits = new ArrayList<>();

        for (int i=0; i<hits.size(); i++) {

            for (int j=0; j<hits.get(i).size(); j++) {

                allHits.add(hits.get(i).get(j));
            }
        }

        return allHits;
    }

    public Map<String, Object> makeOpponentDTO(GamePlayer gamePlayer) {

        Map<String, Object> dto = new HashMap<>();

        dto.put("id", gpOpponent(gamePlayer).getId());
        dto.put("historial",  makeHistorialDTO(gamePlayer));
        dto.put("hits", getHits(gamePlayer));

        return dto;
    }

    public ArrayList<Map<String, Object>> makeHistorialDTO(GamePlayer gamePlayer) {

        ArrayList<ArrayList<String>> hits = getHits(gamePlayer);
        Set<Ship> shipLocs = gpOpponent(gamePlayer).getShips();

        ArrayList<Map<String, Object>> historial = new ArrayList<>();

        for (Ship ship : shipLocs) {

            Map<String, Object> dto = new HashMap<>();

            dto.put("turn", hits.size());
            dto.put("ship", ship.getShipType());
            dto.put("hitsCounted", countHitsByShip(ship, gamePlayer));
            dto.put("sunk", decideSunk(ship, gamePlayer));

            historial.add(dto);
        }

        return historial;
    }

    public Boolean decideSunk(Ship ship, GamePlayer gamePlayer) {

        List<String> shipLoc = ship.getShipLoc();

        return
            shipLoc.stream()
                    .allMatch(s -> allHits(gamePlayer).contains(s));
    }

    public Long countHitsByShip(Ship ship, GamePlayer gamePlayer) {

        ArrayList<ArrayList<String>> hits = getHits(gamePlayer);
        List<String> shipLoc = ship.getShipLoc();

        Long hitsCounted = 0L;

        ArrayList<String> allHits = new ArrayList<>();

        for (int i=0; i<hits.size(); i++) {

            for (int j=0; j<hits.get(i).size(); j++) {
                allHits.add(hits.get(i).get(j));
            }

            hitsCounted = shipLoc.stream()
                                .filter(s -> allHits.contains(s))
                                .count();
        }

        return hitsCounted;
    }

    public ArrayList<String> getOpponentShipLocations(GamePlayer gamePlayer) {

        ArrayList<String> opponentShipLocs = new ArrayList<>();

        Set<Ship> locs = gamePlayer.getShips();

        for (Ship ship : locs) {

            List<String> shipLoc = ship.getShipLoc();

            for (String locations : shipLoc) {
                opponentShipLocs.add(locations);
            }
        }
        return opponentShipLocs;
    }

    public ArrayList<ArrayList<String>> getHits(GamePlayer gamePlayer) {

        ArrayList<ArrayList<String>> hits = new ArrayList<>();

        ArrayList<String> opponentShipsLocs = getOpponentShipLocations(gpOpponent(gamePlayer));
        Set<Salvo> locs = gamePlayer.getSalvos();

        for (Salvo salvo : locs) {

            ArrayList<String> hit = new ArrayList<>();
            List<String> salvoLoc = salvo.getSalvoLocation();

            for (int j=0; j<opponentShipsLocs.size(); j++) {

                for (int i = 0; i < salvoLoc.size(); i++) {

                    if (opponentShipsLocs.get(j).equals(salvoLoc.get(i).substring(5))) {
                        hit.add(salvoLoc.get(i).substring(5));
                    }
                }
            }

            hits.add(hit);
        }

        return hits;
    }

    public GamePlayer gpOpponent(GamePlayer gamePlayer){

        GamePlayer gpOpponent = gamePlayer.getGame().getGamePlayers().stream()
                                                                    .filter(gp -> gp.getId() != gamePlayer.getId())
                                                                    .findFirst()
                                                                    .orElse(null);
        return gpOpponent;

    }

//    public Map<String, Object> gameView(Long gamePlayer_Id) {
//
//        GamePlayer listGamePlayers = repoGamePlayer.findOne(gamePlayer_Id);
//
//        Map<String, Object> eachGameView = new HashMap<>();
//
//        eachGameView.put("id", listGamePlayers.getGame().getId());
//        eachGameView.put("creation", listGamePlayers.getGame().getCreationDate());
//        eachGameView.put("gamePlayer", listGamePlayers.getGame().getGamePlayers().stream()
//                .map(gamePlayer -> makeGamePlayerDTO(gamePlayer))
//                .collect(Collectors.toList()));
//        eachGameView.put("ships", listGamePlayers.getShips().stream()
//                .map(ship -> makeShipDTO(ship))
//                .collect(Collectors.toList())
//        );
//        eachGameView.put("salvoes", listGamePlayers.getGame().getGamePlayers().stream()
//                .map(gp -> makeSalvoPlayerDTO(gp))
//                .collect(Collectors.toList()));
//
//        return eachGameView;
//
//    }


    public Map<String, Object> makeShipDTO(Ship ship) {
        Map<String, Object> dto = new HashMap<>();

        dto.put("type", ship.getShipType());
        dto.put("locations", ship.getShipLoc());
        return dto;
    }

    public Map<Long, Object> makeSalvoPlayerDTO(GamePlayer gamePlayer) {
        Map<Long, Object> dto = new HashMap<>();

        dto.put(gamePlayer.getPlayer().getId(), gamePlayer.getSalvos().stream()
                                                                    .map(salvo -> makeSalvoDTO(salvo))
                                                                    .collect(Collectors.toList()));
        return dto;
    }

    public Map<Integer, Object> makeSalvoDTO(Salvo salvo) {
        Map<Integer, Object> dto = new HashMap<>();

        dto.put(salvo.getTurn(), salvo.getSalvoLocation());

        return dto;
    }

    @RequestMapping(path = "/api/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createUser(@RequestParam String username, @RequestParam String password) {
        if (username.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>(makeMap("error", "empty field"), HttpStatus.FORBIDDEN);
        }
        Player player = repoPlayers.findOneByUserName(username);
        if (player != null) {
            return new ResponseEntity<>(makeMap("error", "Name in use"), HttpStatus.FORBIDDEN);
        }
        player = repoPlayers.save(new Player(username, password));
        return new ResponseEntity<>(makeMap("id", player.getId()), HttpStatus.CREATED);

    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    @Autowired
    private ShipRepository repoShips;

    @RequestMapping(path="/games/players/{gamePlayerId}/ships", method=RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> playerShips(@PathVariable Long gamePlayerId, @RequestBody Set<Ship> ships, Authentication auth) {
        if (isGuest(auth)) {
            return new ResponseEntity<>(makeMap("error", "You must be logged"), HttpStatus.UNAUTHORIZED);
        }

        GamePlayer gpExists = repoGamePlayer.findOne(gamePlayerId);

        if (gpExists == null) {
            return new ResponseEntity<>(makeMap("error", "This game player doesn't exists"), HttpStatus.UNAUTHORIZED);
        }

        String name = auth.getName();
        Player playerLogged = repoPlayers.findOneByUserName(name);

        if (gpExists.getPlayer().getUserName() != playerLogged.getUserName()) {
            return new ResponseEntity<>(makeMap("error", "This game player isn't yours"), HttpStatus.UNAUTHORIZED);
        }

        if (gpExists.getShips().size() > 0) {
            return new ResponseEntity<>(makeMap("error", "You already have ships placed"), HttpStatus.FORBIDDEN);
        }

        if (ships.size() < 5) {
            return new ResponseEntity<>(makeMap("error", "Low number of ships"), HttpStatus.FORBIDDEN);
        }

        if (ships.size() > 5) {
            return new ResponseEntity<>(makeMap("error", "High number of ships"), HttpStatus.FORBIDDEN);
        }

        for (Ship ship : ships) {

            gpExists.addShip(ship);
            repoGamePlayer.save(gpExists);
            repoShips.save(ship);

        }

        return new ResponseEntity<>(makeMap("ship", "ships created"), HttpStatus.CREATED);

    }

    @Autowired
    private SalvoRepository repoSalvos;

    @RequestMapping(path="/games/players/{gamePlayerId}/salvos", method=RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> playerSalvos(@PathVariable Long gamePlayerId, @RequestBody Salvo salvo, Authentication auth) {

        if (isGuest(auth)) {
            return new ResponseEntity<>(makeMap("error", "You must be logged"), HttpStatus.UNAUTHORIZED);
        }

        GamePlayer gpExists = repoGamePlayer.findOne(gamePlayerId);

        if (gpExists == null) {
            return new ResponseEntity<>(makeMap("error", "This game player doesn't exists"), HttpStatus.UNAUTHORIZED);
        }

        String name = auth.getName();
        Player playerLogged = repoPlayers.findOneByUserName(name);

        if (gpExists.getPlayer().getUserName() != playerLogged.getUserName()) {
            return new ResponseEntity<>(makeMap("error", "This game player isn't yours"), HttpStatus.UNAUTHORIZED);
        }

        if (salvo.getSalvoLocation().size() < 5) {
            return new ResponseEntity<>(makeMap("error", "Low number of shots"), HttpStatus.UNAUTHORIZED);
        }

        if (salvo.getSalvoLocation().size() > 5) {
            return new ResponseEntity<>(makeMap("error", "High number of shots"), HttpStatus.UNAUTHORIZED);
        }

        for (String salvoLoc : salvo.getSalvoLocation()) {

            if (gpExists.getSalvos().contains(salvoLoc)) {
                return new ResponseEntity<>(makeMap("error", "You already have a shot placed in this cell"), HttpStatus.UNAUTHORIZED);
            }

        }

        int turn = gpExists.getSalvos().size();
        salvo.setTurn(turn+1);

        gpExists.addSalvo(salvo);
        repoGamePlayer.save(gpExists);
        repoSalvos.save(salvo);

        return new ResponseEntity<>(makeMap("salvo", "salvos fired"), HttpStatus.CREATED);

    }

}
