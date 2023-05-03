package com.hoangdp.gateway.web.rest;

import com.hoangdp.gateway.domain.Notes;
import com.hoangdp.gateway.repository.NotesRepository;
import com.hoangdp.gateway.service.NotesService;
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
 * REST controller for managing {@link com.hoangdp.gateway.domain.Notes}.
 */
@RestController
@RequestMapping("/api")
public class NotesResource {

    private final Logger log = LoggerFactory.getLogger(NotesResource.class);

    private static final String ENTITY_NAME = "notes";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final NotesService notesService;

    private final NotesRepository notesRepository;

    public NotesResource(NotesService notesService, NotesRepository notesRepository) {
        this.notesService = notesService;
        this.notesRepository = notesRepository;
    }

    /**
     * {@code POST  /notes} : Create a new notes.
     *
     * @param notes the notes to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new notes, or with status {@code 400 (Bad Request)} if the notes has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/notes")
    public Mono<ResponseEntity<Notes>> createNotes(@RequestBody Notes notes) throws URISyntaxException {
        log.debug("REST request to save Notes : {}", notes);
        if (notes.getNoteId() != null) {
            throw new BadRequestAlertException("A new notes cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return notesService
            .save(notes)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/notes/" + result.getNoteId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getNoteId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /notes/:noteId} : Updates an existing notes.
     *
     * @param noteId the id of the notes to save.
     * @param notes the notes to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated notes,
     * or with status {@code 400 (Bad Request)} if the notes is not valid,
     * or with status {@code 500 (Internal Server Error)} if the notes couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/notes/{noteId}")
    public Mono<ResponseEntity<Notes>> updateNotes(
        @PathVariable(value = "noteId", required = false) final Long noteId,
        @RequestBody Notes notes
    ) throws URISyntaxException {
        log.debug("REST request to update Notes : {}, {}", noteId, notes);
        if (notes.getNoteId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(noteId, notes.getNoteId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return notesRepository
            .existsById(noteId)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return notesService
                    .update(notes)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getNoteId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /notes/:noteId} : Partial updates given fields of an existing notes, field will ignore if it is null
     *
     * @param noteId the id of the notes to save.
     * @param notes the notes to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated notes,
     * or with status {@code 400 (Bad Request)} if the notes is not valid,
     * or with status {@code 404 (Not Found)} if the notes is not found,
     * or with status {@code 500 (Internal Server Error)} if the notes couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/notes/{noteId}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Notes>> partialUpdateNotes(
        @PathVariable(value = "noteId", required = false) final Long noteId,
        @RequestBody Notes notes
    ) throws URISyntaxException {
        log.debug("REST request to partial update Notes partially : {}, {}", noteId, notes);
        if (notes.getNoteId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(noteId, notes.getNoteId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return notesRepository
            .existsById(noteId)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Notes> result = notesService.partialUpdate(notes);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getNoteId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /notes} : get all the notes.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of notes in body.
     */
    @GetMapping("/notes")
    public Mono<ResponseEntity<List<Notes>>> getAllNotes(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of Notes");
        return notesService
            .countAll()
            .zipWith(notesService.findAll(pageable).collectList())
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
     * {@code GET  /notes/:id} : get the "id" notes.
     *
     * @param id the id of the notes to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the notes, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/notes/{id}")
    public Mono<ResponseEntity<Notes>> getNotes(@PathVariable Long id) {
        log.debug("REST request to get Notes : {}", id);
        Mono<Notes> notes = notesService.findOne(id);
        return ResponseUtil.wrapOrNotFound(notes);
    }

    /**
     * {@code DELETE  /notes/:id} : delete the "id" notes.
     *
     * @param id the id of the notes to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/notes/{id}")
    public Mono<ResponseEntity<Void>> deleteNotes(@PathVariable Long id) {
        log.debug("REST request to delete Notes : {}", id);
        return notesService
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
