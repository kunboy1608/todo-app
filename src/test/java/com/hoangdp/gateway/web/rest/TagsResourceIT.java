package com.hoangdp.gateway.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.hoangdp.gateway.IntegrationTest;
import com.hoangdp.gateway.domain.Tags;
import com.hoangdp.gateway.repository.EntityManager;
import com.hoangdp.gateway.repository.TagsRepository;
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
 * Integration tests for the {@link TagsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TagsResourceIT {

    private static final Long DEFAULT_OWNER = 1L;
    private static final Long UPDATED_OWNER = 2L;

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/tags";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{tagId}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TagsRepository tagsRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Tags tags;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tags createEntity(EntityManager em) {
        Tags tags = new Tags().owner(DEFAULT_OWNER).name(DEFAULT_NAME);
        return tags;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tags createUpdatedEntity(EntityManager em) {
        Tags tags = new Tags().owner(UPDATED_OWNER).name(UPDATED_NAME);
        return tags;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Tags.class).block();
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
        tags = createEntity(em);
    }

    @Test
    void createTags() throws Exception {
        int databaseSizeBeforeCreate = tagsRepository.findAll().collectList().block().size();
        // Create the Tags
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(tags))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Tags in the database
        List<Tags> tagsList = tagsRepository.findAll().collectList().block();
        assertThat(tagsList).hasSize(databaseSizeBeforeCreate + 1);
        Tags testTags = tagsList.get(tagsList.size() - 1);
        assertThat(testTags.getOwner()).isEqualTo(DEFAULT_OWNER);
        assertThat(testTags.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    void createTagsWithExistingId() throws Exception {
        // Create the Tags with an existing ID
        tags.setTagId(1L);

        int databaseSizeBeforeCreate = tagsRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(tags))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Tags in the database
        List<Tags> tagsList = tagsRepository.findAll().collectList().block();
        assertThat(tagsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllTags() {
        // Initialize the database
        tagsRepository.save(tags).block();

        // Get all the tagsList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=tagId,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].tagId")
            .value(hasItem(tags.getTagId().intValue()))
            .jsonPath("$.[*].owner")
            .value(hasItem(DEFAULT_OWNER.intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME));
    }

    @Test
    void getTags() {
        // Initialize the database
        tagsRepository.save(tags).block();

        // Get the tags
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, tags.getTagId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.tagId")
            .value(is(tags.getTagId().intValue()))
            .jsonPath("$.owner")
            .value(is(DEFAULT_OWNER.intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME));
    }

    @Test
    void getNonExistingTags() {
        // Get the tags
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingTags() throws Exception {
        // Initialize the database
        tagsRepository.save(tags).block();

        int databaseSizeBeforeUpdate = tagsRepository.findAll().collectList().block().size();

        // Update the tags
        Tags updatedTags = tagsRepository.findById(tags.getTagId()).block();
        updatedTags.owner(UPDATED_OWNER).name(UPDATED_NAME);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedTags.getTagId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedTags))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Tags in the database
        List<Tags> tagsList = tagsRepository.findAll().collectList().block();
        assertThat(tagsList).hasSize(databaseSizeBeforeUpdate);
        Tags testTags = tagsList.get(tagsList.size() - 1);
        assertThat(testTags.getOwner()).isEqualTo(UPDATED_OWNER);
        assertThat(testTags.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    void putNonExistingTags() throws Exception {
        int databaseSizeBeforeUpdate = tagsRepository.findAll().collectList().block().size();
        tags.setTagId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, tags.getTagId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(tags))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Tags in the database
        List<Tags> tagsList = tagsRepository.findAll().collectList().block();
        assertThat(tagsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchTags() throws Exception {
        int databaseSizeBeforeUpdate = tagsRepository.findAll().collectList().block().size();
        tags.setTagId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(tags))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Tags in the database
        List<Tags> tagsList = tagsRepository.findAll().collectList().block();
        assertThat(tagsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamTags() throws Exception {
        int databaseSizeBeforeUpdate = tagsRepository.findAll().collectList().block().size();
        tags.setTagId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(tags))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Tags in the database
        List<Tags> tagsList = tagsRepository.findAll().collectList().block();
        assertThat(tagsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateTagsWithPatch() throws Exception {
        // Initialize the database
        tagsRepository.save(tags).block();

        int databaseSizeBeforeUpdate = tagsRepository.findAll().collectList().block().size();

        // Update the tags using partial update
        Tags partialUpdatedTags = new Tags();
        partialUpdatedTags.setTagId(tags.getTagId());

        partialUpdatedTags.name(UPDATED_NAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTags.getTagId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedTags))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Tags in the database
        List<Tags> tagsList = tagsRepository.findAll().collectList().block();
        assertThat(tagsList).hasSize(databaseSizeBeforeUpdate);
        Tags testTags = tagsList.get(tagsList.size() - 1);
        assertThat(testTags.getOwner()).isEqualTo(DEFAULT_OWNER);
        assertThat(testTags.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    void fullUpdateTagsWithPatch() throws Exception {
        // Initialize the database
        tagsRepository.save(tags).block();

        int databaseSizeBeforeUpdate = tagsRepository.findAll().collectList().block().size();

        // Update the tags using partial update
        Tags partialUpdatedTags = new Tags();
        partialUpdatedTags.setTagId(tags.getTagId());

        partialUpdatedTags.owner(UPDATED_OWNER).name(UPDATED_NAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTags.getTagId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedTags))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Tags in the database
        List<Tags> tagsList = tagsRepository.findAll().collectList().block();
        assertThat(tagsList).hasSize(databaseSizeBeforeUpdate);
        Tags testTags = tagsList.get(tagsList.size() - 1);
        assertThat(testTags.getOwner()).isEqualTo(UPDATED_OWNER);
        assertThat(testTags.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    void patchNonExistingTags() throws Exception {
        int databaseSizeBeforeUpdate = tagsRepository.findAll().collectList().block().size();
        tags.setTagId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, tags.getTagId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(tags))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Tags in the database
        List<Tags> tagsList = tagsRepository.findAll().collectList().block();
        assertThat(tagsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchTags() throws Exception {
        int databaseSizeBeforeUpdate = tagsRepository.findAll().collectList().block().size();
        tags.setTagId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(tags))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Tags in the database
        List<Tags> tagsList = tagsRepository.findAll().collectList().block();
        assertThat(tagsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamTags() throws Exception {
        int databaseSizeBeforeUpdate = tagsRepository.findAll().collectList().block().size();
        tags.setTagId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(tags))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Tags in the database
        List<Tags> tagsList = tagsRepository.findAll().collectList().block();
        assertThat(tagsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteTags() {
        // Initialize the database
        tagsRepository.save(tags).block();

        int databaseSizeBeforeDelete = tagsRepository.findAll().collectList().block().size();

        // Delete the tags
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, tags.getTagId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Tags> tagsList = tagsRepository.findAll().collectList().block();
        assertThat(tagsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
