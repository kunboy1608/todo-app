package com.hoangdp.gateway.web.rest;

import com.hoangdp.gateway.domain.Types;
import com.hoangdp.gateway.repository.TypesRepository;
import com.hoangdp.gateway.service.TypesService;
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
 * REST controller for managing {@link com.hoangdp.gateway.domain.Types}.
 */
@RestController
@RequestMapping("/api")
public class TypesResource {

    private final Logger log = LoggerFactory.getLogger(TypesResource.class);

    private static final String ENTITY_NAME = "types";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TypesService typesService;

    private final TypesRepository typesRepository;

    public TypesResource(TypesService typesService, TypesRepository typesRepository) {
        this.typesService = typesService;
        this.typesRepository = typesRepository;
    }

    /**
     * {@code POST  /types} : Create a new types.
     *
     * @param types the types to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new types, or with status {@code 400 (Bad Request)} if the types has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/types")
    public Mono<ResponseEntity<Types>> createTypes(@RequestBody Types types) throws URISyntaxException {
        log.debug("REST request to save Types : {}", types);
        if (types.getTypeId() != null) {
            throw new BadRequestAlertException("A new types cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return typesService
            .save(types)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/types/" + result.getTypeId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getTypeId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /types/:typeId} : Updates an existing types.
     *
     * @param typeId the id of the types to save.
     * @param types the types to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated types,
     * or with status {@code 400 (Bad Request)} if the types is not valid,
     * or with status {@code 500 (Internal Server Error)} if the types couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/types/{typeId}")
    public Mono<ResponseEntity<Types>> updateTypes(
        @PathVariable(value = "typeId", required = false) final Long typeId,
        @RequestBody Types types
    ) throws URISyntaxException {
        log.debug("REST request to update Types : {}, {}", typeId, types);
        if (types.getTypeId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(typeId, types.getTypeId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return typesRepository
            .existsById(typeId)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return typesService
                    .update(types)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getTypeId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /types/:typeId} : Partial updates given fields of an existing types, field will ignore if it is null
     *
     * @param typeId the id of the types to save.
     * @param types the types to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated types,
     * or with status {@code 400 (Bad Request)} if the types is not valid,
     * or with status {@code 404 (Not Found)} if the types is not found,
     * or with status {@code 500 (Internal Server Error)} if the types couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/types/{typeId}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Types>> partialUpdateTypes(
        @PathVariable(value = "typeId", required = false) final Long typeId,
        @RequestBody Types types
    ) throws URISyntaxException {
        log.debug("REST request to partial update Types partially : {}, {}", typeId, types);
        if (types.getTypeId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(typeId, types.getTypeId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return typesRepository
            .existsById(typeId)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Types> result = typesService.partialUpdate(types);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getTypeId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /types} : get all the types.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of types in body.
     */
    @GetMapping("/types")
    public Mono<ResponseEntity<List<Types>>> getAllTypes(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of Types");
        return typesService
            .countAll()
            .zipWith(typesService.findAll(pageable).collectList())
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
     * {@code GET  /types/:id} : get the "id" types.
     *
     * @param id the id of the types to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the types, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/types/{id}")
    public Mono<ResponseEntity<Types>> getTypes(@PathVariable Long id) {
        log.debug("REST request to get Types : {}", id);
        Mono<Types> types = typesService.findOne(id);
        return ResponseUtil.wrapOrNotFound(types);
    }

    /**
     * {@code DELETE  /types/:id} : delete the "id" types.
     *
     * @param id the id of the types to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/types/{id}")
    public Mono<ResponseEntity<Void>> deleteTypes(@PathVariable Long id) {
        log.debug("REST request to delete Types : {}", id);
        return typesService
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
