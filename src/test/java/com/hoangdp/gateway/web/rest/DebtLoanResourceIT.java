package com.hoangdp.gateway.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.hoangdp.gateway.IntegrationTest;
import com.hoangdp.gateway.domain.DebtLoan;
import com.hoangdp.gateway.repository.DebtLoanRepository;
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
 * Integration tests for the {@link DebtLoanResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class DebtLoanResourceIT {

    private static final Long DEFAULT_LOAN_USER_ID = 1L;
    private static final Long UPDATED_LOAN_USER_ID = 2L;

    private static final Long DEFAULT_DEBT_USER_ID = 1L;
    private static final Long UPDATED_DEBT_USER_ID = 2L;

    private static final Double DEFAULT_COST = 1D;
    private static final Double UPDATED_COST = 2D;

    private static final Instant DEFAULT_DEADLINE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DEADLINE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_DAT_OF_PAYMENT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DAT_OF_PAYMENT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/debt-loans";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private DebtLoanRepository debtLoanRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private DebtLoan debtLoan;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DebtLoan createEntity(EntityManager em) {
        DebtLoan debtLoan = new DebtLoan()
            .loanUserId(DEFAULT_LOAN_USER_ID)
            .debtUserId(DEFAULT_DEBT_USER_ID)
            .cost(DEFAULT_COST)
            .deadline(DEFAULT_DEADLINE)
            .datOfPayment(DEFAULT_DAT_OF_PAYMENT);
        return debtLoan;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DebtLoan createUpdatedEntity(EntityManager em) {
        DebtLoan debtLoan = new DebtLoan()
            .loanUserId(UPDATED_LOAN_USER_ID)
            .debtUserId(UPDATED_DEBT_USER_ID)
            .cost(UPDATED_COST)
            .deadline(UPDATED_DEADLINE)
            .datOfPayment(UPDATED_DAT_OF_PAYMENT);
        return debtLoan;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(DebtLoan.class).block();
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
        debtLoan = createEntity(em);
    }

    @Test
    void createDebtLoan() throws Exception {
        int databaseSizeBeforeCreate = debtLoanRepository.findAll().collectList().block().size();
        // Create the DebtLoan
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(debtLoan))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the DebtLoan in the database
        List<DebtLoan> debtLoanList = debtLoanRepository.findAll().collectList().block();
        assertThat(debtLoanList).hasSize(databaseSizeBeforeCreate + 1);
        DebtLoan testDebtLoan = debtLoanList.get(debtLoanList.size() - 1);
        assertThat(testDebtLoan.getLoanUserId()).isEqualTo(DEFAULT_LOAN_USER_ID);
        assertThat(testDebtLoan.getDebtUserId()).isEqualTo(DEFAULT_DEBT_USER_ID);
        assertThat(testDebtLoan.getCost()).isEqualTo(DEFAULT_COST);
        assertThat(testDebtLoan.getDeadline()).isEqualTo(DEFAULT_DEADLINE);
        assertThat(testDebtLoan.getDatOfPayment()).isEqualTo(DEFAULT_DAT_OF_PAYMENT);
    }

    @Test
    void createDebtLoanWithExistingId() throws Exception {
        // Create the DebtLoan with an existing ID
        debtLoan.setId(1L);

        int databaseSizeBeforeCreate = debtLoanRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(debtLoan))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the DebtLoan in the database
        List<DebtLoan> debtLoanList = debtLoanRepository.findAll().collectList().block();
        assertThat(debtLoanList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllDebtLoans() {
        // Initialize the database
        debtLoanRepository.save(debtLoan).block();

        // Get all the debtLoanList
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
            .value(hasItem(debtLoan.getId().intValue()))
            .jsonPath("$.[*].loanUserId")
            .value(hasItem(DEFAULT_LOAN_USER_ID.intValue()))
            .jsonPath("$.[*].debtUserId")
            .value(hasItem(DEFAULT_DEBT_USER_ID.intValue()))
            .jsonPath("$.[*].cost")
            .value(hasItem(DEFAULT_COST.doubleValue()))
            .jsonPath("$.[*].deadline")
            .value(hasItem(DEFAULT_DEADLINE.toString()))
            .jsonPath("$.[*].datOfPayment")
            .value(hasItem(DEFAULT_DAT_OF_PAYMENT.toString()));
    }

    @Test
    void getDebtLoan() {
        // Initialize the database
        debtLoanRepository.save(debtLoan).block();

        // Get the debtLoan
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, debtLoan.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(debtLoan.getId().intValue()))
            .jsonPath("$.loanUserId")
            .value(is(DEFAULT_LOAN_USER_ID.intValue()))
            .jsonPath("$.debtUserId")
            .value(is(DEFAULT_DEBT_USER_ID.intValue()))
            .jsonPath("$.cost")
            .value(is(DEFAULT_COST.doubleValue()))
            .jsonPath("$.deadline")
            .value(is(DEFAULT_DEADLINE.toString()))
            .jsonPath("$.datOfPayment")
            .value(is(DEFAULT_DAT_OF_PAYMENT.toString()));
    }

    @Test
    void getNonExistingDebtLoan() {
        // Get the debtLoan
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingDebtLoan() throws Exception {
        // Initialize the database
        debtLoanRepository.save(debtLoan).block();

        int databaseSizeBeforeUpdate = debtLoanRepository.findAll().collectList().block().size();

        // Update the debtLoan
        DebtLoan updatedDebtLoan = debtLoanRepository.findById(debtLoan.getId()).block();
        updatedDebtLoan
            .loanUserId(UPDATED_LOAN_USER_ID)
            .debtUserId(UPDATED_DEBT_USER_ID)
            .cost(UPDATED_COST)
            .deadline(UPDATED_DEADLINE)
            .datOfPayment(UPDATED_DAT_OF_PAYMENT);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedDebtLoan.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedDebtLoan))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the DebtLoan in the database
        List<DebtLoan> debtLoanList = debtLoanRepository.findAll().collectList().block();
        assertThat(debtLoanList).hasSize(databaseSizeBeforeUpdate);
        DebtLoan testDebtLoan = debtLoanList.get(debtLoanList.size() - 1);
        assertThat(testDebtLoan.getLoanUserId()).isEqualTo(UPDATED_LOAN_USER_ID);
        assertThat(testDebtLoan.getDebtUserId()).isEqualTo(UPDATED_DEBT_USER_ID);
        assertThat(testDebtLoan.getCost()).isEqualTo(UPDATED_COST);
        assertThat(testDebtLoan.getDeadline()).isEqualTo(UPDATED_DEADLINE);
        assertThat(testDebtLoan.getDatOfPayment()).isEqualTo(UPDATED_DAT_OF_PAYMENT);
    }

    @Test
    void putNonExistingDebtLoan() throws Exception {
        int databaseSizeBeforeUpdate = debtLoanRepository.findAll().collectList().block().size();
        debtLoan.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, debtLoan.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(debtLoan))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the DebtLoan in the database
        List<DebtLoan> debtLoanList = debtLoanRepository.findAll().collectList().block();
        assertThat(debtLoanList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchDebtLoan() throws Exception {
        int databaseSizeBeforeUpdate = debtLoanRepository.findAll().collectList().block().size();
        debtLoan.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(debtLoan))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the DebtLoan in the database
        List<DebtLoan> debtLoanList = debtLoanRepository.findAll().collectList().block();
        assertThat(debtLoanList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamDebtLoan() throws Exception {
        int databaseSizeBeforeUpdate = debtLoanRepository.findAll().collectList().block().size();
        debtLoan.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(debtLoan))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the DebtLoan in the database
        List<DebtLoan> debtLoanList = debtLoanRepository.findAll().collectList().block();
        assertThat(debtLoanList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateDebtLoanWithPatch() throws Exception {
        // Initialize the database
        debtLoanRepository.save(debtLoan).block();

        int databaseSizeBeforeUpdate = debtLoanRepository.findAll().collectList().block().size();

        // Update the debtLoan using partial update
        DebtLoan partialUpdatedDebtLoan = new DebtLoan();
        partialUpdatedDebtLoan.setId(debtLoan.getId());

        partialUpdatedDebtLoan.deadline(UPDATED_DEADLINE).datOfPayment(UPDATED_DAT_OF_PAYMENT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDebtLoan.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedDebtLoan))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the DebtLoan in the database
        List<DebtLoan> debtLoanList = debtLoanRepository.findAll().collectList().block();
        assertThat(debtLoanList).hasSize(databaseSizeBeforeUpdate);
        DebtLoan testDebtLoan = debtLoanList.get(debtLoanList.size() - 1);
        assertThat(testDebtLoan.getLoanUserId()).isEqualTo(DEFAULT_LOAN_USER_ID);
        assertThat(testDebtLoan.getDebtUserId()).isEqualTo(DEFAULT_DEBT_USER_ID);
        assertThat(testDebtLoan.getCost()).isEqualTo(DEFAULT_COST);
        assertThat(testDebtLoan.getDeadline()).isEqualTo(UPDATED_DEADLINE);
        assertThat(testDebtLoan.getDatOfPayment()).isEqualTo(UPDATED_DAT_OF_PAYMENT);
    }

    @Test
    void fullUpdateDebtLoanWithPatch() throws Exception {
        // Initialize the database
        debtLoanRepository.save(debtLoan).block();

        int databaseSizeBeforeUpdate = debtLoanRepository.findAll().collectList().block().size();

        // Update the debtLoan using partial update
        DebtLoan partialUpdatedDebtLoan = new DebtLoan();
        partialUpdatedDebtLoan.setId(debtLoan.getId());

        partialUpdatedDebtLoan
            .loanUserId(UPDATED_LOAN_USER_ID)
            .debtUserId(UPDATED_DEBT_USER_ID)
            .cost(UPDATED_COST)
            .deadline(UPDATED_DEADLINE)
            .datOfPayment(UPDATED_DAT_OF_PAYMENT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDebtLoan.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedDebtLoan))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the DebtLoan in the database
        List<DebtLoan> debtLoanList = debtLoanRepository.findAll().collectList().block();
        assertThat(debtLoanList).hasSize(databaseSizeBeforeUpdate);
        DebtLoan testDebtLoan = debtLoanList.get(debtLoanList.size() - 1);
        assertThat(testDebtLoan.getLoanUserId()).isEqualTo(UPDATED_LOAN_USER_ID);
        assertThat(testDebtLoan.getDebtUserId()).isEqualTo(UPDATED_DEBT_USER_ID);
        assertThat(testDebtLoan.getCost()).isEqualTo(UPDATED_COST);
        assertThat(testDebtLoan.getDeadline()).isEqualTo(UPDATED_DEADLINE);
        assertThat(testDebtLoan.getDatOfPayment()).isEqualTo(UPDATED_DAT_OF_PAYMENT);
    }

    @Test
    void patchNonExistingDebtLoan() throws Exception {
        int databaseSizeBeforeUpdate = debtLoanRepository.findAll().collectList().block().size();
        debtLoan.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, debtLoan.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(debtLoan))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the DebtLoan in the database
        List<DebtLoan> debtLoanList = debtLoanRepository.findAll().collectList().block();
        assertThat(debtLoanList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchDebtLoan() throws Exception {
        int databaseSizeBeforeUpdate = debtLoanRepository.findAll().collectList().block().size();
        debtLoan.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(debtLoan))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the DebtLoan in the database
        List<DebtLoan> debtLoanList = debtLoanRepository.findAll().collectList().block();
        assertThat(debtLoanList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamDebtLoan() throws Exception {
        int databaseSizeBeforeUpdate = debtLoanRepository.findAll().collectList().block().size();
        debtLoan.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(debtLoan))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the DebtLoan in the database
        List<DebtLoan> debtLoanList = debtLoanRepository.findAll().collectList().block();
        assertThat(debtLoanList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteDebtLoan() {
        // Initialize the database
        debtLoanRepository.save(debtLoan).block();

        int databaseSizeBeforeDelete = debtLoanRepository.findAll().collectList().block().size();

        // Delete the debtLoan
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, debtLoan.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<DebtLoan> debtLoanList = debtLoanRepository.findAll().collectList().block();
        assertThat(debtLoanList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
