package com.hoangdp.gateway.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.hoangdp.gateway.IntegrationTest;
import com.hoangdp.gateway.domain.ConversationsDetails;
import com.hoangdp.gateway.repository.ConversationsDetailsRepository;
import com.hoangdp.gateway.repository.EntityManager;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link ConversationsDetailsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ConversationsDetailsResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_GROUP = false;
    private static final Boolean UPDATED_IS_GROUP = true;

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_ON = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_ON = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_MODIFIED_BY = "AAAAAAAAAA";
    private static final String UPDATED_MODIFIED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_MODIFIED_ON = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_MODIFIED_ON = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/conversations-details";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ConversationsDetailsRepository conversationsDetailsRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private ConversationsDetails conversationsDetails;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ConversationsDetails createEntity(EntityManager em) {
        ConversationsDetails conversationsDetails = new ConversationsDetails()
            .name(DEFAULT_NAME)
            .isGroup(DEFAULT_IS_GROUP)
            .createdBy(DEFAULT_CREATED_BY)
            .createdOn(DEFAULT_CREATED_ON)
            .modifiedBy(DEFAULT_MODIFIED_BY)
            .modifiedOn(DEFAULT_MODIFIED_ON);
        return conversationsDetails;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ConversationsDetails createUpdatedEntity(EntityManager em) {
        ConversationsDetails conversationsDetails = new ConversationsDetails()
            .name(UPDATED_NAME)
            .isGroup(UPDATED_IS_GROUP)
            .createdBy(UPDATED_CREATED_BY)
            .createdOn(UPDATED_CREATED_ON)
            .modifiedBy(UPDATED_MODIFIED_BY)
            .modifiedOn(UPDATED_MODIFIED_ON);
        return conversationsDetails;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(ConversationsDetails.class).block();
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
        conversationsDetails = createEntity(em);
    }

    @Test
    void createConversationsDetails() throws Exception {
        int databaseSizeBeforeCreate = conversationsDetailsRepository.findAll().collectList().block().size();
        // Create the ConversationsDetails
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(conversationsDetails))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the ConversationsDetails in the database
        List<ConversationsDetails> conversationsDetailsList = conversationsDetailsRepository.findAll().collectList().block();
        assertThat(conversationsDetailsList).hasSize(databaseSizeBeforeCreate + 1);
        ConversationsDetails testConversationsDetails = conversationsDetailsList.get(conversationsDetailsList.size() - 1);
        assertThat(testConversationsDetails.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testConversationsDetails.getIsGroup()).isEqualTo(DEFAULT_IS_GROUP);
        assertThat(testConversationsDetails.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testConversationsDetails.getCreatedOn()).isEqualTo(DEFAULT_CREATED_ON);
        assertThat(testConversationsDetails.getModifiedBy()).isEqualTo(DEFAULT_MODIFIED_BY);
        assertThat(testConversationsDetails.getModifiedOn()).isEqualTo(DEFAULT_MODIFIED_ON);
    }

    @Test
    void createConversationsDetailsWithExistingId() throws Exception {
        // Create the ConversationsDetails with an existing ID
        conversationsDetails.setId(1L);

        int databaseSizeBeforeCreate = conversationsDetailsRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(conversationsDetails))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ConversationsDetails in the database
        List<ConversationsDetails> conversationsDetailsList = conversationsDetailsRepository.findAll().collectList().block();
        assertThat(conversationsDetailsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllConversationsDetails() {
        // Initialize the database
        conversationsDetailsRepository.save(conversationsDetails).block();

        // Get all the conversationsDetailsList
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
            .jsonPath("$.[*].id")
            .value(hasItem(conversationsDetails.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].isGroup")
            .value(hasItem(DEFAULT_IS_GROUP.booleanValue()))
            .jsonPath("$.[*].createdBy")
            .value(hasItem(DEFAULT_CREATED_BY))
            .jsonPath("$.[*].createdOn")
            .value(hasItem(DEFAULT_CREATED_ON.toString()))
            .jsonPath("$.[*].modifiedBy")
            .value(hasItem(DEFAULT_MODIFIED_BY))
            .jsonPath("$.[*].modifiedOn")
            .value(hasItem(DEFAULT_MODIFIED_ON.toString()));
    }

    @Test
    void getConversationsDetails() {
        // Initialize the database
        conversationsDetailsRepository.save(conversationsDetails).block();

        // Get the conversationsDetails
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, conversationsDetails.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(conversationsDetails.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.isGroup")
            .value(is(DEFAULT_IS_GROUP.booleanValue()))
            .jsonPath("$.createdBy")
            .value(is(DEFAULT_CREATED_BY))
            .jsonPath("$.createdOn")
            .value(is(DEFAULT_CREATED_ON.toString()))
            .jsonPath("$.modifiedBy")
            .value(is(DEFAULT_MODIFIED_BY))
            .jsonPath("$.modifiedOn")
            .value(is(DEFAULT_MODIFIED_ON.toString()));
    }

    @Test
    void getNonExistingConversationsDetails() {
        // Get the conversationsDetails
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingConversationsDetails() throws Exception {
        // Initialize the database
        conversationsDetailsRepository.save(conversationsDetails).block();

        int databaseSizeBeforeUpdate = conversationsDetailsRepository.findAll().collectList().block().size();

        // Update the conversationsDetails
        ConversationsDetails updatedConversationsDetails = conversationsDetailsRepository.findById(conversationsDetails.getId()).block();
        updatedConversationsDetails
            .name(UPDATED_NAME)
            .isGroup(UPDATED_IS_GROUP)
            .createdBy(UPDATED_CREATED_BY)
            .createdOn(UPDATED_CREATED_ON)
            .modifiedBy(UPDATED_MODIFIED_BY)
            .modifiedOn(UPDATED_MODIFIED_ON);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedConversationsDetails.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedConversationsDetails))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ConversationsDetails in the database
        List<ConversationsDetails> conversationsDetailsList = conversationsDetailsRepository.findAll().collectList().block();
        assertThat(conversationsDetailsList).hasSize(databaseSizeBeforeUpdate);
        ConversationsDetails testConversationsDetails = conversationsDetailsList.get(conversationsDetailsList.size() - 1);
        assertThat(testConversationsDetails.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testConversationsDetails.getIsGroup()).isEqualTo(UPDATED_IS_GROUP);
        assertThat(testConversationsDetails.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testConversationsDetails.getCreatedOn()).isEqualTo(UPDATED_CREATED_ON);
        assertThat(testConversationsDetails.getModifiedBy()).isEqualTo(UPDATED_MODIFIED_BY);
        assertThat(testConversationsDetails.getModifiedOn()).isEqualTo(UPDATED_MODIFIED_ON);
    }

    @Test
    void putNonExistingConversationsDetails() throws Exception {
        int databaseSizeBeforeUpdate = conversationsDetailsRepository.findAll().collectList().block().size();
        conversationsDetails.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, conversationsDetails.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(conversationsDetails))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ConversationsDetails in the database
        List<ConversationsDetails> conversationsDetailsList = conversationsDetailsRepository.findAll().collectList().block();
        assertThat(conversationsDetailsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchConversationsDetails() throws Exception {
        int databaseSizeBeforeUpdate = conversationsDetailsRepository.findAll().collectList().block().size();
        conversationsDetails.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(conversationsDetails))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ConversationsDetails in the database
        List<ConversationsDetails> conversationsDetailsList = conversationsDetailsRepository.findAll().collectList().block();
        assertThat(conversationsDetailsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamConversationsDetails() throws Exception {
        int databaseSizeBeforeUpdate = conversationsDetailsRepository.findAll().collectList().block().size();
        conversationsDetails.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(conversationsDetails))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ConversationsDetails in the database
        List<ConversationsDetails> conversationsDetailsList = conversationsDetailsRepository.findAll().collectList().block();
        assertThat(conversationsDetailsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateConversationsDetailsWithPatch() throws Exception {
        // Initialize the database
        conversationsDetailsRepository.save(conversationsDetails).block();

        int databaseSizeBeforeUpdate = conversationsDetailsRepository.findAll().collectList().block().size();

        // Update the conversationsDetails using partial update
        ConversationsDetails partialUpdatedConversationsDetails = new ConversationsDetails();
        partialUpdatedConversationsDetails.setId(conversationsDetails.getId());

        partialUpdatedConversationsDetails.isGroup(UPDATED_IS_GROUP).createdOn(UPDATED_CREATED_ON).modifiedOn(UPDATED_MODIFIED_ON);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedConversationsDetails.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedConversationsDetails))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ConversationsDetails in the database
        List<ConversationsDetails> conversationsDetailsList = conversationsDetailsRepository.findAll().collectList().block();
        assertThat(conversationsDetailsList).hasSize(databaseSizeBeforeUpdate);
        ConversationsDetails testConversationsDetails = conversationsDetailsList.get(conversationsDetailsList.size() - 1);
        assertThat(testConversationsDetails.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testConversationsDetails.getIsGroup()).isEqualTo(UPDATED_IS_GROUP);
        assertThat(testConversationsDetails.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testConversationsDetails.getCreatedOn()).isEqualTo(UPDATED_CREATED_ON);
        assertThat(testConversationsDetails.getModifiedBy()).isEqualTo(DEFAULT_MODIFIED_BY);
        assertThat(testConversationsDetails.getModifiedOn()).isEqualTo(UPDATED_MODIFIED_ON);
    }

    @Test
    void fullUpdateConversationsDetailsWithPatch() throws Exception {
        // Initialize the database
        conversationsDetailsRepository.save(conversationsDetails).block();

        int databaseSizeBeforeUpdate = conversationsDetailsRepository.findAll().collectList().block().size();

        // Update the conversationsDetails using partial update
        ConversationsDetails partialUpdatedConversationsDetails = new ConversationsDetails();
        partialUpdatedConversationsDetails.setId(conversationsDetails.getId());

        partialUpdatedConversationsDetails
            .name(UPDATED_NAME)
            .isGroup(UPDATED_IS_GROUP)
            .createdBy(UPDATED_CREATED_BY)
            .createdOn(UPDATED_CREATED_ON)
            .modifiedBy(UPDATED_MODIFIED_BY)
            .modifiedOn(UPDATED_MODIFIED_ON);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedConversationsDetails.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedConversationsDetails))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ConversationsDetails in the database
        List<ConversationsDetails> conversationsDetailsList = conversationsDetailsRepository.findAll().collectList().block();
        assertThat(conversationsDetailsList).hasSize(databaseSizeBeforeUpdate);
        ConversationsDetails testConversationsDetails = conversationsDetailsList.get(conversationsDetailsList.size() - 1);
        assertThat(testConversationsDetails.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testConversationsDetails.getIsGroup()).isEqualTo(UPDATED_IS_GROUP);
        assertThat(testConversationsDetails.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testConversationsDetails.getCreatedOn()).isEqualTo(UPDATED_CREATED_ON);
        assertThat(testConversationsDetails.getModifiedBy()).isEqualTo(UPDATED_MODIFIED_BY);
        assertThat(testConversationsDetails.getModifiedOn()).isEqualTo(UPDATED_MODIFIED_ON);
    }

    @Test
    void patchNonExistingConversationsDetails() throws Exception {
        int databaseSizeBeforeUpdate = conversationsDetailsRepository.findAll().collectList().block().size();
        conversationsDetails.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, conversationsDetails.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(conversationsDetails))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ConversationsDetails in the database
        List<ConversationsDetails> conversationsDetailsList = conversationsDetailsRepository.findAll().collectList().block();
        assertThat(conversationsDetailsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchConversationsDetails() throws Exception {
        int databaseSizeBeforeUpdate = conversationsDetailsRepository.findAll().collectList().block().size();
        conversationsDetails.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(conversationsDetails))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ConversationsDetails in the database
        List<ConversationsDetails> conversationsDetailsList = conversationsDetailsRepository.findAll().collectList().block();
        assertThat(conversationsDetailsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamConversationsDetails() throws Exception {
        int databaseSizeBeforeUpdate = conversationsDetailsRepository.findAll().collectList().block().size();
        conversationsDetails.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(conversationsDetails))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ConversationsDetails in the database
        List<ConversationsDetails> conversationsDetailsList = conversationsDetailsRepository.findAll().collectList().block();
        assertThat(conversationsDetailsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteConversationsDetails() {
        // Initialize the database
        conversationsDetailsRepository.save(conversationsDetails).block();

        int databaseSizeBeforeDelete = conversationsDetailsRepository.findAll().collectList().block().size();

        // Delete the conversationsDetails
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, conversationsDetails.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<ConversationsDetails> conversationsDetailsList = conversationsDetailsRepository.findAll().collectList().block();
        assertThat(conversationsDetailsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
