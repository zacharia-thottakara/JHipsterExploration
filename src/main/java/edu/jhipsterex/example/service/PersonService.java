package edu.jhipsterex.example.service;

import edu.jhipsterex.example.domain.Person;
import edu.jhipsterex.example.repository.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Person}.
 */
@Service
public class PersonService {

    private final Logger log = LoggerFactory.getLogger(PersonService.class);

    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    /**
     * Save a person.
     *
     * @param person the entity to save.
     * @return the persisted entity.
     */
    public Mono<Person> save(Person person) {
        log.debug("Request to save Person : {}", person);
        return personRepository.save(person);
    }

    /**
     * Update a person.
     *
     * @param person the entity to save.
     * @return the persisted entity.
     */
    public Mono<Person> update(Person person) {
        log.debug("Request to save Person : {}", person);
        return personRepository.save(person);
    }

    /**
     * Partially update a person.
     *
     * @param person the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<Person> partialUpdate(Person person) {
        log.debug("Request to partially update Person : {}", person);

        return personRepository
            .findById(person.getId())
            .map(existingPerson -> {
                if (person.getName() != null) {
                    existingPerson.setName(person.getName());
                }
                if (person.getEmail() != null) {
                    existingPerson.setEmail(person.getEmail());
                }
                if (person.getNote() != null) {
                    existingPerson.setNote(person.getNote());
                }

                return existingPerson;
            })
            .flatMap(personRepository::save);
    }

    /**
     * Get all the people.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Flux<Person> findAll(Pageable pageable) {
        log.debug("Request to get all People");
        return personRepository.findAllBy(pageable);
    }

    /**
     * Get all the people with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Flux<Person> findAllWithEagerRelationships(Pageable pageable) {
        return personRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Returns the number of people available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return personRepository.count();
    }

    /**
     * Get one person by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Mono<Person> findOne(String id) {
        log.debug("Request to get Person : {}", id);
        return personRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the person by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Person : {}", id);
        return personRepository.deleteById(id);
    }
}
