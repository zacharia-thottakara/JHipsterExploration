package edu.jhipsterex.example.web.rest;

import edu.jhipsterex.example.domain.Step;
import edu.jhipsterex.example.repository.StepRepository;
import edu.jhipsterex.example.service.StepService;
import edu.jhipsterex.example.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link edu.jhipsterex.example.domain.Step}.
 */
@RestController
@RequestMapping("/api")
public class StepResource {

    private final Logger log = LoggerFactory.getLogger(StepResource.class);

    private static final String ENTITY_NAME = "step";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StepService stepService;

    private final StepRepository stepRepository;

    public StepResource(StepService stepService, StepRepository stepRepository) {
        this.stepService = stepService;
        this.stepRepository = stepRepository;
    }

    /**
     * {@code POST  /steps} : Create a new step.
     *
     * @param step the step to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new step, or with status {@code 400 (Bad Request)} if the step has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/steps")
    public Mono<ResponseEntity<Step>> createStep(@Valid @RequestBody Step step) throws URISyntaxException {
        log.debug("REST request to save Step : {}", step);
        if (step.getId() != null) {
            throw new BadRequestAlertException("A new step cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return stepService
            .save(step)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/steps/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /steps/:id} : Updates an existing step.
     *
     * @param id the id of the step to save.
     * @param step the step to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated step,
     * or with status {@code 400 (Bad Request)} if the step is not valid,
     * or with status {@code 500 (Internal Server Error)} if the step couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/steps/{id}")
    public Mono<ResponseEntity<Step>> updateStep(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody Step step
    ) throws URISyntaxException {
        log.debug("REST request to update Step : {}, {}", id, step);
        if (step.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, step.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return stepRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return stepService
                    .update(step)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /steps/:id} : Partial updates given fields of an existing step, field will ignore if it is null
     *
     * @param id the id of the step to save.
     * @param step the step to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated step,
     * or with status {@code 400 (Bad Request)} if the step is not valid,
     * or with status {@code 404 (Not Found)} if the step is not found,
     * or with status {@code 500 (Internal Server Error)} if the step couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/steps/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Step>> partialUpdateStep(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody Step step
    ) throws URISyntaxException {
        log.debug("REST request to partial update Step partially : {}, {}", id, step);
        if (step.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, step.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return stepRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Step> result = stepService.partialUpdate(step);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /steps} : get all the steps.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of steps in body.
     */
    @GetMapping("/steps")
    public Mono<ResponseEntity<List<Step>>> getAllSteps(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of Steps");
        return stepService
            .countAll()
            .zipWith(stepService.findAll(pageable).collectList())
            .map(countWithEntities ->
                ResponseEntity
                    .ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            UriComponentsBuilder.fromHttpRequest(request),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2())
            );
    }

    /**
     * {@code GET  /steps/:id} : get the "id" step.
     *
     * @param id the id of the step to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the step, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/steps/{id}")
    public Mono<ResponseEntity<Step>> getStep(@PathVariable String id) {
        log.debug("REST request to get Step : {}", id);
        Mono<Step> step = stepService.findOne(id);
        return ResponseUtil.wrapOrNotFound(step);
    }

    /**
     * {@code DELETE  /steps/:id} : delete the "id" step.
     *
     * @param id the id of the step to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/steps/{id}")
    public Mono<ResponseEntity<Void>> deleteStep(@PathVariable String id) {
        log.debug("REST request to delete Step : {}", id);
        return stepService
            .delete(id)
            .then(
                Mono.just(
                    ResponseEntity
                        .noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id))
                        .build()
                )
            );
    }
}
