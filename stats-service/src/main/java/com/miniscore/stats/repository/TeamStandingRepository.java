package com.miniscore.stats.repository;

import com.miniscore.stats.entity.TeamStanding;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamStandingRepository extends JpaRepository<TeamStanding, Long> {

    Optional<TeamStanding> findByTeamId(UUID teamId);

    List<TeamStanding> findByLeagueNameOrderByPointsDesc(String leagueName);
}
