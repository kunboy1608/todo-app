package com.hoangdp.gateway.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.hoangdp.gateway.IntegrationTest;
import com.hoangdp.gateway.domain.Expenses;
import com.hoangdp.gateway.repository.EntityManager;
import com.hoangdp.gateway.repository.ExpensesRepository;
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
 * Integration tests for the {@link ExpensesResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ExpensesResourceIT {

    private static final Long DEFAULT_OWNER = 1L;
    private static final Long UPDATED_OWNER = 2L;

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final Double DEFAULT_COST = 1D;
    private static final Double UPDATED_COST = 2D;

    private static final String DEFAULT_TAG = "AAAAAAAAAA";
    private static final String UPDATED_TAG = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DAY = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DAY = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_ON = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_ON = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_MODIFIED_BY = "AAAAAAAAAA";
    private static final String UPDATED_MODIFIED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_MODIFIED_ON = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_MODIFIED_ON = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/expenses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{expenseId}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ExpensesRepository expensesRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Expenses expenses;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Expenses createEntity(EntityManager em) {
        Expenses expenses = new Expenses()
            .owner(DEFAULT_OWNER)
            .content(DEFAULT_CONTENT)
            .cost(DEFAULT_COST)
            .tag(DEFAULT_TAG)
            .day(DEFAULT_DAY)
            .createdBy(DEFAULT_CREATED_BY)
            .createdOn(DEFAULT_CREATED_ON)
            .modifiedBy(DEFAULT_MODIFIED_BY)
            .modifiedOn(DEFAULT_MODIFIED_ON);
        return expenses;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Expenses createUpdatedEntity(EntityManager em) {
        Expenses expenses = new Expenses()
            .owner(UPDATED_OWNER)
            .content(UPDATED_CONTENT)
            .cost(UPDATED_COST)
            .tag(UPDATED_TAG)
            .day(UPDATED_DAY)
            .createdBy(UPDATED_CREATED_BY)
            .createdOn(UPDATED_CREATED_ON)
            .modifiedBy(UPDATED_MODIFIED_BY)
            .modifiedOn(UPDATED_MODIFIED_ON);
        return expenses;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Expenses.class).block();
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
        expenses = createEntity(em);
    }

    @Test
    void createExpenses() throws Exception {
        int databaseSizeBeforeCreate = expensesRepository.findAll().collectList().block().size();
        // Create the Expenses
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(expenses))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Expenses in the database
        List<Expenses> expensesList = expensesRepository.findAll().collectList().block();
        assertThat(expensesList).hasSize(databaseSizeBeforeCreate + 1);
        Expenses testExpenses = expensesList.get(expensesList.size() - 1);
        assertThat(testExpenses.getOwner()).isEqualTo(DEFAULT_OWNER);
        assertThat(testExpenses.getContent()).isEqualTo(DEFAULT_CONTENT);
        assertThat(testExpenses.getCost()).isEqualTo(DEFAULT_COST);
        assertThat(testExpenses.getTag()).isEqualTo(DEFAULT_TAG);
        assertThat(testExpenses.getDay()).isEqualTo(DEFAULT_DAY);
        assertThat(testExpenses.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testExpenses.getCreatedOn()).isEqualTo(DEFAULT_CREATED_ON);
        assertThat(testExpenses.getModifiedBy()).isEqualTo(DEFAULT_MODIFIED_BY);
        assertThat(testExpenses.getModifiedOn()).isEqualTo(DEFAULT_MODIFIED_ON);
    }

    @Test
    void createExpensesWithExistingId() throws Exception {
        // Create the Expenses with an existing ID
        expenses.setExpenseId(1L);

        int databaseSizeBeforeCreate = expensesRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(expenses))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Expenses in the database
        List<Expenses> expensesList = expensesRepository.findAll().collectList().block();
        assertThat(expensesList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllExpenses() {
        // Initialize the database
        expensesRepository.save(expenses).block();

        // Get all the expensesList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=expenseId,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].expenseId")
            .value(hasItem(expenses.getExpenseId().intValue()))
            .jsonPath("$.[*].owner")
            .value(hasItem(DEFAULT_OWNER.intValue()))
            .jsonPath("$.[*].content")
            .value(hasItem(DEFAULT_CONTENT))
            .jsonPath("$.[*].cost")
            .value(hasItem(DEFAULT_COST.doubleValue()))
            .jsonPath("$.[*].tag")
            .value(hasItem(DEFAULT_TAG))
            .jsonPath("$.[*].day")
            .value(hasItem(DEFAULT_DAY.toString()))
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
    void getExpenses() {
        // Initialize the database
        expensesRepository.save(expenses).block();

        // Get the expenses
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, expenses.getExpenseId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.expenseId")
            .value(is(expenses.getExpenseId().intValue()))
            .jsonPath("$.owner")
            .value(is(DEFAULT_OWNER.intValue()))
            .jsonPath("$.content")
            .value(is(DEFAULT_CONTENT))
            .jsonPath("$.cost")
            .value(is(DEFAULT_COST.doubleValue()))
            .jsonPath("$.tag")
            .value(is(DEFAULT_TAG))
            .jsonPath("$.day")
            .value(is(DEFAULT_DAY.toString()))
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
    void getNonExistingExpenses() {
        // Get the expenses
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingExpenses() throws Exception {
        // Initialize the database
        expensesRepository.save(expenses).block();

        int databaseSizeBeforeUpdate = expensesRepository.findAll().collectList().block().size();

        // Update the expenses
        Expenses updatedExpenses = expensesRepository.findById(expenses.getExpenseId()).block();
        updatedExpenses
            .owner(UPDATED_OWNER)
            .content(UPDATED_CONTENT)
            .cost(UPDATED_COST)
            .tag(UPDATED_TAG)
            .day(UPDATED_DAY)
            .createdBy(UPDATED_CREATED_BY)
            .createdOn(UPDATED_CREATED_ON)
            .modifiedBy(UPDATED_MODIFIED_BY)
            .modifiedOn(UPDATED_MODIFIED_ON);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedExpenses.getExpenseId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedExpenses))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Expenses in the database
        List<Expenses> expensesList = expensesRepository.findAll().collectList().block();
        assertThat(expensesList).hasSize(databaseSizeBeforeUpdate);
        Expenses testExpenses = expensesList.get(expensesList.size() - 1);
        assertThat(testExpenses.getOwner()).isEqualTo(UPDATED_OWNER);
        assertThat(testExpenses.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testExpenses.getCost()).isEqualTo(UPDATED_COST);
        assertThat(testExpenses.getTag()).isEqualTo(UPDATED_TAG);
        assertThat(testExpenses.getDay()).isEqualTo(UPDATED_DAY);
        assertThat(testExpenses.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testExpenses.getCreatedOn()).isEqualTo(UPDATED_CREATED_ON);
        assertThat(testExpenses.getModifiedBy()).isEqualTo(UPDATED_MODIFIED_BY);
        assertThat(testExpenses.getModifiedOn()).isEqualTo(UPDATED_MODIFIED_ON);
    }

    @Test
    void putNonExistingExpenses() throws Exception {
        int databaseSizeBeforeUpdate = expensesRepository.findAll().collectList().block().size();
        expenses.setExpenseId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, expenses.getExpenseId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(expenses))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Expenses in the database
        List<Expenses> expensesList = expensesRepository.findAll().collectList().block();
        assertThat(expensesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchExpenses() throws Exception {
        int databaseSizeBeforeUpdate = expensesRepository.findAll().collectList().block().size();
        expenses.setExpenseId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(expenses))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Expenses in the database
        List<Expenses> expensesList = expensesRepository.findAll().collectList().block();
        assertThat(expensesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamExpenses() throws Exception {
        int databaseSizeBeforeUpdate = expensesRepository.findAll().collectList().block().size();
        expenses.setExpenseId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(expenses))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Expenses in the database
        List<Expenses> expensesList = expensesRepository.findAll().collectList().block();
        assertThat(expensesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateExpensesWithPatch() throws Exception {
        // Initialize the database
        expensesRepository.save(expenses).block();

        int databaseSizeBeforeUpdate = expensesRepository.findAll().collectList().block().size();

        // Update the expenses using partial update
        Expenses partialUpdatedExpenses = new Expenses();
        partialUpdatedExpenses.setExpenseId(expenses.getExpenseId());

        partialUpdatedExpenses
            .owner(UPDATED_OWNER)
            .createdBy(UPDATED_CREATED_BY)
            .modifiedBy(UPDATED_MODIFIED_BY)
            .modifiedOn(UPDATED_MODIFIED_ON);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedExpenses.getExpenseId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedExpenses))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Expenses in the database
        List<Expenses> expensesList = expensesRepository.findAll().collectList().block();
        assertThat(expensesList).hasSize(databaseSizeBeforeUpdate);
        Expenses testExpenses = expensesList.get(expensesList.size() - 1);
        assertThat(testExpenses.getOwner()).isEqualTo(UPDATED_OWNER);
        assertThat(testExpenses.getContent()).isEqualTo(DEFAULT_CONTENT);
        assertThat(testExpenses.getCost()).isEqualTo(DEFAULT_COST);
        assertThat(testExpenses.getTag()).isEqualTo(DEFAULT_TAG);
        assertThat(testExpenses.getDay()).isEqualTo(DEFAULT_DAY);
        assertThat(testExpenses.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testExpenses.getCreatedOn()).isEqualTo(DEFAULT_CREATED_ON);
        assertThat(testExpenses.getModifiedBy()).isEqualTo(UPDATED_MODIFIED_BY);
        assertThat(testExpenses.getModifiedOn()).isEqualTo(UPDATED_MODIFIED_ON);
    }

    @Test
    void fullUpdateExpensesWithPatch() throws Exception {
        // Initialize the database
        expensesRepository.save(expenses).block();

        int databaseSizeBeforeUpdate = expensesRepository.findAll().collectList().block().size();

        // Update the expenses using partial update
        Expenses partialUpdatedExpenses = new Expenses();
        partialUpdatedExpenses.setExpenseId(expenses.getExpenseId());

        partialUpdatedExpenses
            .owner(UPDATED_OWNER)
            .content(UPDATED_CONTENT)
            .cost(UPDATED_COST)
            .tag(UPDATED_TAG)
            .day(UPDATED_DAY)
            .createdBy(UPDATED_CREATED_BY)
            .createdOn(UPDATED_CREATED_ON)
            .modifiedBy(UPDATED_MODIFIED_BY)
            .modifiedOn(UPDATED_MODIFIED_ON);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedExpenses.getExpenseId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedExpenses))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Expenses in the database
        List<Expenses> expensesList = expensesRepository.findAll().collectList().block();
        assertThat(expensesList).hasSize(databaseSizeBeforeUpdate);
        Expenses testExpenses = expensesList.get(expensesList.size() - 1);
        assertThat(testExpenses.getOwner()).isEqualTo(UPDATED_OWNER);
        assertThat(testExpenses.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testExpenses.getCost()).isEqualTo(UPDATED_COST);
        assertThat(testExpenses.getTag()).isEqualTo(UPDATED_TAG);
        assertThat(testExpenses.getDay()).isEqualTo(UPDATED_DAY);
        assertThat(testExpenses.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testExpenses.getCreatedOn()).isEqualTo(UPDATED_CREATED_ON);
        assertThat(testExpenses.getModifiedBy()).isEqualTo(UPDATED_MODIFIED_BY);
        assertThat(testExpenses.getModifiedOn()).isEqualTo(UPDATED_MODIFIED_ON);
    }

    @Test
    void patchNonExistingExpenses() throws Exception {
        int databaseSizeBeforeUpdate = expensesRepository.findAll().collectList().block().size();
        expenses.setExpenseId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, expenses.getExpenseId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(expenses))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Expenses in the database
        List<Expenses> expensesList = expensesRepository.findAll().collectList().block();
        assertThat(expensesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchExpenses() throws Exception {
        int databaseSizeBeforeUpdate = expensesRepository.findAll().collectList().block().size();
        expenses.setExpenseId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(expenses))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Expenses in the database
        List<Expenses> expensesList = expensesRepository.findAll().collectList().block();
        assertThat(expensesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamExpenses() throws Exception {
        int databaseSizeBeforeUpdate = expensesRepository.findAll().collectList().block().size();
        expenses.setExpenseId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(expenses))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Expenses in the database
        List<Expenses> expensesList = expensesRepository.findAll().collectList().block();
        assertThat(expensesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteExpenses() {
        // Initialize the database
        expensesRepository.save(expenses).block();

        int databaseSizeBeforeDelete = expensesRepository.findAll().collectList().block().size();

        // Delete the expenses
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, expenses.getExpenseId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Expenses> expensesList = expensesRepository.findAll().collectList().block();
        assertThat(expensesList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
