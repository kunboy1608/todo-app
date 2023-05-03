package com.hoangdp.gateway.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.hoangdp.gateway.IntegrationTest;
import com.hoangdp.gateway.domain.Profiles;
import com.hoangdp.gateway.repository.EntityManager;
import com.hoangdp.gateway.repository.ProfilesRepository;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link ProfilesResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ProfilesResourceIT {

    private static final String DEFAULT_USERNAME = "AAAAAAAAAA";
    private static final String UPDATED_USERNAME = "BBBBBBBBBB";

    private static final String DEFAULT_NICKNAME = "AAAAAAAAAA";
    private static final String UPDATED_NICKNAME = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_BIRTHDAY = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_BIRTHDAY = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_BIO = "AAAAAAAAAA";
    private static final String UPDATED_BIO = "BBBBBBBBBB";

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_ON = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_ON = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_MODIFIED_BY = "AAAAAAAAAA";
    private static final String UPDATED_MODIFIED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_MODIFIED_ON = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_MODIFIED_ON = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/profiles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{profileId}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProfilesRepository profilesRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Profiles profiles;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Profiles createEntity(EntityManager em) {
        Profiles profiles = new Profiles()
            .username(DEFAULT_USERNAME)
            .nickname(DEFAULT_NICKNAME)
            .birthday(DEFAULT_BIRTHDAY)
            .bio(DEFAULT_BIO)
            .createdBy(DEFAULT_CREATED_BY)
            .createdOn(DEFAULT_CREATED_ON)
            .modifiedBy(DEFAULT_MODIFIED_BY)
            .modifiedOn(DEFAULT_MODIFIED_ON);
        return profiles;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Profiles createUpdatedEntity(EntityManager em) {
        Profiles profiles = new Profiles()
            .username(UPDATED_USERNAME)
            .nickname(UPDATED_NICKNAME)
            .birthday(UPDATED_BIRTHDAY)
            .bio(UPDATED_BIO)
            .createdBy(UPDATED_CREATED_BY)
            .createdOn(UPDATED_CREATED_ON)
            .modifiedBy(UPDATED_MODIFIED_BY)
            .modifiedOn(UPDATED_MODIFIED_ON);
        return profiles;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Profiles.class).block();
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
        profiles = createEntity(em);
    }

    @Test
    void createProfiles() throws Exception {
        int databaseSizeBeforeCreate = profilesRepository.findAll().collectList().block().size();
        // Create the Profiles
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(profiles))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Profiles in the database
        List<Profiles> profilesList = profilesRepository.findAll().collectList().block();
        assertThat(profilesList).hasSize(databaseSizeBeforeCreate + 1);
        Profiles testProfiles = profilesList.get(profilesList.size() - 1);
        assertThat(testProfiles.getUsername()).isEqualTo(DEFAULT_USERNAME);
        assertThat(testProfiles.getNickname()).isEqualTo(DEFAULT_NICKNAME);
        assertThat(testProfiles.getBirthday()).isEqualTo(DEFAULT_BIRTHDAY);
        assertThat(testProfiles.getBio()).isEqualTo(DEFAULT_BIO);
        assertThat(testProfiles.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testProfiles.getCreatedOn()).isEqualTo(DEFAULT_CREATED_ON);
        assertThat(testProfiles.getModifiedBy()).isEqualTo(DEFAULT_MODIFIED_BY);
        assertThat(testProfiles.getModifiedOn()).isEqualTo(DEFAULT_MODIFIED_ON);
    }

    @Test
    void createProfilesWithExistingId() throws Exception {
        // Create the Profiles with an existing ID
        profiles.setProfileId(1L);

        int databaseSizeBeforeCreate = profilesRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(profiles))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Profiles in the database
        List<Profiles> profilesList = profilesRepository.findAll().collectList().block();
        assertThat(profilesList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllProfiles() {
        // Initialize the database
        profilesRepository.save(profiles).block();

        // Get all the profilesList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=profileId,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].profileId")
            .value(hasItem(profiles.getProfileId().intValue()))
            .jsonPath("$.[*].username")
            .value(hasItem(DEFAULT_USERNAME))
            .jsonPath("$.[*].nickname")
            .value(hasItem(DEFAULT_NICKNAME))
            .jsonPath("$.[*].birthday")
            .value(hasItem(DEFAULT_BIRTHDAY.toString()))
            .jsonPath("$.[*].bio")
            .value(hasItem(DEFAULT_BIO))
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
    void getProfiles() {
        // Initialize the database
        profilesRepository.save(profiles).block();

        // Get the profiles
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, profiles.getProfileId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.profileId")
            .value(is(profiles.getProfileId().intValue()))
            .jsonPath("$.username")
            .value(is(DEFAULT_USERNAME))
            .jsonPath("$.nickname")
            .value(is(DEFAULT_NICKNAME))
            .jsonPath("$.birthday")
            .value(is(DEFAULT_BIRTHDAY.toString()))
            .jsonPath("$.bio")
            .value(is(DEFAULT_BIO))
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
    void getNonExistingProfiles() {
        // Get the profiles
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingProfiles() throws Exception {
        // Initialize the database
        profilesRepository.save(profiles).block();

        int databaseSizeBeforeUpdate = profilesRepository.findAll().collectList().block().size();

        // Update the profiles
        Profiles updatedProfiles = profilesRepository.findById(profiles.getProfileId()).block();
        updatedProfiles
            .username(UPDATED_USERNAME)
            .nickname(UPDATED_NICKNAME)
            .birthday(UPDATED_BIRTHDAY)
            .bio(UPDATED_BIO)
            .createdBy(UPDATED_CREATED_BY)
            .createdOn(UPDATED_CREATED_ON)
            .modifiedBy(UPDATED_MODIFIED_BY)
            .modifiedOn(UPDATED_MODIFIED_ON);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedProfiles.getProfileId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedProfiles))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Profiles in the database
        List<Profiles> profilesList = profilesRepository.findAll().collectList().block();
        assertThat(profilesList).hasSize(databaseSizeBeforeUpdate);
        Profiles testProfiles = profilesList.get(profilesList.size() - 1);
        assertThat(testProfiles.getUsername()).isEqualTo(UPDATED_USERNAME);
        assertThat(testProfiles.getNickname()).isEqualTo(UPDATED_NICKNAME);
        assertThat(testProfiles.getBirthday()).isEqualTo(UPDATED_BIRTHDAY);
        assertThat(testProfiles.getBio()).isEqualTo(UPDATED_BIO);
        assertThat(testProfiles.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testProfiles.getCreatedOn()).isEqualTo(UPDATED_CREATED_ON);
        assertThat(testProfiles.getModifiedBy()).isEqualTo(UPDATED_MODIFIED_BY);
        assertThat(testProfiles.getModifiedOn()).isEqualTo(UPDATED_MODIFIED_ON);
    }

    @Test
    void putNonExistingProfiles() throws Exception {
        int databaseSizeBeforeUpdate = profilesRepository.findAll().collectList().block().size();
        profiles.setProfileId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, profiles.getProfileId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(profiles))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Profiles in the database
        List<Profiles> profilesList = profilesRepository.findAll().collectList().block();
        assertThat(profilesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchProfiles() throws Exception {
        int databaseSizeBeforeUpdate = profilesRepository.findAll().collectList().block().size();
        profiles.setProfileId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(profiles))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Profiles in the database
        List<Profiles> profilesList = profilesRepository.findAll().collectList().block();
        assertThat(profilesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamProfiles() throws Exception {
        int databaseSizeBeforeUpdate = profilesRepository.findAll().collectList().block().size();
        profiles.setProfileId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(profiles))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Profiles in the database
        List<Profiles> profilesList = profilesRepository.findAll().collectList().block();
        assertThat(profilesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateProfilesWithPatch() throws Exception {
        // Initialize the database
        profilesRepository.save(profiles).block();

        int databaseSizeBeforeUpdate = profilesRepository.findAll().collectList().block().size();

        // Update the profiles using partial update
        Profiles partialUpdatedProfiles = new Profiles();
        partialUpdatedProfiles.setProfileId(profiles.getProfileId());

        partialUpdatedProfiles.birthday(UPDATED_BIRTHDAY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProfiles.getProfileId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedProfiles))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Profiles in the database
        List<Profiles> profilesList = profilesRepository.findAll().collectList().block();
        assertThat(profilesList).hasSize(databaseSizeBeforeUpdate);
        Profiles testProfiles = profilesList.get(profilesList.size() - 1);
        assertThat(testProfiles.getUsername()).isEqualTo(DEFAULT_USERNAME);
        assertThat(testProfiles.getNickname()).isEqualTo(DEFAULT_NICKNAME);
        assertThat(testProfiles.getBirthday()).isEqualTo(UPDATED_BIRTHDAY);
        assertThat(testProfiles.getBio()).isEqualTo(DEFAULT_BIO);
        assertThat(testProfiles.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testProfiles.getCreatedOn()).isEqualTo(DEFAULT_CREATED_ON);
        assertThat(testProfiles.getModifiedBy()).isEqualTo(DEFAULT_MODIFIED_BY);
        assertThat(testProfiles.getModifiedOn()).isEqualTo(DEFAULT_MODIFIED_ON);
    }

    @Test
    void fullUpdateProfilesWithPatch() throws Exception {
        // Initialize the database
        profilesRepository.save(profiles).block();

        int databaseSizeBeforeUpdate = profilesRepository.findAll().collectList().block().size();

        // Update the profiles using partial update
        Profiles partialUpdatedProfiles = new Profiles();
        partialUpdatedProfiles.setProfileId(profiles.getProfileId());

        partialUpdatedProfiles
            .username(UPDATED_USERNAME)
            .nickname(UPDATED_NICKNAME)
            .birthday(UPDATED_BIRTHDAY)
            .bio(UPDATED_BIO)
            .createdBy(UPDATED_CREATED_BY)
            .createdOn(UPDATED_CREATED_ON)
            .modifiedBy(UPDATED_MODIFIED_BY)
            .modifiedOn(UPDATED_MODIFIED_ON);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProfiles.getProfileId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedProfiles))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Profiles in the database
        List<Profiles> profilesList = profilesRepository.findAll().collectList().block();
        assertThat(profilesList).hasSize(databaseSizeBeforeUpdate);
        Profiles testProfiles = profilesList.get(profilesList.size() - 1);
        assertThat(testProfiles.getUsername()).isEqualTo(UPDATED_USERNAME);
        assertThat(testProfiles.getNickname()).isEqualTo(UPDATED_NICKNAME);
        assertThat(testProfiles.getBirthday()).isEqualTo(UPDATED_BIRTHDAY);
        assertThat(testProfiles.getBio()).isEqualTo(UPDATED_BIO);
        assertThat(testProfiles.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testProfiles.getCreatedOn()).isEqualTo(UPDATED_CREATED_ON);
        assertThat(testProfiles.getModifiedBy()).isEqualTo(UPDATED_MODIFIED_BY);
        assertThat(testProfiles.getModifiedOn()).isEqualTo(UPDATED_MODIFIED_ON);
    }

    @Test
    void patchNonExistingProfiles() throws Exception {
        int databaseSizeBeforeUpdate = profilesRepository.findAll().collectList().block().size();
        profiles.setProfileId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, profiles.getProfileId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(profiles))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Profiles in the database
        List<Profiles> profilesList = profilesRepository.findAll().collectList().block();
        assertThat(profilesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchProfiles() throws Exception {
        int databaseSizeBeforeUpdate = profilesRepository.findAll().collectList().block().size();
        profiles.setProfileId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(profiles))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Profiles in the database
        List<Profiles> profilesList = profilesRepository.findAll().collectList().block();
        assertThat(profilesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamProfiles() throws Exception {
        int databaseSizeBeforeUpdate = profilesRepository.findAll().collectList().block().size();
        profiles.setProfileId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(profiles))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Profiles in the database
        List<Profiles> profilesList = profilesRepository.findAll().collectList().block();
        assertThat(profilesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteProfiles() {
        // Initialize the database
        profilesRepository.save(profiles).block();

        int databaseSizeBeforeDelete = profilesRepository.findAll().collectList().block().size();

        // Delete the profiles
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, profiles.getProfileId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Profiles> profilesList = profilesRepository.findAll().collectList().block();
        assertThat(profilesList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
