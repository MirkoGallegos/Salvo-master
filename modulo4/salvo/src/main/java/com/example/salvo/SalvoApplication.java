package com.example.salvo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDateTime;


@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);

	}
		//por el momento para el testeo
	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository, GamePlayerRepository gamePlayerRepository, ScoreRepository scoreRepository) {
		return (args) -> {

			Player jack = playerRepository.save(new Player("j.bauer@ctu.gov", "Jack", "Bauer"));
			Player chloe = playerRepository.save(new Player("c.obrian@ctu.gov", "Chloe", "O'Brian"));
			Player kim = playerRepository.save(new Player("kim_bauer@gmail.com", "Kim", "Bauer"));
			Player tony = playerRepository.save(new Player("t.almeida@ctu.gov", "Tony", "Almeida"));

			Game game1 = gameRepository.save(new Game(LocalDateTime.now()));
			Game game2 = gameRepository.save(new Game(LocalDateTime.now().plusHours(1)));
			Game game3 = gameRepository.save(new Game(LocalDateTime.now().plusHours(2)));

			GamePlayer gp1 = gamePlayerRepository.save(new GamePlayer(game1, jack, LocalDateTime.now()));
			GamePlayer gp2 = gamePlayerRepository.save(new GamePlayer(game1, chloe, LocalDateTime.now()));
			GamePlayer gp3 = gamePlayerRepository.save(new GamePlayer(game2, kim, LocalDateTime.now()));
			GamePlayer gp4 = gamePlayerRepository.save(new GamePlayer(game2, tony, LocalDateTime.now()));
			GamePlayer gp5 = gamePlayerRepository.save(new GamePlayer(game3, jack, LocalDateTime.now()));

			gp1.addShip(new Ship("destoyer", Arrays.asList("A1", "A2", "A3")));
			gp1.addShip(new Ship("submarine", Arrays.asList("C1", "C2", "C3", "C4")));

			gp1.addSalvo(new Salvo(1,Arrays.asList("B5", "B6")));
			gp1.addSalvo(new Salvo(2,Arrays.asList("J1", "D9")));


			gp2.addShip(new Ship("destoyer", Arrays.asList("H1", "I1", "J1")));
			gp2.addShip(new Ship("submarine", Arrays.asList("D4", "D5", "D6", "D7")));

			gp2.addSalvo(new Salvo(1,Arrays.asList("A1", "F6")));
			gp2.addSalvo(new Salvo(2,Arrays.asList("A2", "A3")));

			scoreRepository.save(new Score(2,game1,jack,LocalDateTime.now()));
			scoreRepository.save(new Score(0,game1,chloe,LocalDateTime.now()));

			scoreRepository.save(new Score(1,game2,kim,LocalDateTime.now()));
			scoreRepository.save(new Score(1,game2,tony,LocalDateTime.now()));

			gamePlayerRepository.save(gp1);
			gamePlayerRepository.save(gp2);

		};

	}
}