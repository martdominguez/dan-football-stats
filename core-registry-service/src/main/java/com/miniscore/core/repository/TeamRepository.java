package com.miniscore.core.repository;

import com.miniscore.core.entity.Team;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, UUID> {
}
