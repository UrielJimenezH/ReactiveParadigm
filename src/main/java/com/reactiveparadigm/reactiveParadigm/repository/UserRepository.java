package com.reactiveparadigm.reactiveParadigm.repository;

import com.reactiveparadigm.reactiveParadigm.domain.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends ReactiveMongoRepository<User, String> {
}
