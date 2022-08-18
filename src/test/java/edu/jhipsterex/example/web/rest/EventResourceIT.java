package edu.jhipsterex.example.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import edu.jhipsterex.example.IntegrationTest;
import edu.jhipsterex.example.domain.Event;
import edu.jhipsterex.example.repository.EventRepository;
import edu.jhipsterex.example.service.EventService;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link EventResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class EventResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_INITIAL = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_INITIAL = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_REPEAT = "AAAAAAAAAA";
    private static final String UPDATED_REPEAT = "BBBBBBBBBB";

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/events";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private EventRepository eventRepository;

    @Mock
    private EventRepository eventRepositoryMock;

    @Mock
    private EventService eventServiceMock;

    @Autowired
    private WebTestClient webTestClient;

    private Event event;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Event createEntity() {
        Event event = new Event().name(DEFAULT_NAME).initial(DEFAULT_INITIAL).repeat(DEFAULT_REPEAT).note(DEFAULT_NOTE);
        return event;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Event createUpdatedEntity() {
        Event event = new Event().name(UPDATED_NAME).initial(UPDATED_INITIAL).repeat(UPDATED_REPEAT).note(UPDATED_NOTE);
        return event;
    }

    @BeforeEach
    public void setupCsrf() {
        webTestClient = webTestClient.mutateWith(csrf());
    }

    @BeforeEach
    public void initTest() {
        eventRepository.deleteAll().block();
        event = createEntity();
    }

    @Test
    void createEvent() throws Exception {
        int databaseSizeBeforeCreate = eventRepository.findAll().collectList().block().size();
        // Create the Event
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(event))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll().collectList().block();
        assertThat(eventList).hasSize(databaseSizeBeforeCreate + 1);
        Event testEvent = eventList.get(eventList.size() - 1);
        assertThat(testEvent.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testEvent.getInitial()).isEqualTo(DEFAULT_INITIAL);
        assertThat(testEvent.getRepeat()).isEqualTo(DEFAULT_REPEAT);
        assertThat(testEvent.getNote()).isEqualTo(DEFAULT_NOTE);
    }

    @Test
    void createEventWithExistingId() throws Exception {
        // Create the Event with an existing ID
        event.setId("existing_id");

        int databaseSizeBeforeCreate = eventRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(event))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll().collectList().block();
        assertThat(eventList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = eventRepository.findAll().collectList().block().size();
        // set the field null
        event.setName(null);

        // Create the Event, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(event))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Event> eventList = eventRepository.findAll().collectList().block();
        assertThat(eventList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkInitialIsRequired() throws Exception {
        int databaseSizeBeforeTest = eventRepository.findAll().collectList().block().size();
        // set the field null
        event.setInitial(null);

        // Create the Event, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(event))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Event> eventList = eventRepository.findAll().collectList().block();
        assertThat(eventList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllEvents() {
        // Initialize the database
        eventRepository.save(event).block();

        // Get all the eventList
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
            .jsonPath("$.[*].initial")
            .value(hasItem(DEFAULT_INITIAL.toString()))
            .jsonPath("$.[*].repeat")
            .value(hasItem(DEFAULT_REPEAT))
            .jsonPath("$.[*].note")
            .value(hasItem(DEFAULT_NOTE));
    }

    @Test
    void getEvent() {
        // Initialize the database
        eventRepository.save(event).block();

        // Get the event
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, event.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.initial")
            .value(is(DEFAULT_INITIAL.toString()))
            .jsonPath("$.repeat")
            .value(is(DEFAULT_REPEAT))
            .jsonPath("$.note")
            .value(is(DEFAULT_NOTE));
    }

    @Test
    void getNonExistingEvent() {
        // Get the event
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewEvent() throws Exception {
        // Initialize the database
        eventRepository.save(event).block();

        int databaseSizeBeforeUpdate = eventRepository.findAll().collectList().block().size();

        // Update the event
        Event updatedEvent = eventRepository.findById(event.getId()).block();
        updatedEvent.name(UPDATED_NAME).initial(UPDATED_INITIAL).repeat(UPDATED_REPEAT).note(UPDATED_NOTE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedEvent.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedEvent))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll().collectList().block();
        assertThat(eventList).hasSize(databaseSizeBeforeUpdate);
        Event testEvent = eventList.get(eventList.size() - 1);
        assertThat(testEvent.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testEvent.getInitial()).isEqualTo(UPDATED_INITIAL);
        assertThat(testEvent.getRepeat()).isEqualTo(UPDATED_REPEAT);
        assertThat(testEvent.getNote()).isEqualTo(UPDATED_NOTE);
    }

    @Test
    void putNonExistingEvent() throws Exception {
        int databaseSizeBeforeUpdate = eventRepository.findAll().collectList().block().size();
        event.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, event.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(event))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll().collectList().block();
        assertThat(eventList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchEvent() throws Exception {
        int databaseSizeBeforeUpdate = eventRepository.findAll().collectList().block().size();
        event.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(event))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll().collectList().block();
        assertThat(eventList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamEvent() throws Exception {
        int databaseSizeBeforeUpdate = eventRepository.findAll().collectList().block().size();
        event.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(event))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll().collectList().block();
        assertThat(eventList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateEventWithPatch() throws Exception {
        // Initialize the database
        eventRepository.save(event).block();

        int databaseSizeBeforeUpdate = eventRepository.findAll().collectList().block().size();

        // Update the event using partial update
        Event partialUpdatedEvent = new Event();
        partialUpdatedEvent.setId(event.getId());

        partialUpdatedEvent.initial(UPDATED_INITIAL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEvent.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedEvent))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll().collectList().block();
        assertThat(eventList).hasSize(databaseSizeBeforeUpdate);
        Event testEvent = eventList.get(eventList.size() - 1);
        assertThat(testEvent.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testEvent.getInitial()).isEqualTo(UPDATED_INITIAL);
        assertThat(testEvent.getRepeat()).isEqualTo(DEFAULT_REPEAT);
        assertThat(testEvent.getNote()).isEqualTo(DEFAULT_NOTE);
    }

    @Test
    void fullUpdateEventWithPatch() throws Exception {
        // Initialize the database
        eventRepository.save(event).block();

        int databaseSizeBeforeUpdate = eventRepository.findAll().collectList().block().size();

        // Update the event using partial update
        Event partialUpdatedEvent = new Event();
        partialUpdatedEvent.setId(event.getId());

        partialUpdatedEvent.name(UPDATED_NAME).initial(UPDATED_INITIAL).repeat(UPDATED_REPEAT).note(UPDATED_NOTE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEvent.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedEvent))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll().collectList().block();
        assertThat(eventList).hasSize(databaseSizeBeforeUpdate);
        Event testEvent = eventList.get(eventList.size() - 1);
        assertThat(testEvent.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testEvent.getInitial()).isEqualTo(UPDATED_INITIAL);
        assertThat(testEvent.getRepeat()).isEqualTo(UPDATED_REPEAT);
        assertThat(testEvent.getNote()).isEqualTo(UPDATED_NOTE);
    }

    @Test
    void patchNonExistingEvent() throws Exception {
        int databaseSizeBeforeUpdate = eventRepository.findAll().collectList().block().size();
        event.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, event.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(event))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll().collectList().block();
        assertThat(eventList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchEvent() throws Exception {
        int databaseSizeBeforeUpdate = eventRepository.findAll().collectList().block().size();
        event.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(event))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll().collectList().block();
        assertThat(eventList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamEvent() throws Exception {
        int databaseSizeBeforeUpdate = eventRepository.findAll().collectList().block().size();
        event.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(event))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll().collectList().block();
        assertThat(eventList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteEvent() {
        // Initialize the database
        eventRepository.save(event).block();

        int databaseSizeBeforeDelete = eventRepository.findAll().collectList().block().size();

        // Delete the event
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, event.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Event> eventList = eventRepository.findAll().collectList().block();
        assertThat(eventList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
