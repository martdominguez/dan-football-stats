package com.miniscore.stats.repository;

import com.miniscore.stats.entity.PlayerScorer;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerScorerRepository extends JpaRepository<PlayerScorer, Long> {

    Optional<PlayerScorer> findByPlayerId(Long playerId);

    List<PlayerScorer> findTop10ByOrderByGoalsDescPlayerNameAsc();
}
