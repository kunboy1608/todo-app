package com.hoangdp.gateway.web.rest;

import com.hoangdp.gateway.domain.Events;
import com.hoangdp.gateway.repository.EventsRepository;
import com.hoangdp.gateway.service.EventsService;
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
 * REST controller for managing {@link com.hoangdp.gateway.domain.Events}.
 */
@RestController
@RequestMapping("/api")
public class EventsResource {

    private final Logger log = LoggerFactory.getLogger(EventsResource.class);

    private static final String ENTITY_NAME = "events";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EventsService eventsService;

    private final EventsRepository eventsRepository;

    public EventsResource(EventsService eventsService, EventsRepository eventsRepository) {
        this.eventsService = eventsService;
        this.eventsRepository = eventsRepository;
    }

    /**
     * {@code POST  /events} : Create a new events.
     *
     * @param events the events to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new events, or with status {@code 400 (Bad Request)} if the events has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/events")
    public Mono<ResponseEntity<Events>> createEvents(@RequestBody Events events) throws URISyntaxException {
        log.debug("REST request to save Events : {}", events);
        if (events.getEventId() != null) {
            throw new BadRequestAlertException("A new events cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return eventsService
            .save(events)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/events/" + result.getEventId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getEventId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /events/:eventId} : Updates an existing events.
     *
     * @param eventId the id of the events to save.
     * @param events the events to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated events,
     * or with status {@code 400 (Bad Request)} if the events is not valid,
     * or with status {@code 500 (Internal Server Error)} if the events couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/events/{eventId}")
    public Mono<ResponseEntity<Events>> updateEvents(
        @PathVariable(value = "eventId", required = false) final Long eventId,
        @RequestBody Events events
    ) throws URISyntaxException {
        log.debug("REST request to update Events : {}, {}", eventId, events);
        if (events.getEventId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(eventId, events.getEventId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return eventsRepository
            .existsById(eventId)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return eventsService
                    .update(events)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getEventId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /events/:eventId} : Partial updates given fields of an existing events, field will ignore if it is null
     *
     * @param eventId the id of the events to save.
     * @param events the events to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated events,
     * or with status {@code 400 (Bad Request)} if the events is not valid,
     * or with status {@code 404 (Not Found)} if the events is not found,
     * or with status {@code 500 (Internal Server Error)} if the events couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/events/{eventId}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Events>> partialUpdateEvents(
        @PathVariable(value = "eventId", required = false) final Long eventId,
        @RequestBody Events events
    ) throws URISyntaxException {
        log.debug("REST request to partial update Events partially : {}, {}", eventId, events);
        if (events.getEventId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(eventId, events.getEventId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return eventsRepository
            .existsById(eventId)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Events> result = eventsService.partialUpdate(events);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getEventId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /events} : get all the events.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of events in body.
     */
    @GetMapping("/events")
    public Mono<ResponseEntity<List<Events>>> getAllEvents(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of Events");
        return eventsService
            .countAll()
            .zipWith(eventsService.findAll(pageable).collectList())
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
     * {@code GET  /events/:id} : get the "id" events.
     *
     * @param id the id of the events to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the events, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/events/{id}")
    public Mono<ResponseEntity<Events>> getEvents(@PathVariable Long id) {
        log.debug("REST request to get Events : {}", id);
        Mono<Events> events = eventsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(events);
    }

    /**
     * {@code DELETE  /events/:id} : delete the "id" events.
     *
     * @param id the id of the events to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/events/{id}")
    public Mono<ResponseEntity<Void>> deleteEvents(@PathVariable Long id) {
        log.debug("REST request to delete Events : {}", id);
        return eventsService
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
