package edu.jhipsterex.example.repository;

import edu.jhipsterex.example.domain.Authority;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Neo4j repository for the {@link Authority} entity.
 */
public interface AuthorityRepository extends ReactiveNeo4jRepository<Authority, String> {
    Flux<Authority> findAll();
}
