package com.miniscore.live.repository;

import com.miniscore.live.document.MatchDocument;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MatchRepository extends MongoRepository<MatchDocument, String> {

    Optional<MatchDocument> findByMatchId(UUID matchId);
}
