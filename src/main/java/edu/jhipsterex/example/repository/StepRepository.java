package edu.jhipsterex.example.repository;

import edu.jhipsterex.example.domain.Step;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data Neo4j reactive repository for the Step entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StepRepository extends ReactiveNeo4jRepository<Step, String> {
    Flux<Step> findAllBy(Pageable pageable);

    @Query("MATCH (n:Step)<-[]-(m) RETURN n,m")
    Flux<Step> findAllWithEagerRelationships(Pageable pageable);

    @Query("MATCH (n:Step)<-[]-(m) RETURN n,m")
    Flux<Step> findAllWithEagerRelationships();

    @Query("MATCH (e:Step {id: $id}) RETURN e")
    Mono<Step> findOneWithEagerRelationships(String id);
}
