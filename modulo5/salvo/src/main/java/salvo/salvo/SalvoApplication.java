package salvo.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);

	}
	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository, GamePlayerRepository gamePlayerRepository, ShipRepository shipRepository, SalvoRepository salvoRepository, ScoreRepository scoreRepository) {
		return (args) -> {
			//create players

			Player p1 = new Player("j.bauer@ctu.gov", "24");
			Player p2 = new Player("c.obrian@ctu.gov", "42");
			Player p3 = new Player("kim_bauer@gmail.com", "kb");
			Player p4 = new Player("t.almeida@ctu.gov", "mole");

			// save players

			playerRepository.save(p1);
			playerRepository.save(p2);
			playerRepository.save(p3);
			playerRepository.save(p4);

			//create dates

			Date d1 = new Date();
			Date d2 = Date.from(d1.toInstant().plusSeconds(3600));
			Date d3 = Date.from(d1.toInstant().plusSeconds(7200));

			//create games

			Game g1 = new Game(d1);
			Game g2 = new Game(d2);
			Game g3 = new Game(d3);
			Game g4 = new Game(d1);
			Game g5 = new Game(d1);
			Game g6 = new Game(d1);
			Game g7 = new Game(d1);
			Game g8 = new Game(d1);

			//save games

			gameRepository.save(g1);
			gameRepository.save(g2);
			gameRepository.save(g3);
			gameRepository.save(g4);
			gameRepository.save(g5);
			gameRepository.save(g6);
			gameRepository.save(g7);
			gameRepository.save(g8);

			//create join dates

			Date jd1 = new Date();

			//create gameplayers

			GamePlayer gp1 = new GamePlayer(p1, g1, jd1);
			GamePlayer gp2 = new GamePlayer(p2, g1, jd1);
			GamePlayer gp3 = new GamePlayer(p1, g2, jd1);
			GamePlayer gp4 = new GamePlayer(p2, g2, jd1);
			GamePlayer gp5 = new GamePlayer(p2, g3, jd1);
			GamePlayer gp6 = new GamePlayer(p4, g3, jd1);
			GamePlayer gp7 = new GamePlayer(p2, g4, jd1);
			GamePlayer gp8 = new GamePlayer(p1, g4, jd1);
			GamePlayer gp9 = new GamePlayer(p4, g5, jd1);
			GamePlayer gp10 = new GamePlayer(p3, g6, jd1);
			GamePlayer gp11 = new GamePlayer(p4, g7, jd1);
			GamePlayer gp12 = new GamePlayer(p3, g8, jd1);
			GamePlayer gp13 = new GamePlayer(p4, g8, jd1);
			GamePlayer gp14 = new GamePlayer(p1, g5, jd1);

			//save gameplayers

			gamePlayerRepository.save(gp1);
			gamePlayerRepository.save(gp2);
			gamePlayerRepository.save(gp3);
			gamePlayerRepository.save(gp4);
			gamePlayerRepository.save(gp5);
			gamePlayerRepository.save(gp6);
			gamePlayerRepository.save(gp7);
			gamePlayerRepository.save(gp8);
			gamePlayerRepository.save(gp9);
			gamePlayerRepository.save(gp10);
			gamePlayerRepository.save(gp11);
			gamePlayerRepository.save(gp12);
			gamePlayerRepository.save(gp13);
			gamePlayerRepository.save(gp14);

			//create shipLocations

			List<String> shipLoc1 = new ArrayList<>();
			shipLoc1.add(0, "H2");
			shipLoc1.add(1, "H3");
			shipLoc1.add(2, "H4");
			List<String> shipLoc2 = new ArrayList<>();
			shipLoc2.add(0, "E1");
			shipLoc2.add(1, "F1");
			shipLoc2.add(2, "G1");
			List<String> shipLoc3 = new ArrayList<>();
			shipLoc3.add(0, "B4");
			shipLoc3.add(1, "B5");
			List<String> shipLoc4 = new ArrayList<>();
			shipLoc4.add(0, "B5");
			shipLoc4.add(1, "C5");
			shipLoc4.add(2, "D5");
			List<String> shipLoc5 = new ArrayList<>();
			shipLoc5.add(0, "C6");
			shipLoc5.add(1, "C7");
			List<String> shipLoc6 = new ArrayList<>();
			shipLoc6.add(0, "A2");
			shipLoc6.add(1, "A3");
			shipLoc6.add(2, "A4");
			List<String> shipLoc7 = new ArrayList<>();
			shipLoc7.add(0, "G6");
			shipLoc7.add(1, "H6");
			List<String> shipLoc8 = new ArrayList<>(Arrays.asList("B4", "B5", "B6"));
			List<String> shipLoc9 = new ArrayList<>(Arrays.asList("F1", "F2"));


			//create ships and add to gameplayer

			Ship sh1 = new Ship("Destroyer", shipLoc1);
			gp1.addShip(sh1);
			Ship sh2 = new Ship("Submarine", shipLoc2);
			gp1.addShip(sh2);
			Ship sh3 = new Ship("Patrol Boat", shipLoc3);
			gp1.addShip(sh3);
			Ship sh4 = new Ship("Destroyer", shipLoc4);
			gp2.addShip(sh4);
			Ship sh5 = new Ship("Submarine", shipLoc6);
			gp4.addShip(sh5);
			Ship sh6 = new Ship("Patrol Boat", shipLoc9);
			gp2.addShip(sh6);
			Ship sh7 = new Ship("Destroyer", shipLoc4);
			gp3.addShip(sh7);
			Ship sh8 = new Ship("Patrol Boat", shipLoc5);
			gp3.addShip(sh8);
			Ship sh9 = new Ship("Patrol Boat", shipLoc7);
			gp4.addShip(sh9);
			Ship sh10 = new Ship("Destroyer", shipLoc4);
			gp5.addShip(sh10);
			Ship sh11 = new Ship("Patrol Boat", shipLoc5);
			gp5.addShip(sh11);
			Ship sh12 = new Ship("Submarine", shipLoc6);
			gp6.addShip(sh12);
			Ship sh13 = new Ship("Patrol Boat", shipLoc7);
			gp6.addShip(sh13);
			Ship sh14 = new Ship("Destroyer", shipLoc4);
			gp7.addShip(sh14);
			Ship sh15 = new Ship("Patrol Boat", shipLoc5);
			gp7.addShip(sh15);
			Ship sh16 = new Ship("Submarine", shipLoc6);
			gp8.addShip(sh16);
			Ship sh17 = new Ship("Patrol Boat", shipLoc7);
			gp8.addShip(sh17);
			Ship sh18 = new Ship("Destroyer", shipLoc4);
			gp9.addShip(sh18);
			Ship sh19 = new Ship("Patrol Boat", shipLoc5);
			gp9.addShip(sh19);
			Ship sh20 = new Ship("Submarine", shipLoc6);
			gp14.addShip(sh20);
			Ship sh21 = new Ship("Patrol Boat", shipLoc7);
			gp14.addShip(sh21);

			//create salvoLocations

			List<String> salvoLoc1 = new ArrayList<>();
			salvoLoc1.add(0, "salvoB5");
			salvoLoc1.add(1, "salvoC5");
			salvoLoc1.add(2, "salvoF1");
			List<String> salvoLoc2 = new ArrayList<>(Arrays.asList("salvoB4", "salvoB5", "salvoB6"));
			List<String> salvoLoc3 = new ArrayList<>(Arrays.asList("salvoF2", "salvoD5"));
			List<String> salvoLoc4 = new ArrayList<>(Arrays.asList("salvoE1", "salvoH3", "salvoA2"));
			List<String> salvoLoc5 = new ArrayList<>(Arrays.asList("salvoA2", "salvoA4", "salvoG6"));
			List<String> salvoLoc6 = new ArrayList<>(Arrays.asList("salvoB5", "salvoD5", "salvoC7"));
			List<String> salvoLoc7 = new ArrayList<>(Arrays.asList("salvoA3", "salvoH6"));
			List<String> salvoLoc8 = new ArrayList<>(Arrays.asList("salvoC5", "salvoC6"));
			List<String> salvoLoc9 = new ArrayList<>(Arrays.asList("salvoG6", "salvoH6", "salvoA4"));
			List<String> salvoLoc10 = new ArrayList<>(Arrays.asList("salvoH1", "salvoH2", "salvoH3"));
			List<String> salvoLoc11 = new ArrayList<>(Arrays.asList("salvoA2", "salvoA3", "salvoD8"));
			List<String> salvoLoc12 = new ArrayList<>(Arrays.asList("salvoE1", "salvoF2", "salvoG3"));
			List<String> salvoLoc13 = new ArrayList<>(Arrays.asList("salvoA3", "salvoA4", "salvoF7"));
			List<String> salvoLoc14 = new ArrayList<>(Arrays.asList("salvoB5", "salvoC6", "salvoH1"));
			List<String> salvoLoc15 = new ArrayList<>(Arrays.asList("salvoA2", "salvoG6", "salvoH6"));
			List<String> salvoLoc16 = new ArrayList<>(Arrays.asList("salvoC5", "salvoC7", "salvoD5"));
			List<String> salvoLoc17 = new ArrayList<>(Arrays.asList("salvoA1", "salvoA2", "salvoA3"));
			List<String> salvoLoc18 = new ArrayList<>(Arrays.asList("salvoB5", "salvoB6", "salvoC7"));
			List<String> salvoLoc19 = new ArrayList<>(Arrays.asList("salvoG6", "salvoG7", "salvoG8"));
			List<String> salvoLoc20 = new ArrayList<>(Arrays.asList("salvoC6", "salvoD6", "salvoE6"));
			List<String> salvoLoc21 = new ArrayList<>(Arrays.asList("salvoH1", "salvoH8"));

			//create salvos and add to gameplayer

			Salvo s1 = new Salvo(1, salvoLoc1);
			gp1.addSalvo(s1);
			Salvo s2 = new Salvo(1, salvoLoc2);
			gp2.addSalvo(s2);
			Salvo s3 = new Salvo(2, salvoLoc3);
			gp1.addSalvo(s3);
			Salvo s4 = new Salvo(2, salvoLoc4);
			gp2.addSalvo(s4);
			Salvo s5 = new Salvo(1, salvoLoc5);
			gp3.addSalvo(s5);
			Salvo s6 = new Salvo(1, salvoLoc6);
			gp4.addSalvo(s6);
			Salvo s7 = new Salvo(2, salvoLoc7);
			gp3.addSalvo(s7);
			Salvo s8 = new Salvo(2, salvoLoc8);
			gp4.addSalvo(s8);
			Salvo s9 = new Salvo(1, salvoLoc9);
			gp5.addSalvo(s9);
			Salvo s10 = new Salvo(1, salvoLoc10);
			gp6.addSalvo(s10);
			Salvo s11 = new Salvo(2, salvoLoc11);
			gp5.addSalvo(s11);
			Salvo s12 = new Salvo(2, salvoLoc12);
			gp6.addSalvo(s12);
			Salvo s13 = new Salvo(1, salvoLoc13);
			gp7.addSalvo(s13);
			Salvo s14 = new Salvo(1, salvoLoc14);
			gp8.addSalvo(s14);
			Salvo s15 = new Salvo(2, salvoLoc15);
			gp7.addSalvo(s15);
			Salvo s16 = new Salvo(2, salvoLoc16);
			gp8.addSalvo(s16);
			Salvo s17 = new Salvo(1, salvoLoc17);
			gp9.addSalvo(s17);
			Salvo s18 = new Salvo(1, salvoLoc18);
			gp14.addSalvo(s18);
			Salvo s19 = new Salvo(2, salvoLoc19);
			gp9.addSalvo(s19);
			Salvo s20 = new Salvo(2, salvoLoc20);
			gp14.addSalvo(s20);
			Salvo s21 = new Salvo(3, salvoLoc21);
			gp14.addSalvo(s21);

			//create finishDate

			Date fd1 = new Date();

			//create scores and add to players and games

			Score sc1 = new Score(1.0, fd1);
			p1.addScore(sc1);
			g1.addScores(sc1);
			Score sc2 = new Score(0.0, fd1);
			p2.addScore(sc2);
			g1.addScores(sc2);
			Score sc3 = new Score(0.5, fd1);
			p1.addScore(sc3);
			g2.addScores(sc3);
			Score sc4 = new Score(0.5, fd1);
			p2.addScore(sc4);
			g2.addScores(sc4);
			Score sc5 = new Score(1.0, fd1);
			p2.addScore(sc5);
			g3.addScores(sc5);
			Score sc6 = new Score(0.0, fd1);
			p4.addScore(sc6);
			g3.addScores(sc6);
			Score sc7 = new Score(0.5, fd1);
			p2.addScore(sc7);
			g4.addScores(sc7);
			Score sc8 = new Score(0.5, fd1);
			p1.addScore(sc8);
			g4.addScores(sc8);

			//save gamePlayers

			gamePlayerRepository.save(gp1);
			gamePlayerRepository.save(gp2);
			gamePlayerRepository.save(gp3);
			gamePlayerRepository.save(gp4);
			gamePlayerRepository.save(gp5);
			gamePlayerRepository.save(gp6);
			gamePlayerRepository.save(gp7);
			gamePlayerRepository.save(gp8);
			gamePlayerRepository.save(gp9);
			gamePlayerRepository.save(gp14);


			//save ships

			shipRepository.save(sh1);
			shipRepository.save(sh2);
			shipRepository.save(sh3);
			shipRepository.save(sh4);
			shipRepository.save(sh5);
			shipRepository.save(sh6);
			shipRepository.save(sh7);
			shipRepository.save(sh8);
			shipRepository.save(sh9);
			shipRepository.save(sh10);
			shipRepository.save(sh11);
			shipRepository.save(sh12);
			shipRepository.save(sh13);
			shipRepository.save(sh14);
			shipRepository.save(sh15);
			shipRepository.save(sh16);
			shipRepository.save(sh17);
			shipRepository.save(sh18);
			shipRepository.save(sh19);
			shipRepository.save(sh20);
			shipRepository.save(sh21);

			//save salvos

			salvoRepository.save(s1);
			salvoRepository.save(s2);
			salvoRepository.save(s3);
			salvoRepository.save(s4);
			salvoRepository.save(s5);
			salvoRepository.save(s6);
			salvoRepository.save(s7);
			salvoRepository.save(s8);
			salvoRepository.save(s9);
			salvoRepository.save(s10);
			salvoRepository.save(s11);
			salvoRepository.save(s12);
			salvoRepository.save(s13);
			salvoRepository.save(s14);
			salvoRepository.save(s15);
			salvoRepository.save(s16);
			salvoRepository.save(s17);
			salvoRepository.save(s18);
			salvoRepository.save(s19);
			salvoRepository.save(s20);
			salvoRepository.save(s21);

			//save players with scores
			playerRepository.save(p1);
			playerRepository.save(p2);
			playerRepository.save(p4);

			//save games with scores

			gameRepository.save(g1);
			gameRepository.save(g2);
			gameRepository.save(g3);
			gameRepository.save(g4);


			//save scores

			scoreRepository.save(sc1);
			scoreRepository.save(sc2);
			scoreRepository.save(sc3);
			scoreRepository.save(sc4);
			scoreRepository.save(sc5);
			scoreRepository.save(sc6);
			scoreRepository.save(sc7);
			scoreRepository.save(sc8);



//			List<Game> games;
//
//			System.out.println("\n\nGet information about all Games by a specific id\n");
//			games = gameRepository.findAll();
//			for(Game game : games)
//			{
//				System.out.println(game.getId());
//			}
//
//			List<Player> players;
//
//			System.out.println("\n\nGet information about all Players by a specific userName\n");
//			players = playerRepository.findByUserName("j.bauer@ctu.gov");
//			for(Player player : players)
//			{
//				System.out.println(player.getUserName());
//			}

		};

	}

}

