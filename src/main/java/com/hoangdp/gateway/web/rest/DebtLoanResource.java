package com.hoangdp.gateway.web.rest;

import com.hoangdp.gateway.domain.DebtLoan;
import com.hoangdp.gateway.repository.DebtLoanRepository;
import com.hoangdp.gateway.service.DebtLoanService;
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
 * REST controller for managing {@link com.hoangdp.gateway.domain.DebtLoan}.
 */
@RestController
@RequestMapping("/api")
public class DebtLoanResource {

    private final Logger log = LoggerFactory.getLogger(DebtLoanResource.class);

    private static final String ENTITY_NAME = "debtLoan";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DebtLoanService debtLoanService;

    private final DebtLoanRepository debtLoanRepository;

    public DebtLoanResource(DebtLoanService debtLoanService, DebtLoanRepository debtLoanRepository) {
        this.debtLoanService = debtLoanService;
        this.debtLoanRepository = debtLoanRepository;
    }

    /**
     * {@code POST  /debt-loans} : Create a new debtLoan.
     *
     * @param debtLoan the debtLoan to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new debtLoan, or with status {@code 400 (Bad Request)} if the debtLoan has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/debt-loans")
    public Mono<ResponseEntity<DebtLoan>> createDebtLoan(@RequestBody DebtLoan debtLoan) throws URISyntaxException {
        log.debug("REST request to save DebtLoan : {}", debtLoan);
        if (debtLoan.getId() != null) {
            throw new BadRequestAlertException("A new debtLoan cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return debtLoanService
            .save(debtLoan)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/debt-loans/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /debt-loans/:id} : Updates an existing debtLoan.
     *
     * @param id the id of the debtLoan to save.
     * @param debtLoan the debtLoan to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated debtLoan,
     * or with status {@code 400 (Bad Request)} if the debtLoan is not valid,
     * or with status {@code 500 (Internal Server Error)} if the debtLoan couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/debt-loans/{id}")
    public Mono<ResponseEntity<DebtLoan>> updateDebtLoan(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody DebtLoan debtLoan
    ) throws URISyntaxException {
        log.debug("REST request to update DebtLoan : {}, {}", id, debtLoan);
        if (debtLoan.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, debtLoan.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return debtLoanRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return debtLoanService
                    .update(debtLoan)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /debt-loans/:id} : Partial updates given fields of an existing debtLoan, field will ignore if it is null
     *
     * @param id the id of the debtLoan to save.
     * @param debtLoan the debtLoan to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated debtLoan,
     * or with status {@code 400 (Bad Request)} if the debtLoan is not valid,
     * or with status {@code 404 (Not Found)} if the debtLoan is not found,
     * or with status {@code 500 (Internal Server Error)} if the debtLoan couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/debt-loans/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<DebtLoan>> partialUpdateDebtLoan(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody DebtLoan debtLoan
    ) throws URISyntaxException {
        log.debug("REST request to partial update DebtLoan partially : {}, {}", id, debtLoan);
        if (debtLoan.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, debtLoan.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return debtLoanRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<DebtLoan> result = debtLoanService.partialUpdate(debtLoan);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /debt-loans} : get all the debtLoans.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of debtLoans in body.
     */
    @GetMapping("/debt-loans")
    public Mono<ResponseEntity<List<DebtLoan>>> getAllDebtLoans(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of DebtLoans");
        return debtLoanService
            .countAll()
            .zipWith(debtLoanService.findAll(pageable).collectList())
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
     * {@code GET  /debt-loans/:id} : get the "id" debtLoan.
     *
     * @param id the id of the debtLoan to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the debtLoan, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/debt-loans/{id}")
    public Mono<ResponseEntity<DebtLoan>> getDebtLoan(@PathVariable Long id) {
        log.debug("REST request to get DebtLoan : {}", id);
        Mono<DebtLoan> debtLoan = debtLoanService.findOne(id);
        return ResponseUtil.wrapOrNotFound(debtLoan);
    }

    /**
     * {@code DELETE  /debt-loans/:id} : delete the "id" debtLoan.
     *
     * @param id the id of the debtLoan to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/debt-loans/{id}")
    public Mono<ResponseEntity<Void>> deleteDebtLoan(@PathVariable Long id) {
        log.debug("REST request to delete DebtLoan : {}", id);
        return debtLoanService
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
