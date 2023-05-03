package com.hoangdp.gateway.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.hoangdp.gateway.IntegrationTest;
import com.hoangdp.gateway.domain.Types;
import com.hoangdp.gateway.repository.EntityManager;
import com.hoangdp.gateway.repository.TypesRepository;
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
 * Integration tests for the {@link TypesResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TypesResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Long DEFAULT_OWNER = 1L;
    private static final Long UPDATED_OWNER = 2L;

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_ON = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_ON = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_MODIFIED_BY = "AAAAAAAAAA";
    private static final String UPDATED_MODIFIED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_MODIFIED_ON = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_MODIFIED_ON = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/types";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{typeId}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TypesRepository typesRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Types types;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Types createEntity(EntityManager em) {
        Types types = new Types()
            .name(DEFAULT_NAME)
            .owner(DEFAULT_OWNER)
            .createdBy(DEFAULT_CREATED_BY)
            .createdOn(DEFAULT_CREATED_ON)
            .modifiedBy(DEFAULT_MODIFIED_BY)
            .modifiedOn(DEFAULT_MODIFIED_ON);
        return types;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Types createUpdatedEntity(EntityManager em) {
        Types types = new Types()
            .name(UPDATED_NAME)
            .owner(UPDATED_OWNER)
            .createdBy(UPDATED_CREATED_BY)
            .createdOn(UPDATED_CREATED_ON)
            .modifiedBy(UPDATED_MODIFIED_BY)
            .modifiedOn(UPDATED_MODIFIED_ON);
        return types;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Types.class).block();
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
        types = createEntity(em);
    }

    @Test
    void createTypes() throws Exception {
        int databaseSizeBeforeCreate = typesRepository.findAll().collectList().block().size();
        // Create the Types
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(types))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Types in the database
        List<Types> typesList = typesRepository.findAll().collectList().block();
        assertThat(typesList).hasSize(databaseSizeBeforeCreate + 1);
        Types testTypes = typesList.get(typesList.size() - 1);
        assertThat(testTypes.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTypes.getOwner()).isEqualTo(DEFAULT_OWNER);
        assertThat(testTypes.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testTypes.getCreatedOn()).isEqualTo(DEFAULT_CREATED_ON);
        assertThat(testTypes.getModifiedBy()).isEqualTo(DEFAULT_MODIFIED_BY);
        assertThat(testTypes.getModifiedOn()).isEqualTo(DEFAULT_MODIFIED_ON);
    }

    @Test
    void createTypesWithExistingId() throws Exception {
        // Create the Types with an existing ID
        types.setTypeId(1L);

        int databaseSizeBeforeCreate = typesRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(types))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Types in the database
        List<Types> typesList = typesRepository.findAll().collectList().block();
        assertThat(typesList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllTypes() {
        // Initialize the database
        typesRepository.save(types).block();

        // Get all the typesList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=typeId,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].typeId")
            .value(hasItem(types.getTypeId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].owner")
            .value(hasItem(DEFAULT_OWNER.intValue()))
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
    void getTypes() {
        // Initialize the database
        typesRepository.save(types).block();

        // Get the types
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, types.getTypeId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.typeId")
            .value(is(types.getTypeId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.owner")
            .value(is(DEFAULT_OWNER.intValue()))
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
    void getNonExistingTypes() {
        // Get the types
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingTypes() throws Exception {
        // Initialize the database
        typesRepository.save(types).block();

        int databaseSizeBeforeUpdate = typesRepository.findAll().collectList().block().size();

        // Update the types
        Types updatedTypes = typesRepository.findById(types.getTypeId()).block();
        updatedTypes
            .name(UPDATED_NAME)
            .owner(UPDATED_OWNER)
            .createdBy(UPDATED_CREATED_BY)
            .createdOn(UPDATED_CREATED_ON)
            .modifiedBy(UPDATED_MODIFIED_BY)
            .modifiedOn(UPDATED_MODIFIED_ON);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedTypes.getTypeId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedTypes))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Types in the database
        List<Types> typesList = typesRepository.findAll().collectList().block();
        assertThat(typesList).hasSize(databaseSizeBeforeUpdate);
        Types testTypes = typesList.get(typesList.size() - 1);
        assertThat(testTypes.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTypes.getOwner()).isEqualTo(UPDATED_OWNER);
        assertThat(testTypes.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testTypes.getCreatedOn()).isEqualTo(UPDATED_CREATED_ON);
        assertThat(testTypes.getModifiedBy()).isEqualTo(UPDATED_MODIFIED_BY);
        assertThat(testTypes.getModifiedOn()).isEqualTo(UPDATED_MODIFIED_ON);
    }

    @Test
    void putNonExistingTypes() throws Exception {
        int databaseSizeBeforeUpdate = typesRepository.findAll().collectList().block().size();
        types.setTypeId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, types.getTypeId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(types))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Types in the database
        List<Types> typesList = typesRepository.findAll().collectList().block();
        assertThat(typesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchTypes() throws Exception {
        int databaseSizeBeforeUpdate = typesRepository.findAll().collectList().block().size();
        types.setTypeId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(types))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Types in the database
        List<Types> typesList = typesRepository.findAll().collectList().block();
        assertThat(typesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamTypes() throws Exception {
        int databaseSizeBeforeUpdate = typesRepository.findAll().collectList().block().size();
        types.setTypeId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(types))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Types in the database
        List<Types> typesList = typesRepository.findAll().collectList().block();
        assertThat(typesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateTypesWithPatch() throws Exception {
        // Initialize the database
        typesRepository.save(types).block();

        int databaseSizeBeforeUpdate = typesRepository.findAll().collectList().block().size();

        // Update the types using partial update
        Types partialUpdatedTypes = new Types();
        partialUpdatedTypes.setTypeId(types.getTypeId());

        partialUpdatedTypes.owner(UPDATED_OWNER).createdOn(UPDATED_CREATED_ON).modifiedOn(UPDATED_MODIFIED_ON);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTypes.getTypeId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedTypes))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Types in the database
        List<Types> typesList = typesRepository.findAll().collectList().block();
        assertThat(typesList).hasSize(databaseSizeBeforeUpdate);
        Types testTypes = typesList.get(typesList.size() - 1);
        assertThat(testTypes.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTypes.getOwner()).isEqualTo(UPDATED_OWNER);
        assertThat(testTypes.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testTypes.getCreatedOn()).isEqualTo(UPDATED_CREATED_ON);
        assertThat(testTypes.getModifiedBy()).isEqualTo(DEFAULT_MODIFIED_BY);
        assertThat(testTypes.getModifiedOn()).isEqualTo(UPDATED_MODIFIED_ON);
    }

    @Test
    void fullUpdateTypesWithPatch() throws Exception {
        // Initialize the database
        typesRepository.save(types).block();

        int databaseSizeBeforeUpdate = typesRepository.findAll().collectList().block().size();

        // Update the types using partial update
        Types partialUpdatedTypes = new Types();
        partialUpdatedTypes.setTypeId(types.getTypeId());

        partialUpdatedTypes
            .name(UPDATED_NAME)
            .owner(UPDATED_OWNER)
            .createdBy(UPDATED_CREATED_BY)
            .createdOn(UPDATED_CREATED_ON)
            .modifiedBy(UPDATED_MODIFIED_BY)
            .modifiedOn(UPDATED_MODIFIED_ON);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTypes.getTypeId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedTypes))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Types in the database
        List<Types> typesList = typesRepository.findAll().collectList().block();
        assertThat(typesList).hasSize(databaseSizeBeforeUpdate);
        Types testTypes = typesList.get(typesList.size() - 1);
        assertThat(testTypes.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTypes.getOwner()).isEqualTo(UPDATED_OWNER);
        assertThat(testTypes.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testTypes.getCreatedOn()).isEqualTo(UPDATED_CREATED_ON);
        assertThat(testTypes.getModifiedBy()).isEqualTo(UPDATED_MODIFIED_BY);
        assertThat(testTypes.getModifiedOn()).isEqualTo(UPDATED_MODIFIED_ON);
    }

    @Test
    void patchNonExistingTypes() throws Exception {
        int databaseSizeBeforeUpdate = typesRepository.findAll().collectList().block().size();
        types.setTypeId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, types.getTypeId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(types))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Types in the database
        List<Types> typesList = typesRepository.findAll().collectList().block();
        assertThat(typesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchTypes() throws Exception {
        int databaseSizeBeforeUpdate = typesRepository.findAll().collectList().block().size();
        types.setTypeId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(types))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Types in the database
        List<Types> typesList = typesRepository.findAll().collectList().block();
        assertThat(typesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamTypes() throws Exception {
        int databaseSizeBeforeUpdate = typesRepository.findAll().collectList().block().size();
        types.setTypeId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(types))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Types in the database
        List<Types> typesList = typesRepository.findAll().collectList().block();
        assertThat(typesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteTypes() {
        // Initialize the database
        typesRepository.save(types).block();

        int databaseSizeBeforeDelete = typesRepository.findAll().collectList().block().size();

        // Delete the types
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, types.getTypeId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Types> typesList = typesRepository.findAll().collectList().block();
        assertThat(typesList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
