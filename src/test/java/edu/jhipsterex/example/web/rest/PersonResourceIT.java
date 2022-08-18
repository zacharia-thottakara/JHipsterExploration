package edu.jhipsterex.example.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import edu.jhipsterex.example.IntegrationTest;
import edu.jhipsterex.example.domain.Person;
import edu.jhipsterex.example.repository.PersonRepository;
import edu.jhipsterex.example.service.PersonService;
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
 * Integration tests for the {@link PersonResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class PersonResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "5@?A#9j.i\\XV";
    private static final String UPDATED_EMAIL = "J#S@b.g)zG";

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/people";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private PersonRepository personRepository;

    @Mock
    private PersonRepository personRepositoryMock;

    @Mock
    private PersonService personServiceMock;

    @Autowired
    private WebTestClient webTestClient;

    private Person person;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Person createEntity() {
        Person person = new Person().name(DEFAULT_NAME).email(DEFAULT_EMAIL).note(DEFAULT_NOTE);
        return person;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Person createUpdatedEntity() {
        Person person = new Person().name(UPDATED_NAME).email(UPDATED_EMAIL).note(UPDATED_NOTE);
        return person;
    }

    @BeforeEach
    public void setupCsrf() {
        webTestClient = webTestClient.mutateWith(csrf());
    }

    @BeforeEach
    public void initTest() {
        personRepository.deleteAll().block();
        person = createEntity();
    }

    @Test
    void createPerson() throws Exception {
        int databaseSizeBeforeCreate = personRepository.findAll().collectList().block().size();
        // Create the Person
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(person))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Person in the database
        List<Person> personList = personRepository.findAll().collectList().block();
        assertThat(personList).hasSize(databaseSizeBeforeCreate + 1);
        Person testPerson = personList.get(personList.size() - 1);
        assertThat(testPerson.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testPerson.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testPerson.getNote()).isEqualTo(DEFAULT_NOTE);
    }

    @Test
    void createPersonWithExistingId() throws Exception {
        // Create the Person with an existing ID
        person.setId("existing_id");

        int databaseSizeBeforeCreate = personRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(person))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Person in the database
        List<Person> personList = personRepository.findAll().collectList().block();
        assertThat(personList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = personRepository.findAll().collectList().block().size();
        // set the field null
        person.setName(null);

        // Create the Person, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(person))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Person> personList = personRepository.findAll().collectList().block();
        assertThat(personList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkEmailIsRequired() throws Exception {
        int databaseSizeBeforeTest = personRepository.findAll().collectList().block().size();
        // set the field null
        person.setEmail(null);

        // Create the Person, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(person))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Person> personList = personRepository.findAll().collectList().block();
        assertThat(personList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllPeople() {
        // Initialize the database
        personRepository.save(person).block();

        // Get all the personList
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
            .jsonPath("$.[*].email")
            .value(hasItem(DEFAULT_EMAIL))
            .jsonPath("$.[*].note")
            .value(hasItem(DEFAULT_NOTE));
    }

    @Test
    void getPerson() {
        // Initialize the database
        personRepository.save(person).block();

        // Get the person
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, person.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.email")
            .value(is(DEFAULT_EMAIL))
            .jsonPath("$.note")
            .value(is(DEFAULT_NOTE));
    }

    @Test
    void getNonExistingPerson() {
        // Get the person
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewPerson() throws Exception {
        // Initialize the database
        personRepository.save(person).block();

        int databaseSizeBeforeUpdate = personRepository.findAll().collectList().block().size();

        // Update the person
        Person updatedPerson = personRepository.findById(person.getId()).block();
        updatedPerson.name(UPDATED_NAME).email(UPDATED_EMAIL).note(UPDATED_NOTE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedPerson.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedPerson))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Person in the database
        List<Person> personList = personRepository.findAll().collectList().block();
        assertThat(personList).hasSize(databaseSizeBeforeUpdate);
        Person testPerson = personList.get(personList.size() - 1);
        assertThat(testPerson.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPerson.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testPerson.getNote()).isEqualTo(UPDATED_NOTE);
    }

    @Test
    void putNonExistingPerson() throws Exception {
        int databaseSizeBeforeUpdate = personRepository.findAll().collectList().block().size();
        person.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, person.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(person))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Person in the database
        List<Person> personList = personRepository.findAll().collectList().block();
        assertThat(personList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchPerson() throws Exception {
        int databaseSizeBeforeUpdate = personRepository.findAll().collectList().block().size();
        person.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(person))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Person in the database
        List<Person> personList = personRepository.findAll().collectList().block();
        assertThat(personList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamPerson() throws Exception {
        int databaseSizeBeforeUpdate = personRepository.findAll().collectList().block().size();
        person.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(person))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Person in the database
        List<Person> personList = personRepository.findAll().collectList().block();
        assertThat(personList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdatePersonWithPatch() throws Exception {
        // Initialize the database
        personRepository.save(person).block();

        int databaseSizeBeforeUpdate = personRepository.findAll().collectList().block().size();

        // Update the person using partial update
        Person partialUpdatedPerson = new Person();
        partialUpdatedPerson.setId(person.getId());

        partialUpdatedPerson.email(UPDATED_EMAIL).note(UPDATED_NOTE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPerson.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPerson))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Person in the database
        List<Person> personList = personRepository.findAll().collectList().block();
        assertThat(personList).hasSize(databaseSizeBeforeUpdate);
        Person testPerson = personList.get(personList.size() - 1);
        assertThat(testPerson.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testPerson.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testPerson.getNote()).isEqualTo(UPDATED_NOTE);
    }

    @Test
    void fullUpdatePersonWithPatch() throws Exception {
        // Initialize the database
        personRepository.save(person).block();

        int databaseSizeBeforeUpdate = personRepository.findAll().collectList().block().size();

        // Update the person using partial update
        Person partialUpdatedPerson = new Person();
        partialUpdatedPerson.setId(person.getId());

        partialUpdatedPerson.name(UPDATED_NAME).email(UPDATED_EMAIL).note(UPDATED_NOTE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPerson.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPerson))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Person in the database
        List<Person> personList = personRepository.findAll().collectList().block();
        assertThat(personList).hasSize(databaseSizeBeforeUpdate);
        Person testPerson = personList.get(personList.size() - 1);
        assertThat(testPerson.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPerson.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testPerson.getNote()).isEqualTo(UPDATED_NOTE);
    }

    @Test
    void patchNonExistingPerson() throws Exception {
        int databaseSizeBeforeUpdate = personRepository.findAll().collectList().block().size();
        person.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, person.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(person))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Person in the database
        List<Person> personList = personRepository.findAll().collectList().block();
        assertThat(personList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchPerson() throws Exception {
        int databaseSizeBeforeUpdate = personRepository.findAll().collectList().block().size();
        person.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(person))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Person in the database
        List<Person> personList = personRepository.findAll().collectList().block();
        assertThat(personList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamPerson() throws Exception {
        int databaseSizeBeforeUpdate = personRepository.findAll().collectList().block().size();
        person.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(person))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Person in the database
        List<Person> personList = personRepository.findAll().collectList().block();
        assertThat(personList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deletePerson() {
        // Initialize the database
        personRepository.save(person).block();

        int databaseSizeBeforeDelete = personRepository.findAll().collectList().block().size();

        // Delete the person
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, person.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Person> personList = personRepository.findAll().collectList().block();
        assertThat(personList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
