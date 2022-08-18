package edu.jhipsterex.example.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import edu.jhipsterex.example.IntegrationTest;
import edu.jhipsterex.example.domain.Step;
import edu.jhipsterex.example.repository.StepRepository;
import edu.jhipsterex.example.service.StepService;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Integration tests for the {@link StepResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class StepResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/steps";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private StepRepository stepRepository;

    @Mock
    private StepRepository stepRepositoryMock;

    @Mock
    private StepService stepServiceMock;

    @Autowired
    private WebTestClient webTestClient;

    private Step step;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Step createEntity() {
        Step step = new Step().name(DEFAULT_NAME).note(DEFAULT_NOTE);
        return step;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Step createUpdatedEntity() {
        Step step = new Step().name(UPDATED_NAME).note(UPDATED_NOTE);
        return step;
    }

    @BeforeEach
    public void setupCsrf() {
        webTestClient = webTestClient.mutateWith(csrf());
    }

    @BeforeEach
    public void initTest() {
        stepRepository.deleteAll().block();
        step = createEntity();
    }

    @Test
    void createStep() throws Exception {
        int databaseSizeBeforeCreate = stepRepository.findAll().collectList().block().size();
        // Create the Step
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(step))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Step in the database
        List<Step> stepList = stepRepository.findAll().collectList().block();
        assertThat(stepList).hasSize(databaseSizeBeforeCreate + 1);
        Step testStep = stepList.get(stepList.size() - 1);
        assertThat(testStep.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testStep.getNote()).isEqualTo(DEFAULT_NOTE);
    }

    @Test
    void createStepWithExistingId() throws Exception {
        // Create the Step with an existing ID
        step.setId("existing_id");

        int databaseSizeBeforeCreate = stepRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(step))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Step in the database
        List<Step> stepList = stepRepository.findAll().collectList().block();
        assertThat(stepList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = stepRepository.findAll().collectList().block().size();
        // set the field null
        step.setName(null);

        // Create the Step, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(step))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Step> stepList = stepRepository.findAll().collectList().block();
        assertThat(stepList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllSteps() {
        // Initialize the database
        stepRepository.save(step).block();

        // Get all the stepList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].note")
            .value(hasItem(DEFAULT_NOTE));
    }

    @Test
    void getStep() {
        // Initialize the database
        stepRepository.save(step).block();

        // Get the step
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, step.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.note")
            .value(is(DEFAULT_NOTE));
    }

    @Test
    void getNonExistingStep() {
        // Get the step
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewStep() throws Exception {
        // Initialize the database
        stepRepository.save(step).block();

        int databaseSizeBeforeUpdate = stepRepository.findAll().collectList().block().size();

        // Update the step
        Step updatedStep = stepRepository.findById(step.getId()).block();
        updatedStep.name(UPDATED_NAME).note(UPDATED_NOTE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedStep.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedStep))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Step in the database
        List<Step> stepList = stepRepository.findAll().collectList().block();
        assertThat(stepList).hasSize(databaseSizeBeforeUpdate);
        Step testStep = stepList.get(stepList.size() - 1);
        assertThat(testStep.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testStep.getNote()).isEqualTo(UPDATED_NOTE);
    }

    @Test
    void putNonExistingStep() throws Exception {
        int databaseSizeBeforeUpdate = stepRepository.findAll().collectList().block().size();
        step.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, step.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(step))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Step in the database
        List<Step> stepList = stepRepository.findAll().collectList().block();
        assertThat(stepList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchStep() throws Exception {
        int databaseSizeBeforeUpdate = stepRepository.findAll().collectList().block().size();
        step.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(step))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Step in the database
        List<Step> stepList = stepRepository.findAll().collectList().block();
        assertThat(stepList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamStep() throws Exception {
        int databaseSizeBeforeUpdate = stepRepository.findAll().collectList().block().size();
        step.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(step))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Step in the database
        List<Step> stepList = stepRepository.findAll().collectList().block();
        assertThat(stepList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateStepWithPatch() throws Exception {
        // Initialize the database
        stepRepository.save(step).block();

        int databaseSizeBeforeUpdate = stepRepository.findAll().collectList().block().size();

        // Update the step using partial update
        Step partialUpdatedStep = new Step();
        partialUpdatedStep.setId(step.getId());

        partialUpdatedStep.name(UPDATED_NAME).note(UPDATED_NOTE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedStep.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedStep))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Step in the database
        List<Step> stepList = stepRepository.findAll().collectList().block();
        assertThat(stepList).hasSize(databaseSizeBeforeUpdate);
        Step testStep = stepList.get(stepList.size() - 1);
        assertThat(testStep.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testStep.getNote()).isEqualTo(UPDATED_NOTE);
    }

    @Test
    void fullUpdateStepWithPatch() throws Exception {
        // Initialize the database
        stepRepository.save(step).block();

        int databaseSizeBeforeUpdate = stepRepository.findAll().collectList().block().size();

        // Update the step using partial update
        Step partialUpdatedStep = new Step();
        partialUpdatedStep.setId(step.getId());

        partialUpdatedStep.name(UPDATED_NAME).note(UPDATED_NOTE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedStep.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedStep))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Step in the database
        List<Step> stepList = stepRepository.findAll().collectList().block();
        assertThat(stepList).hasSize(databaseSizeBeforeUpdate);
        Step testStep = stepList.get(stepList.size() - 1);
        assertThat(testStep.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testStep.getNote()).isEqualTo(UPDATED_NOTE);
    }

    @Test
    void patchNonExistingStep() throws Exception {
        int databaseSizeBeforeUpdate = stepRepository.findAll().collectList().block().size();
        step.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, step.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(step))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Step in the database
        List<Step> stepList = stepRepository.findAll().collectList().block();
        assertThat(stepList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchStep() throws Exception {
        int databaseSizeBeforeUpdate = stepRepository.findAll().collectList().block().size();
        step.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(step))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Step in the database
        List<Step> stepList = stepRepository.findAll().collectList().block();
        assertThat(stepList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamStep() throws Exception {
        int databaseSizeBeforeUpdate = stepRepository.findAll().collectList().block().size();
        step.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(step))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Step in the database
        List<Step> stepList = stepRepository.findAll().collectList().block();
        assertThat(stepList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteStep() {
        // Initialize the database
        stepRepository.save(step).block();

        int databaseSizeBeforeDelete = stepRepository.findAll().collectList().block().size();

        // Delete the step
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, step.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Step> stepList = stepRepository.findAll().collectList().block();
        assertThat(stepList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
