package com.miniscore.core.repository;

import com.miniscore.core.entity.Player;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    List<Player> findByTeamTeamIdOrderByShirtNumberAsc(Long teamId);
}
