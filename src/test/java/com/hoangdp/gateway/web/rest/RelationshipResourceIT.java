package com.hoangdp.gateway.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.hoangdp.gateway.IntegrationTest;
import com.hoangdp.gateway.domain.Relationship;
import com.hoangdp.gateway.repository.EntityManager;
import com.hoangdp.gateway.repository.RelationshipRepository;
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
 * Integration tests for the {@link RelationshipResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class RelationshipResourceIT {

    private static final Long DEFAULT_OWNER = 1L;
    private static final Long UPDATED_OWNER = 2L;

    private static final Long DEFAULT_PARTNER = 1L;
    private static final Long UPDATED_PARTNER = 2L;

    private static final Integer DEFAULT_STATUS = 1;
    private static final Integer UPDATED_STATUS = 2;

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_ON = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_ON = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_MODIFIED_BY = "AAAAAAAAAA";
    private static final String UPDATED_MODIFIED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_MODIFIED_ON = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_MODIFIED_ON = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/relationships";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{relationshipId}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RelationshipRepository relationshipRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Relationship relationship;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Relationship createEntity(EntityManager em) {
        Relationship relationship = new Relationship()
            .owner(DEFAULT_OWNER)
            .partner(DEFAULT_PARTNER)
            .status(DEFAULT_STATUS)
            .createdBy(DEFAULT_CREATED_BY)
            .createdOn(DEFAULT_CREATED_ON)
            .modifiedBy(DEFAULT_MODIFIED_BY)
            .modifiedOn(DEFAULT_MODIFIED_ON);
        return relationship;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Relationship createUpdatedEntity(EntityManager em) {
        Relationship relationship = new Relationship()
            .owner(UPDATED_OWNER)
            .partner(UPDATED_PARTNER)
            .status(UPDATED_STATUS)
            .createdBy(UPDATED_CREATED_BY)
            .createdOn(UPDATED_CREATED_ON)
            .modifiedBy(UPDATED_MODIFIED_BY)
            .modifiedOn(UPDATED_MODIFIED_ON);
        return relationship;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Relationship.class).block();
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
        relationship = createEntity(em);
    }

    @Test
    void createRelationship() throws Exception {
        int databaseSizeBeforeCreate = relationshipRepository.findAll().collectList().block().size();
        // Create the Relationship
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(relationship))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Relationship in the database
        List<Relationship> relationshipList = relationshipRepository.findAll().collectList().block();
        assertThat(relationshipList).hasSize(databaseSizeBeforeCreate + 1);
        Relationship testRelationship = relationshipList.get(relationshipList.size() - 1);
        assertThat(testRelationship.getOwner()).isEqualTo(DEFAULT_OWNER);
        assertThat(testRelationship.getPartner()).isEqualTo(DEFAULT_PARTNER);
        assertThat(testRelationship.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testRelationship.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testRelationship.getCreatedOn()).isEqualTo(DEFAULT_CREATED_ON);
        assertThat(testRelationship.getModifiedBy()).isEqualTo(DEFAULT_MODIFIED_BY);
        assertThat(testRelationship.getModifiedOn()).isEqualTo(DEFAULT_MODIFIED_ON);
    }

    @Test
    void createRelationshipWithExistingId() throws Exception {
        // Create the Relationship with an existing ID
        relationship.setRelationshipId(1L);

        int databaseSizeBeforeCreate = relationshipRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(relationship))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Relationship in the database
        List<Relationship> relationshipList = relationshipRepository.findAll().collectList().block();
        assertThat(relationshipList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllRelationships() {
        // Initialize the database
        relationshipRepository.save(relationship).block();

        // Get all the relationshipList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=relationshipId,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].relationshipId")
            .value(hasItem(relationship.getRelationshipId().intValue()))
            .jsonPath("$.[*].owner")
            .value(hasItem(DEFAULT_OWNER.intValue()))
            .jsonPath("$.[*].partner")
            .value(hasItem(DEFAULT_PARTNER.intValue()))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS))
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
    void getRelationship() {
        // Initialize the database
        relationshipRepository.save(relationship).block();

        // Get the relationship
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, relationship.getRelationshipId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.relationshipId")
            .value(is(relationship.getRelationshipId().intValue()))
            .jsonPath("$.owner")
            .value(is(DEFAULT_OWNER.intValue()))
            .jsonPath("$.partner")
            .value(is(DEFAULT_PARTNER.intValue()))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS))
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
    void getNonExistingRelationship() {
        // Get the relationship
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingRelationship() throws Exception {
        // Initialize the database
        relationshipRepository.save(relationship).block();

        int databaseSizeBeforeUpdate = relationshipRepository.findAll().collectList().block().size();

        // Update the relationship
        Relationship updatedRelationship = relationshipRepository.findById(relationship.getRelationshipId()).block();
        updatedRelationship
            .owner(UPDATED_OWNER)
            .partner(UPDATED_PARTNER)
            .status(UPDATED_STATUS)
            .createdBy(UPDATED_CREATED_BY)
            .createdOn(UPDATED_CREATED_ON)
            .modifiedBy(UPDATED_MODIFIED_BY)
            .modifiedOn(UPDATED_MODIFIED_ON);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedRelationship.getRelationshipId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedRelationship))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Relationship in the database
        List<Relationship> relationshipList = relationshipRepository.findAll().collectList().block();
        assertThat(relationshipList).hasSize(databaseSizeBeforeUpdate);
        Relationship testRelationship = relationshipList.get(relationshipList.size() - 1);
        assertThat(testRelationship.getOwner()).isEqualTo(UPDATED_OWNER);
        assertThat(testRelationship.getPartner()).isEqualTo(UPDATED_PARTNER);
        assertThat(testRelationship.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testRelationship.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testRelationship.getCreatedOn()).isEqualTo(UPDATED_CREATED_ON);
        assertThat(testRelationship.getModifiedBy()).isEqualTo(UPDATED_MODIFIED_BY);
        assertThat(testRelationship.getModifiedOn()).isEqualTo(UPDATED_MODIFIED_ON);
    }

    @Test
    void putNonExistingRelationship() throws Exception {
        int databaseSizeBeforeUpdate = relationshipRepository.findAll().collectList().block().size();
        relationship.setRelationshipId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, relationship.getRelationshipId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(relationship))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Relationship in the database
        List<Relationship> relationshipList = relationshipRepository.findAll().collectList().block();
        assertThat(relationshipList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchRelationship() throws Exception {
        int databaseSizeBeforeUpdate = relationshipRepository.findAll().collectList().block().size();
        relationship.setRelationshipId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(relationship))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Relationship in the database
        List<Relationship> relationshipList = relationshipRepository.findAll().collectList().block();
        assertThat(relationshipList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamRelationship() throws Exception {
        int databaseSizeBeforeUpdate = relationshipRepository.findAll().collectList().block().size();
        relationship.setRelationshipId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(relationship))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Relationship in the database
        List<Relationship> relationshipList = relationshipRepository.findAll().collectList().block();
        assertThat(relationshipList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateRelationshipWithPatch() throws Exception {
        // Initialize the database
        relationshipRepository.save(relationship).block();

        int databaseSizeBeforeUpdate = relationshipRepository.findAll().collectList().block().size();

        // Update the relationship using partial update
        Relationship partialUpdatedRelationship = new Relationship();
        partialUpdatedRelationship.setRelationshipId(relationship.getRelationshipId());

        partialUpdatedRelationship
            .owner(UPDATED_OWNER)
            .status(UPDATED_STATUS)
            .createdBy(UPDATED_CREATED_BY)
            .createdOn(UPDATED_CREATED_ON)
            .modifiedBy(UPDATED_MODIFIED_BY)
            .modifiedOn(UPDATED_MODIFIED_ON);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRelationship.getRelationshipId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedRelationship))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Relationship in the database
        List<Relationship> relationshipList = relationshipRepository.findAll().collectList().block();
        assertThat(relationshipList).hasSize(databaseSizeBeforeUpdate);
        Relationship testRelationship = relationshipList.get(relationshipList.size() - 1);
        assertThat(testRelationship.getOwner()).isEqualTo(UPDATED_OWNER);
        assertThat(testRelationship.getPartner()).isEqualTo(DEFAULT_PARTNER);
        assertThat(testRelationship.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testRelationship.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testRelationship.getCreatedOn()).isEqualTo(UPDATED_CREATED_ON);
        assertThat(testRelationship.getModifiedBy()).isEqualTo(UPDATED_MODIFIED_BY);
        assertThat(testRelationship.getModifiedOn()).isEqualTo(UPDATED_MODIFIED_ON);
    }

    @Test
    void fullUpdateRelationshipWithPatch() throws Exception {
        // Initialize the database
        relationshipRepository.save(relationship).block();

        int databaseSizeBeforeUpdate = relationshipRepository.findAll().collectList().block().size();

        // Update the relationship using partial update
        Relationship partialUpdatedRelationship = new Relationship();
        partialUpdatedRelationship.setRelationshipId(relationship.getRelationshipId());

        partialUpdatedRelationship
            .owner(UPDATED_OWNER)
            .partner(UPDATED_PARTNER)
            .status(UPDATED_STATUS)
            .createdBy(UPDATED_CREATED_BY)
            .createdOn(UPDATED_CREATED_ON)
            .modifiedBy(UPDATED_MODIFIED_BY)
            .modifiedOn(UPDATED_MODIFIED_ON);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRelationship.getRelationshipId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedRelationship))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Relationship in the database
        List<Relationship> relationshipList = relationshipRepository.findAll().collectList().block();
        assertThat(relationshipList).hasSize(databaseSizeBeforeUpdate);
        Relationship testRelationship = relationshipList.get(relationshipList.size() - 1);
        assertThat(testRelationship.getOwner()).isEqualTo(UPDATED_OWNER);
        assertThat(testRelationship.getPartner()).isEqualTo(UPDATED_PARTNER);
        assertThat(testRelationship.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testRelationship.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testRelationship.getCreatedOn()).isEqualTo(UPDATED_CREATED_ON);
        assertThat(testRelationship.getModifiedBy()).isEqualTo(UPDATED_MODIFIED_BY);
        assertThat(testRelationship.getModifiedOn()).isEqualTo(UPDATED_MODIFIED_ON);
    }

    @Test
    void patchNonExistingRelationship() throws Exception {
        int databaseSizeBeforeUpdate = relationshipRepository.findAll().collectList().block().size();
        relationship.setRelationshipId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, relationship.getRelationshipId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(relationship))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Relationship in the database
        List<Relationship> relationshipList = relationshipRepository.findAll().collectList().block();
        assertThat(relationshipList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchRelationship() throws Exception {
        int databaseSizeBeforeUpdate = relationshipRepository.findAll().collectList().block().size();
        relationship.setRelationshipId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(relationship))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Relationship in the database
        List<Relationship> relationshipList = relationshipRepository.findAll().collectList().block();
        assertThat(relationshipList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamRelationship() throws Exception {
        int databaseSizeBeforeUpdate = relationshipRepository.findAll().collectList().block().size();
        relationship.setRelationshipId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(relationship))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Relationship in the database
        List<Relationship> relationshipList = relationshipRepository.findAll().collectList().block();
        assertThat(relationshipList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteRelationship() {
        // Initialize the database
        relationshipRepository.save(relationship).block();

        int databaseSizeBeforeDelete = relationshipRepository.findAll().collectList().block().size();

        // Delete the relationship
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, relationship.getRelationshipId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Relationship> relationshipList = relationshipRepository.findAll().collectList().block();
        assertThat(relationshipList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
