package com.miniscore.core.repository;

import com.miniscore.core.entity.Player;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, UUID> {

    List<Player> findByTeamTeamIdOrderByShirtNumberAsc(UUID teamId);
}
