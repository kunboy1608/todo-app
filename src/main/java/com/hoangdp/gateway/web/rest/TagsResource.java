package com.hoangdp.gateway.web.rest;

import com.hoangdp.gateway.domain.Tags;
import com.hoangdp.gateway.repository.TagsRepository;
import com.hoangdp.gateway.service.TagsService;
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
 * REST controller for managing {@link com.hoangdp.gateway.domain.Tags}.
 */
@RestController
@RequestMapping("/api")
public class TagsResource {

    private final Logger log = LoggerFactory.getLogger(TagsResource.class);

    private static final String ENTITY_NAME = "tags";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TagsService tagsService;

    private final TagsRepository tagsRepository;

    public TagsResource(TagsService tagsService, TagsRepository tagsRepository) {
        this.tagsService = tagsService;
        this.tagsRepository = tagsRepository;
    }

    /**
     * {@code POST  /tags} : Create a new tags.
     *
     * @param tags the tags to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new tags, or with status {@code 400 (Bad Request)} if the tags has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/tags")
    public Mono<ResponseEntity<Tags>> createTags(@RequestBody Tags tags) throws URISyntaxException {
        log.debug("REST request to save Tags : {}", tags);
        if (tags.getTagId() != null) {
            throw new BadRequestAlertException("A new tags cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return tagsService
            .save(tags)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/tags/" + result.getTagId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getTagId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /tags/:tagId} : Updates an existing tags.
     *
     * @param tagId the id of the tags to save.
     * @param tags the tags to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tags,
     * or with status {@code 400 (Bad Request)} if the tags is not valid,
     * or with status {@code 500 (Internal Server Error)} if the tags couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/tags/{tagId}")
    public Mono<ResponseEntity<Tags>> updateTags(@PathVariable(value = "tagId", required = false) final Long tagId, @RequestBody Tags tags)
        throws URISyntaxException {
        log.debug("REST request to update Tags : {}, {}", tagId, tags);
        if (tags.getTagId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(tagId, tags.getTagId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return tagsRepository
            .existsById(tagId)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return tagsService
                    .update(tags)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getTagId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /tags/:tagId} : Partial updates given fields of an existing tags, field will ignore if it is null
     *
     * @param tagId the id of the tags to save.
     * @param tags the tags to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tags,
     * or with status {@code 400 (Bad Request)} if the tags is not valid,
     * or with status {@code 404 (Not Found)} if the tags is not found,
     * or with status {@code 500 (Internal Server Error)} if the tags couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/tags/{tagId}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Tags>> partialUpdateTags(
        @PathVariable(value = "tagId", required = false) final Long tagId,
        @RequestBody Tags tags
    ) throws URISyntaxException {
        log.debug("REST request to partial update Tags partially : {}, {}", tagId, tags);
        if (tags.getTagId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(tagId, tags.getTagId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return tagsRepository
            .existsById(tagId)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Tags> result = tagsService.partialUpdate(tags);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getTagId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /tags} : get all the tags.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of tags in body.
     */
    @GetMapping("/tags")
    public Mono<ResponseEntity<List<Tags>>> getAllTags(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of Tags");
        return tagsService
            .countAll()
            .zipWith(tagsService.findAll(pageable).collectList())
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
     * {@code GET  /tags/:id} : get the "id" tags.
     *
     * @param id the id of the tags to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the tags, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/tags/{id}")
    public Mono<ResponseEntity<Tags>> getTags(@PathVariable Long id) {
        log.debug("REST request to get Tags : {}", id);
        Mono<Tags> tags = tagsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(tags);
    }

    /**
     * {@code DELETE  /tags/:id} : delete the "id" tags.
     *
     * @param id the id of the tags to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/tags/{id}")
    public Mono<ResponseEntity<Void>> deleteTags(@PathVariable Long id) {
        log.debug("REST request to delete Tags : {}", id);
        return tagsService
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
