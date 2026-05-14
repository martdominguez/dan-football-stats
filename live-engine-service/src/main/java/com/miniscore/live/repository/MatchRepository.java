package com.miniscore.live.repository;

import com.miniscore.live.document.MatchDocument;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MatchRepository extends MongoRepository<MatchDocument, ObjectId> {
}
