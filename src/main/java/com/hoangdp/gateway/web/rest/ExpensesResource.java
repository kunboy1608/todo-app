package com.hoangdp.gateway.web.rest;

import com.hoangdp.gateway.domain.Expenses;
import com.hoangdp.gateway.repository.ExpensesRepository;
import com.hoangdp.gateway.service.ExpensesService;
import com.hoangdp.gateway.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.hoangdp.gateway.domain.Expenses}.
 */
@RestController
@RequestMapping("/api")
public class ExpensesResource {

    private final Logger log = LoggerFactory.getLogger(ExpensesResource.class);

    private static final String ENTITY_NAME = "expenses";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ExpensesService expensesService;

    private final ExpensesRepository expensesRepository;

    public ExpensesResource(ExpensesService expensesService, ExpensesRepository expensesRepository) {
        this.expensesService = expensesService;
        this.expensesRepository = expensesRepository;
    }

    /**
     * {@code POST  /expenses} : Create a new expenses.
     *
     * @param expenses the expenses to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new expenses, or with status {@code 400 (Bad Request)} if the expenses has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/expenses")
    public Mono<ResponseEntity<Expenses>> createExpenses(@RequestBody Expenses expenses) throws URISyntaxException {
        log.debug("REST request to save Expenses : {}", expenses);
        if (expenses.getExpenseId() != null) {
            throw new BadRequestAlertException("A new expenses cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return expensesService
            .save(expenses)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/expenses/" + result.getExpenseId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getExpenseId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /expenses/:expenseId} : Updates an existing expenses.
     *
     * @param expenseId the id of the expenses to save.
     * @param expenses the expenses to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated expenses,
     * or with status {@code 400 (Bad Request)} if the expenses is not valid,
     * or with status {@code 500 (Internal Server Error)} if the expenses couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/expenses/{expenseId}")
    public Mono<ResponseEntity<Expenses>> updateExpenses(
        @PathVariable(value = "expenseId", required = false) final Long expenseId,
        @RequestBody Expenses expenses
    ) throws URISyntaxException {
        log.debug("REST request to update Expenses : {}, {}", expenseId, expenses);
        if (expenses.getExpenseId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(expenseId, expenses.getExpenseId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return expensesRepository
            .existsById(expenseId)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return expensesService
                    .update(expenses)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(
                                HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getExpenseId().toString())
                            )
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /expenses/:expenseId} : Partial updates given fields of an existing expenses, field will ignore if it is null
     *
     * @param expenseId the id of the expenses to save.
     * @param expenses the expenses to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated expenses,
     * or with status {@code 400 (Bad Request)} if the expenses is not valid,
     * or with status {@code 404 (Not Found)} if the expenses is not found,
     * or with status {@code 500 (Internal Server Error)} if the expenses couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/expenses/{expenseId}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Expenses>> partialUpdateExpenses(
        @PathVariable(value = "expenseId", required = false) final Long expenseId,
        @RequestBody Expenses expenses
    ) throws URISyntaxException {
        log.debug("REST request to partial update Expenses partially : {}, {}", expenseId, expenses);
        if (expenses.getExpenseId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(expenseId, expenses.getExpenseId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return expensesRepository
            .existsById(expenseId)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Expenses> result = expensesService.partialUpdate(expenses);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getExpenseId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /expenses} : get all the expenses.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of expenses in body.
     */
    @GetMapping("/expenses")
    public Mono<ResponseEntity<List<Expenses>>> getAllExpenses(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of Expenses");
        return expensesService
            .countAll()
            .zipWith(expensesService.findAll(pageable).collectList())
            .map(countWithEntities ->
                ResponseEntity
                    .ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            UriComponentsBuilder.fromHttpRequest(request),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2())
            );
    }

    /**
     * {@code GET  /expenses/:id} : get the "id" expenses.
     *
     * @param id the id of the expenses to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the expenses, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/expenses/{id}")
    public Mono<ResponseEntity<Expenses>> getExpenses(@PathVariable Long id) {
        log.debug("REST request to get Expenses : {}", id);
        Mono<Expenses> expenses = expensesService.findOne(id);
        return ResponseUtil.wrapOrNotFound(expenses);
    }

    /**
     * {@code DELETE  /expenses/:id} : delete the "id" expenses.
     *
     * @param id the id of the expenses to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/expenses/{id}")
    public Mono<ResponseEntity<Void>> deleteExpenses(@PathVariable Long id) {
        log.debug("REST request to delete Expenses : {}", id);
        return expensesService
            .delete(id)
            .then(
                Mono.just(
                    ResponseEntity
                        .noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }
}
