package edu.jhipsterex.example.service;

import edu.jhipsterex.example.domain.Step;
import edu.jhipsterex.example.repository.StepRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Step}.
 */
@Service
public class StepService {

    private final Logger log = LoggerFactory.getLogger(StepService.class);

    private final StepRepository stepRepository;

    public StepService(StepRepository stepRepository) {
        this.stepRepository = stepRepository;
    }

    /**
     * Save a step.
     *
     * @param step the entity to save.
     * @return the persisted entity.
     */
    public Mono<Step> save(Step step) {
        log.debug("Request to save Step : {}", step);
        return stepRepository.save(step);
    }

    /**
     * Update a step.
     *
     * @param step the entity to save.
     * @return the persisted entity.
     */
    public Mono<Step> update(Step step) {
        log.debug("Request to save Step : {}", step);
        return stepRepository.save(step);
    }

    /**
     * Partially update a step.
     *
     * @param step the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<Step> partialUpdate(Step step) {
        log.debug("Request to partially update Step : {}", step);

        return stepRepository
            .findById(step.getId())
            .map(existingStep -> {
                if (step.getName() != null) {
                    existingStep.setName(step.getName());
                }
                if (step.getNote() != null) {
                    existingStep.setNote(step.getNote());
                }

                return existingStep;
            })
            .flatMap(stepRepository::save);
    }

    /**
     * Get all the steps.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Flux<Step> findAll(Pageable pageable) {
        log.debug("Request to get all Steps");
        return stepRepository.findAllBy(pageable);
    }

    /**
     * Get all the steps with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Flux<Step> findAllWithEagerRelationships(Pageable pageable) {
        return stepRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Returns the number of steps available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return stepRepository.count();
    }

    /**
     * Get one step by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Mono<Step> findOne(String id) {
        log.debug("Request to get Step : {}", id);
        return stepRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the step by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Step : {}", id);
        return stepRepository.deleteById(id);
    }
}
