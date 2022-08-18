package edu.jhipsterex.example.repository;

import edu.jhipsterex.example.domain.Person;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data Neo4j reactive repository for the Person entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PersonRepository extends ReactiveNeo4jRepository<Person, String> {
    Flux<Person> findAllBy(Pageable pageable);

    @Query("MATCH (n:Person)<-[]-(m) RETURN n,m")
    Flux<Person> findAllWithEagerRelationships(Pageable pageable);

    @Query("MATCH (n:Person)<-[]-(m) RETURN n,m")
    Flux<Person> findAllWithEagerRelationships();

    @Query("MATCH (e:Person {id: $id}) RETURN e")
    Mono<Person> findOneWithEagerRelationships(String id);
}
