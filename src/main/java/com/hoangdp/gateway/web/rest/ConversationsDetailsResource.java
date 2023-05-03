package com.hoangdp.gateway.web.rest;

import com.hoangdp.gateway.domain.ConversationsDetails;
import com.hoangdp.gateway.repository.ConversationsDetailsRepository;
import com.hoangdp.gateway.service.ConversationsDetailsService;
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
 * REST controller for managing {@link com.hoangdp.gateway.domain.ConversationsDetails}.
 */
@RestController
@RequestMapping("/api")
public class ConversationsDetailsResource {

    private final Logger log = LoggerFactory.getLogger(ConversationsDetailsResource.class);

    private static final String ENTITY_NAME = "conversationsDetails";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ConversationsDetailsService conversationsDetailsService;

    private final ConversationsDetailsRepository conversationsDetailsRepository;

    public ConversationsDetailsResource(
        ConversationsDetailsService conversationsDetailsService,
        ConversationsDetailsRepository conversationsDetailsRepository
    ) {
        this.conversationsDetailsService = conversationsDetailsService;
        this.conversationsDetailsRepository = conversationsDetailsRepository;
    }

    /**
     * {@code POST  /conversations-details} : Create a new conversationsDetails.
     *
     * @param conversationsDetails the conversationsDetails to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new conversationsDetails, or with status {@code 400 (Bad Request)} if the conversationsDetails has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/conversations-details")
    public Mono<ResponseEntity<ConversationsDetails>> createConversationsDetails(@RequestBody ConversationsDetails conversationsDetails)
        throws URISyntaxException {
        log.debug("REST request to save ConversationsDetails : {}", conversationsDetails);
        if (conversationsDetails.getId() != null) {
            throw new BadRequestAlertException("A new conversationsDetails cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return conversationsDetailsService
            .save(conversationsDetails)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/conversations-details/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /conversations-details/:id} : Updates an existing conversationsDetails.
     *
     * @param id the id of the conversationsDetails to save.
     * @param conversationsDetails the conversationsDetails to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated conversationsDetails,
     * or with status {@code 400 (Bad Request)} if the conversationsDetails is not valid,
     * or with status {@code 500 (Internal Server Error)} if the conversationsDetails couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/conversations-details/{id}")
    public Mono<ResponseEntity<ConversationsDetails>> updateConversationsDetails(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ConversationsDetails conversationsDetails
    ) throws URISyntaxException {
        log.debug("REST request to update ConversationsDetails : {}, {}", id, conversationsDetails);
        if (conversationsDetails.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, conversationsDetails.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return conversationsDetailsRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return conversationsDetailsService
                    .update(conversationsDetails)
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
     * {@code PATCH  /conversations-details/:id} : Partial updates given fields of an existing conversationsDetails, field will ignore if it is null
     *
     * @param id the id of the conversationsDetails to save.
     * @param conversationsDetails the conversationsDetails to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated conversationsDetails,
     * or with status {@code 400 (Bad Request)} if the conversationsDetails is not valid,
     * or with status {@code 404 (Not Found)} if the conversationsDetails is not found,
     * or with status {@code 500 (Internal Server Error)} if the conversationsDetails couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/conversations-details/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ConversationsDetails>> partialUpdateConversationsDetails(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ConversationsDetails conversationsDetails
    ) throws URISyntaxException {
        log.debug("REST request to partial update ConversationsDetails partially : {}, {}", id, conversationsDetails);
        if (conversationsDetails.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, conversationsDetails.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return conversationsDetailsRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<ConversationsDetails> result = conversationsDetailsService.partialUpdate(conversationsDetails);

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
     * {@code GET  /conversations-details} : get all the conversationsDetails.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of conversationsDetails in body.
     */
    @GetMapping("/conversations-details")
    public Mono<ResponseEntity<List<ConversationsDetails>>> getAllConversationsDetails(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of ConversationsDetails");
        return conversationsDetailsService
            .countAll()
            .zipWith(conversationsDetailsService.findAll(pageable).collectList())
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
     * {@code GET  /conversations-details/:id} : get the "id" conversationsDetails.
     *
     * @param id the id of the conversationsDetails to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the conversationsDetails, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/conversations-details/{id}")
    public Mono<ResponseEntity<ConversationsDetails>> getConversationsDetails(@PathVariable Long id) {
        log.debug("REST request to get ConversationsDetails : {}", id);
        Mono<ConversationsDetails> conversationsDetails = conversationsDetailsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(conversationsDetails);
    }

    /**
     * {@code DELETE  /conversations-details/:id} : delete the "id" conversationsDetails.
     *
     * @param id the id of the conversationsDetails to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/conversations-details/{id}")
    public Mono<ResponseEntity<Void>> deleteConversationsDetails(@PathVariable Long id) {
        log.debug("REST request to delete ConversationsDetails : {}", id);
        return conversationsDetailsService
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
