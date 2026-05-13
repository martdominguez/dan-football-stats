package com.miniscore.core.config;

import com.miniscore.core.entity.League;
import com.miniscore.core.entity.Player;
import com.miniscore.core.entity.Team;
import com.miniscore.core.repository.LeagueRepository;
import com.miniscore.core.repository.PlayerRepository;
import com.miniscore.core.repository.TeamRepository;
import java.util.List;
import java.util.UUID;
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
                    UUID.fromString("11111111-1111-1111-1111-111111111111"),
                    "Campus Jaguars",
                    "JAG",
                    league));
            Team sharks = teamRepository.save(new Team(
                    UUID.fromString("22222222-2222-2222-2222-222222222222"),
                    "Engineering Sharks",
                    "SHA",
                    league));

            playerRepository.saveAll(List.of(
                    new Player(UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1"), "Luna", "Diaz", "Forward", 9, jaguars),
                    new Player(UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2"), "Mateo", "Sosa", "Midfielder", 8, jaguars),
                    new Player(UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb1"), "Emma", "Rios", "Forward", 10, sharks),
                    new Player(UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb2"), "Tomas", "Lopez", "Defender", 4, sharks)
            ));
        };
    }
}
