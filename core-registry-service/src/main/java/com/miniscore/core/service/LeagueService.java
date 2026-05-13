package com.miniscore.core.service;

import com.miniscore.core.dto.CreateLeagueRequest;
import com.miniscore.core.dto.LeagueResponse;
import com.miniscore.core.entity.League;
import com.miniscore.core.repository.LeagueRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class LeagueService {

    private final LeagueRepository leagueRepository;

    public LeagueService(LeagueRepository leagueRepository) {
        this.leagueRepository = leagueRepository;
    }

    public LeagueResponse create(CreateLeagueRequest request) {
        League league = leagueRepository.save(new League(request.name(), request.country()));
        return toResponse(league);
    }

    public List<LeagueResponse> getAll() {
        return leagueRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    private LeagueResponse toResponse(League league) {
        return new LeagueResponse(league.getId(), league.getName(), league.getCountry());
    }
}
