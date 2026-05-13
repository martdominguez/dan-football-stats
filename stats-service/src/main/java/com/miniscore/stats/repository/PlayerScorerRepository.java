package com.miniscore.stats.repository;

import com.miniscore.stats.entity.PlayerScorer;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerScorerRepository extends JpaRepository<PlayerScorer, Long> {

    Optional<PlayerScorer> findByPlayerId(UUID playerId);

    List<PlayerScorer> findTop10ByOrderByGoalsDescPlayerNameAsc();
}
