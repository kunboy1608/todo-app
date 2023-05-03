package com.hoangdp.gateway.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.hoangdp.gateway.IntegrationTest;
import com.hoangdp.gateway.domain.Events;
import com.hoangdp.gateway.repository.EntityManager;
import com.hoangdp.gateway.repository.EventsRepository;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link EventsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class EventsResourceIT {

    private static final Long DEFAULT_OWNER = 1L;
    private static final Long UPDATED_OWNER = 2L;

    private static final Integer DEFAULT_KIND = 1;
    private static final Integer UPDATED_KIND = 2;

    private static final String DEFAULT_DATE = "AAAAAAAAAA";
    private static final String UPDATED_DATE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_LUNAR = false;
    private static final Boolean UPDATED_IS_LUNAR = true;

    private static final String ENTITY_API_URL = "/api/events";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{eventId}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private EventsRepository eventsRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Events events;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Events createEntity(EntityManager em) {
        Events events = new Events().owner(DEFAULT_OWNER).kind(DEFAULT_KIND).date(DEFAULT_DATE).isLunar(DEFAULT_IS_LUNAR);
        return events;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Events createUpdatedEntity(EntityManager em) {
        Events events = new Events().owner(UPDATED_OWNER).kind(UPDATED_KIND).date(UPDATED_DATE).isLunar(UPDATED_IS_LUNAR);
        return events;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Events.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        events = createEntity(em);
    }

    @Test
    void createEvents() throws Exception {
        int databaseSizeBeforeCreate = eventsRepository.findAll().collectList().block().size();
        // Create the Events
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(events))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Events in the database
        List<Events> eventsList = eventsRepository.findAll().collectList().block();
        assertThat(eventsList).hasSize(databaseSizeBeforeCreate + 1);
        Events testEvents = eventsList.get(eventsList.size() - 1);
        assertThat(testEvents.getOwner()).isEqualTo(DEFAULT_OWNER);
        assertThat(testEvents.getKind()).isEqualTo(DEFAULT_KIND);
        assertThat(testEvents.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testEvents.getIsLunar()).isEqualTo(DEFAULT_IS_LUNAR);
    }

    @Test
    void createEventsWithExistingId() throws Exception {
        // Create the Events with an existing ID
        events.setEventId(1L);

        int databaseSizeBeforeCreate = eventsRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(events))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Events in the database
        List<Events> eventsList = eventsRepository.findAll().collectList().block();
        assertThat(eventsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllEvents() {
        // Initialize the database
        eventsRepository.save(events).block();

        // Get all the eventsList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=eventId,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].eventId")
            .value(hasItem(events.getEventId().intValue()))
            .jsonPath("$.[*].owner")
            .value(hasItem(DEFAULT_OWNER.intValue()))
            .jsonPath("$.[*].kind")
            .value(hasItem(DEFAULT_KIND))
            .jsonPath("$.[*].date")
            .value(hasItem(DEFAULT_DATE))
            .jsonPath("$.[*].isLunar")
            .value(hasItem(DEFAULT_IS_LUNAR.booleanValue()));
    }

    @Test
    void getEvents() {
        // Initialize the database
        eventsRepository.save(events).block();

        // Get the events
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, events.getEventId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.eventId")
            .value(is(events.getEventId().intValue()))
            .jsonPath("$.owner")
            .value(is(DEFAULT_OWNER.intValue()))
            .jsonPath("$.kind")
            .value(is(DEFAULT_KIND))
            .jsonPath("$.date")
            .value(is(DEFAULT_DATE))
            .jsonPath("$.isLunar")
            .value(is(DEFAULT_IS_LUNAR.booleanValue()));
    }

    @Test
    void getNonExistingEvents() {
        // Get the events
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingEvents() throws Exception {
        // Initialize the database
        eventsRepository.save(events).block();

        int databaseSizeBeforeUpdate = eventsRepository.findAll().collectList().block().size();

        // Update the events
        Events updatedEvents = eventsRepository.findById(events.getEventId()).block();
        updatedEvents.owner(UPDATED_OWNER).kind(UPDATED_KIND).date(UPDATED_DATE).isLunar(UPDATED_IS_LUNAR);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedEvents.getEventId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedEvents))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Events in the database
        List<Events> eventsList = eventsRepository.findAll().collectList().block();
        assertThat(eventsList).hasSize(databaseSizeBeforeUpdate);
        Events testEvents = eventsList.get(eventsList.size() - 1);
        assertThat(testEvents.getOwner()).isEqualTo(UPDATED_OWNER);
        assertThat(testEvents.getKind()).isEqualTo(UPDATED_KIND);
        assertThat(testEvents.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testEvents.getIsLunar()).isEqualTo(UPDATED_IS_LUNAR);
    }

    @Test
    void putNonExistingEvents() throws Exception {
        int databaseSizeBeforeUpdate = eventsRepository.findAll().collectList().block().size();
        events.setEventId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, events.getEventId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(events))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Events in the database
        List<Events> eventsList = eventsRepository.findAll().collectList().block();
        assertThat(eventsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchEvents() throws Exception {
        int databaseSizeBeforeUpdate = eventsRepository.findAll().collectList().block().size();
        events.setEventId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(events))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Events in the database
        List<Events> eventsList = eventsRepository.findAll().collectList().block();
        assertThat(eventsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamEvents() throws Exception {
        int databaseSizeBeforeUpdate = eventsRepository.findAll().collectList().block().size();
        events.setEventId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(events))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Events in the database
        List<Events> eventsList = eventsRepository.findAll().collectList().block();
        assertThat(eventsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateEventsWithPatch() throws Exception {
        // Initialize the database
        eventsRepository.save(events).block();

        int databaseSizeBeforeUpdate = eventsRepository.findAll().collectList().block().size();

        // Update the events using partial update
        Events partialUpdatedEvents = new Events();
        partialUpdatedEvents.setEventId(events.getEventId());

        partialUpdatedEvents.owner(UPDATED_OWNER).kind(UPDATED_KIND).isLunar(UPDATED_IS_LUNAR);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEvents.getEventId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedEvents))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Events in the database
        List<Events> eventsList = eventsRepository.findAll().collectList().block();
        assertThat(eventsList).hasSize(databaseSizeBeforeUpdate);
        Events testEvents = eventsList.get(eventsList.size() - 1);
        assertThat(testEvents.getOwner()).isEqualTo(UPDATED_OWNER);
        assertThat(testEvents.getKind()).isEqualTo(UPDATED_KIND);
        assertThat(testEvents.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testEvents.getIsLunar()).isEqualTo(UPDATED_IS_LUNAR);
    }

    @Test
    void fullUpdateEventsWithPatch() throws Exception {
        // Initialize the database
        eventsRepository.save(events).block();

        int databaseSizeBeforeUpdate = eventsRepository.findAll().collectList().block().size();

        // Update the events using partial update
        Events partialUpdatedEvents = new Events();
        partialUpdatedEvents.setEventId(events.getEventId());

        partialUpdatedEvents.owner(UPDATED_OWNER).kind(UPDATED_KIND).date(UPDATED_DATE).isLunar(UPDATED_IS_LUNAR);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEvents.getEventId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedEvents))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Events in the database
        List<Events> eventsList = eventsRepository.findAll().collectList().block();
        assertThat(eventsList).hasSize(databaseSizeBeforeUpdate);
        Events testEvents = eventsList.get(eventsList.size() - 1);
        assertThat(testEvents.getOwner()).isEqualTo(UPDATED_OWNER);
        assertThat(testEvents.getKind()).isEqualTo(UPDATED_KIND);
        assertThat(testEvents.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testEvents.getIsLunar()).isEqualTo(UPDATED_IS_LUNAR);
    }

    @Test
    void patchNonExistingEvents() throws Exception {
        int databaseSizeBeforeUpdate = eventsRepository.findAll().collectList().block().size();
        events.setEventId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, events.getEventId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(events))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Events in the database
        List<Events> eventsList = eventsRepository.findAll().collectList().block();
        assertThat(eventsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchEvents() throws Exception {
        int databaseSizeBeforeUpdate = eventsRepository.findAll().collectList().block().size();
        events.setEventId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(events))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Events in the database
        List<Events> eventsList = eventsRepository.findAll().collectList().block();
        assertThat(eventsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamEvents() throws Exception {
        int databaseSizeBeforeUpdate = eventsRepository.findAll().collectList().block().size();
        events.setEventId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(events))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Events in the database
        List<Events> eventsList = eventsRepository.findAll().collectList().block();
        assertThat(eventsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteEvents() {
        // Initialize the database
        eventsRepository.save(events).block();

        int databaseSizeBeforeDelete = eventsRepository.findAll().collectList().block().size();

        // Delete the events
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, events.getEventId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Events> eventsList = eventsRepository.findAll().collectList().block();
        assertThat(eventsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
