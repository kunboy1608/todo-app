package com.hoangdp.gateway.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.hoangdp.gateway.IntegrationTest;
import com.hoangdp.gateway.domain.Conversations;
import com.hoangdp.gateway.repository.ConversationsRepository;
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
 * Integration tests for the {@link ConversationsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ConversationsResourceIT {

    private static final Instant DEFAULT_TIMESTAMP = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_TIMESTAMP = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Long DEFAULT_SENDER = 1L;
    private static final Long UPDATED_SENDER = 2L;

    private static final Long DEFAULT_RECEIVER = 1L;
    private static final Long UPDATED_RECEIVER = 2L;

    private static final String DEFAULT_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_MESSAGE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/conversations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{conversationId}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ConversationsRepository conversationsRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Conversations conversations;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Conversations createEntity(EntityManager em) {
        Conversations conversations = new Conversations()
            .timestamp(DEFAULT_TIMESTAMP)
            .sender(DEFAULT_SENDER)
            .receiver(DEFAULT_RECEIVER)
            .message(DEFAULT_MESSAGE);
        return conversations;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Conversations createUpdatedEntity(EntityManager em) {
        Conversations conversations = new Conversations()
            .timestamp(UPDATED_TIMESTAMP)
            .sender(UPDATED_SENDER)
            .receiver(UPDATED_RECEIVER)
            .message(UPDATED_MESSAGE);
        return conversations;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Conversations.class).block();
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
        conversations = createEntity(em);
    }

    @Test
    void createConversations() throws Exception {
        int databaseSizeBeforeCreate = conversationsRepository.findAll().collectList().block().size();
        // Create the Conversations
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(conversations))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Conversations in the database
        List<Conversations> conversationsList = conversationsRepository.findAll().collectList().block();
        assertThat(conversationsList).hasSize(databaseSizeBeforeCreate + 1);
        Conversations testConversations = conversationsList.get(conversationsList.size() - 1);
        assertThat(testConversations.getTimestamp()).isEqualTo(DEFAULT_TIMESTAMP);
        assertThat(testConversations.getSender()).isEqualTo(DEFAULT_SENDER);
        assertThat(testConversations.getReceiver()).isEqualTo(DEFAULT_RECEIVER);
        assertThat(testConversations.getMessage()).isEqualTo(DEFAULT_MESSAGE);
    }

    @Test
    void createConversationsWithExistingId() throws Exception {
        // Create the Conversations with an existing ID
        conversations.setConversationId(1L);

        int databaseSizeBeforeCreate = conversationsRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(conversations))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Conversations in the database
        List<Conversations> conversationsList = conversationsRepository.findAll().collectList().block();
        assertThat(conversationsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkTimestampIsRequired() throws Exception {
        int databaseSizeBeforeTest = conversationsRepository.findAll().collectList().block().size();
        // set the field null
        conversations.setTimestamp(null);

        // Create the Conversations, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(conversations))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Conversations> conversationsList = conversationsRepository.findAll().collectList().block();
        assertThat(conversationsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkSenderIsRequired() throws Exception {
        int databaseSizeBeforeTest = conversationsRepository.findAll().collectList().block().size();
        // set the field null
        conversations.setSender(null);

        // Create the Conversations, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(conversations))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Conversations> conversationsList = conversationsRepository.findAll().collectList().block();
        assertThat(conversationsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkReceiverIsRequired() throws Exception {
        int databaseSizeBeforeTest = conversationsRepository.findAll().collectList().block().size();
        // set the field null
        conversations.setReceiver(null);

        // Create the Conversations, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(conversations))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Conversations> conversationsList = conversationsRepository.findAll().collectList().block();
        assertThat(conversationsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkMessageIsRequired() throws Exception {
        int databaseSizeBeforeTest = conversationsRepository.findAll().collectList().block().size();
        // set the field null
        conversations.setMessage(null);

        // Create the Conversations, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(conversations))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Conversations> conversationsList = conversationsRepository.findAll().collectList().block();
        assertThat(conversationsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllConversations() {
        // Initialize the database
        conversationsRepository.save(conversations).block();

        // Get all the conversationsList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=conversationId,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].conversationId")
            .value(hasItem(conversations.getConversationId().intValue()))
            .jsonPath("$.[*].timestamp")
            .value(hasItem(DEFAULT_TIMESTAMP.toString()))
            .jsonPath("$.[*].sender")
            .value(hasItem(DEFAULT_SENDER.intValue()))
            .jsonPath("$.[*].receiver")
            .value(hasItem(DEFAULT_RECEIVER.intValue()))
            .jsonPath("$.[*].message")
            .value(hasItem(DEFAULT_MESSAGE));
    }

    @Test
    void getConversations() {
        // Initialize the database
        conversationsRepository.save(conversations).block();

        // Get the conversations
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, conversations.getConversationId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.conversationId")
            .value(is(conversations.getConversationId().intValue()))
            .jsonPath("$.timestamp")
            .value(is(DEFAULT_TIMESTAMP.toString()))
            .jsonPath("$.sender")
            .value(is(DEFAULT_SENDER.intValue()))
            .jsonPath("$.receiver")
            .value(is(DEFAULT_RECEIVER.intValue()))
            .jsonPath("$.message")
            .value(is(DEFAULT_MESSAGE));
    }

    @Test
    void getNonExistingConversations() {
        // Get the conversations
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingConversations() throws Exception {
        // Initialize the database
        conversationsRepository.save(conversations).block();

        int databaseSizeBeforeUpdate = conversationsRepository.findAll().collectList().block().size();

        // Update the conversations
        Conversations updatedConversations = conversationsRepository.findById(conversations.getConversationId()).block();
        updatedConversations.timestamp(UPDATED_TIMESTAMP).sender(UPDATED_SENDER).receiver(UPDATED_RECEIVER).message(UPDATED_MESSAGE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedConversations.getConversationId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedConversations))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Conversations in the database
        List<Conversations> conversationsList = conversationsRepository.findAll().collectList().block();
        assertThat(conversationsList).hasSize(databaseSizeBeforeUpdate);
        Conversations testConversations = conversationsList.get(conversationsList.size() - 1);
        assertThat(testConversations.getTimestamp()).isEqualTo(UPDATED_TIMESTAMP);
        assertThat(testConversations.getSender()).isEqualTo(UPDATED_SENDER);
        assertThat(testConversations.getReceiver()).isEqualTo(UPDATED_RECEIVER);
        assertThat(testConversations.getMessage()).isEqualTo(UPDATED_MESSAGE);
    }

    @Test
    void putNonExistingConversations() throws Exception {
        int databaseSizeBeforeUpdate = conversationsRepository.findAll().collectList().block().size();
        conversations.setConversationId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, conversations.getConversationId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(conversations))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Conversations in the database
        List<Conversations> conversationsList = conversationsRepository.findAll().collectList().block();
        assertThat(conversationsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchConversations() throws Exception {
        int databaseSizeBeforeUpdate = conversationsRepository.findAll().collectList().block().size();
        conversations.setConversationId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(conversations))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Conversations in the database
        List<Conversations> conversationsList = conversationsRepository.findAll().collectList().block();
        assertThat(conversationsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamConversations() throws Exception {
        int databaseSizeBeforeUpdate = conversationsRepository.findAll().collectList().block().size();
        conversations.setConversationId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(conversations))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Conversations in the database
        List<Conversations> conversationsList = conversationsRepository.findAll().collectList().block();
        assertThat(conversationsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateConversationsWithPatch() throws Exception {
        // Initialize the database
        conversationsRepository.save(conversations).block();

        int databaseSizeBeforeUpdate = conversationsRepository.findAll().collectList().block().size();

        // Update the conversations using partial update
        Conversations partialUpdatedConversations = new Conversations();
        partialUpdatedConversations.setConversationId(conversations.getConversationId());

        partialUpdatedConversations.sender(UPDATED_SENDER).receiver(UPDATED_RECEIVER);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedConversations.getConversationId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedConversations))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Conversations in the database
        List<Conversations> conversationsList = conversationsRepository.findAll().collectList().block();
        assertThat(conversationsList).hasSize(databaseSizeBeforeUpdate);
        Conversations testConversations = conversationsList.get(conversationsList.size() - 1);
        assertThat(testConversations.getTimestamp()).isEqualTo(DEFAULT_TIMESTAMP);
        assertThat(testConversations.getSender()).isEqualTo(UPDATED_SENDER);
        assertThat(testConversations.getReceiver()).isEqualTo(UPDATED_RECEIVER);
        assertThat(testConversations.getMessage()).isEqualTo(DEFAULT_MESSAGE);
    }

    @Test
    void fullUpdateConversationsWithPatch() throws Exception {
        // Initialize the database
        conversationsRepository.save(conversations).block();

        int databaseSizeBeforeUpdate = conversationsRepository.findAll().collectList().block().size();

        // Update the conversations using partial update
        Conversations partialUpdatedConversations = new Conversations();
        partialUpdatedConversations.setConversationId(conversations.getConversationId());

        partialUpdatedConversations.timestamp(UPDATED_TIMESTAMP).sender(UPDATED_SENDER).receiver(UPDATED_RECEIVER).message(UPDATED_MESSAGE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedConversations.getConversationId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedConversations))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Conversations in the database
        List<Conversations> conversationsList = conversationsRepository.findAll().collectList().block();
        assertThat(conversationsList).hasSize(databaseSizeBeforeUpdate);
        Conversations testConversations = conversationsList.get(conversationsList.size() - 1);
        assertThat(testConversations.getTimestamp()).isEqualTo(UPDATED_TIMESTAMP);
        assertThat(testConversations.getSender()).isEqualTo(UPDATED_SENDER);
        assertThat(testConversations.getReceiver()).isEqualTo(UPDATED_RECEIVER);
        assertThat(testConversations.getMessage()).isEqualTo(UPDATED_MESSAGE);
    }

    @Test
    void patchNonExistingConversations() throws Exception {
        int databaseSizeBeforeUpdate = conversationsRepository.findAll().collectList().block().size();
        conversations.setConversationId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, conversations.getConversationId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(conversations))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Conversations in the database
        List<Conversations> conversationsList = conversationsRepository.findAll().collectList().block();
        assertThat(conversationsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchConversations() throws Exception {
        int databaseSizeBeforeUpdate = conversationsRepository.findAll().collectList().block().size();
        conversations.setConversationId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(conversations))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Conversations in the database
        List<Conversations> conversationsList = conversationsRepository.findAll().collectList().block();
        assertThat(conversationsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamConversations() throws Exception {
        int databaseSizeBeforeUpdate = conversationsRepository.findAll().collectList().block().size();
        conversations.setConversationId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(conversations))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Conversations in the database
        List<Conversations> conversationsList = conversationsRepository.findAll().collectList().block();
        assertThat(conversationsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteConversations() {
        // Initialize the database
        conversationsRepository.save(conversations).block();

        int databaseSizeBeforeDelete = conversationsRepository.findAll().collectList().block().size();

        // Delete the conversations
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, conversations.getConversationId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Conversations> conversationsList = conversationsRepository.findAll().collectList().block();
        assertThat(conversationsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