@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

	@Autowired
	PlayerRepository playerRepository;

	@Override
	public void init(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService());
	}

	@Bean
	UserDetailsService userDetailsService() {
		return new UserDetailsService() {

			@Override
			public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
				List<Player> players = playerRepository.findByUserName(userName);
				if (!players.isEmpty()) {
					Player player = players.get(0);
					return new User(player.getUserName(), player.getPassword(),
							AuthorityUtils.createAuthorityList("USER"));
				} else {
					throw new UsernameNotFoundException("Unknown user: " + userName);
				}
			}
		};
	}
}

@EnableWebSecurity
@Configuration
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/web/games.html", "/web/styles/gameStyle.css", "/web/scripts/games.js", "/api/games", "/api/players", "/api/game/**", "/games/players/**").permitAll()
                .antMatchers("/web/**").hasAuthority("USER")
                .antMatchers("/rest/**").denyAll()
                .anyRequest().fullyAuthenticated();

        http.formLogin()
                .usernameParameter("username")
                .passwordParameter("password")
                .loginPage("/api/login");

        http.logout()
                .logoutUrl("/api/logout");

        // turn off checking for CSRF tokens
        http.csrf().disable();

        // if user is not authenticated, just send an authentication failure response
        http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        // if login is successful, just clear the flags asking for authentication
        http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

        // if login fails, just send an authentication failure response
        http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        // if logout is successful, just send a success response
        http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());

    }

    private void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }
    }

}






