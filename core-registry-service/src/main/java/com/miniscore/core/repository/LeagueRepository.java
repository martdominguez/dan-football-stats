package com.miniscore.core.repository;

import com.miniscore.core.entity.League;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeagueRepository extends JpaRepository<League, Long> {
}
