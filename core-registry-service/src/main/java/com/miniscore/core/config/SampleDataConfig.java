package com.miniscore.core.config;

import com.miniscore.core.entity.League;
import com.miniscore.core.entity.Player;
import com.miniscore.core.entity.Team;
import com.miniscore.core.repository.LeagueRepository;
import com.miniscore.core.repository.PlayerRepository;
import com.miniscore.core.repository.TeamRepository;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SampleDataConfig {

    @Bean
    CommandLineRunner coreSampleData(LeagueRepository leagueRepository,
                                     TeamRepository teamRepository,
                                     PlayerRepository playerRepository) {
        return args -> {
            if (leagueRepository.count() > 0) {
                return;
            }

            League league = leagueRepository.save(new League("University Premier League", "Argentina"));

            Team jaguars = teamRepository.save(new Team(
                    "Campus Jaguars",
                    "JAG",
                    league));
            Team sharks = teamRepository.save(new Team(
                    "Engineering Sharks",
                    "SHA",
                    league));

            playerRepository.saveAll(List.of(
                    new Player("Luna", "Diaz", "Forward", 9, jaguars),
                    new Player("Mateo", "Sosa", "Midfielder", 8, jaguars),
                    new Player("Emma", "Rios", "Forward", 10, sharks),
                    new Player("Tomas", "Lopez", "Defender", 4, sharks)
            ));
        };
    }
}
