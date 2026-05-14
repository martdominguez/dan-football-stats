package com.miniscore.stats.repository;

import com.miniscore.stats.entity.TeamStanding;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamStandingRepository extends JpaRepository<TeamStanding, Long> {

    Optional<TeamStanding> findByTeamId(Long teamId);

    List<TeamStanding> findByLeagueNameOrderByPointsDesc(String leagueName);
}
