package edu.jhipsterex.example.repository;

import edu.jhipsterex.example.domain.Event;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data Neo4j reactive repository for the Event entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EventRepository extends ReactiveNeo4jRepository<Event, String> {
    Flux<Event> findAllBy(Pageable pageable);

    @Query("MATCH (n:Event)<-[]-(m) RETURN n,m")
    Flux<Event> findAllWithEagerRelationships(Pageable pageable);

    @Query("MATCH (n:Event)<-[]-(m) RETURN n,m")
    Flux<Event> findAllWithEagerRelationships();

    @Query("MATCH (e:Event {id: $id}) RETURN e")
    Mono<Event> findOneWithEagerRelationships(String id);
}
