package com.hoangdp.gateway.web.rest;

import com.hoangdp.gateway.domain.Conversations;
import com.hoangdp.gateway.repository.ConversationsRepository;
import com.hoangdp.gateway.service.ConversationsService;
import com.hoangdp.gateway.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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
 * REST controller for managing {@link com.hoangdp.gateway.domain.Conversations}.
 */
@RestController
@RequestMapping("/api")
public class ConversationsResource {

    private final Logger log = LoggerFactory.getLogger(ConversationsResource.class);

    private static final String ENTITY_NAME = "conversations";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ConversationsService conversationsService;

    private final ConversationsRepository conversationsRepository;

    public ConversationsResource(ConversationsService conversationsService, ConversationsRepository conversationsRepository) {
        this.conversationsService = conversationsService;
        this.conversationsRepository = conversationsRepository;
    }

    /**
     * {@code POST  /conversations} : Create a new conversations.
     *
     * @param conversations the conversations to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new conversations, or with status {@code 400 (Bad Request)} if the conversations has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/conversations")
    public Mono<ResponseEntity<Conversations>> createConversations(@Valid @RequestBody Conversations conversations)
        throws URISyntaxException {
        log.debug("REST request to save Conversations : {}", conversations);
        if (conversations.getConversationId() != null) {
            throw new BadRequestAlertException("A new conversations cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return conversationsService
            .save(conversations)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/conversations/" + result.getConversationId()))
                        .headers(
                            HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getConversationId().toString())
                        )
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /conversations/:conversationId} : Updates an existing conversations.
     *
     * @param conversationId the id of the conversations to save.
     * @param conversations the conversations to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated conversations,
     * or with status {@code 400 (Bad Request)} if the conversations is not valid,
     * or with status {@code 500 (Internal Server Error)} if the conversations couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/conversations/{conversationId}")
    public Mono<ResponseEntity<Conversations>> updateConversations(
        @PathVariable(value = "conversationId", required = false) final Long conversationId,
        @Valid @RequestBody Conversations conversations
    ) throws URISyntaxException {
        log.debug("REST request to update Conversations : {}, {}", conversationId, conversations);
        if (conversations.getConversationId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(conversationId, conversations.getConversationId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return conversationsRepository
            .existsById(conversationId)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return conversationsService
                    .update(conversations)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(
                                HeaderUtil.createEntityUpdateAlert(
                                    applicationName,
                                    true,
                                    ENTITY_NAME,
                                    result.getConversationId().toString()
                                )
                            )
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /conversations/:conversationId} : Partial updates given fields of an existing conversations, field will ignore if it is null
     *
     * @param conversationId the id of the conversations to save.
     * @param conversations the conversations to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated conversations,
     * or with status {@code 400 (Bad Request)} if the conversations is not valid,
     * or with status {@code 404 (Not Found)} if the conversations is not found,
     * or with status {@code 500 (Internal Server Error)} if the conversations couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/conversations/{conversationId}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Conversations>> partialUpdateConversations(
        @PathVariable(value = "conversationId", required = false) final Long conversationId,
        @NotNull @RequestBody Conversations conversations
    ) throws URISyntaxException {
        log.debug("REST request to partial update Conversations partially : {}, {}", conversationId, conversations);
        if (conversations.getConversationId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(conversationId, conversations.getConversationId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return conversationsRepository
            .existsById(conversationId)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Conversations> result = conversationsService.partialUpdate(conversations);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(
                                HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getConversationId().toString())
                            )
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /conversations} : get all the conversations.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of conversations in body.
     */
    @GetMapping("/conversations")
    public Mono<ResponseEntity<List<Conversations>>> getAllConversations(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of Conversations");
        return conversationsService
            .countAll()
            .zipWith(conversationsService.findAll(pageable).collectList())
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
     * {@code GET  /conversations/:id} : get the "id" conversations.
     *
     * @param id the id of the conversations to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the conversations, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/conversations/{id}")
    public Mono<ResponseEntity<Conversations>> getConversations(@PathVariable Long id) {
        log.debug("REST request to get Conversations : {}", id);
        Mono<Conversations> conversations = conversationsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(conversations);
    }

    /**
     * {@code DELETE  /conversations/:id} : delete the "id" conversations.
     *
     * @param id the id of the conversations to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/conversations/{id}")
    public Mono<ResponseEntity<Void>> deleteConversations(@PathVariable Long id) {
        log.debug("REST request to delete Conversations : {}", id);
        return conversationsService
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
