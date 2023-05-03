package com.hoangdp.gateway.web.rest;

import com.hoangdp.gateway.domain.Profiles;
import com.hoangdp.gateway.repository.ProfilesRepository;
import com.hoangdp.gateway.service.ProfilesService;
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
 * REST controller for managing {@link com.hoangdp.gateway.domain.Profiles}.
 */
@RestController
@RequestMapping("/api")
public class ProfilesResource {

    private final Logger log = LoggerFactory.getLogger(ProfilesResource.class);

    private static final String ENTITY_NAME = "profiles";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProfilesService profilesService;

    private final ProfilesRepository profilesRepository;

    public ProfilesResource(ProfilesService profilesService, ProfilesRepository profilesRepository) {
        this.profilesService = profilesService;
        this.profilesRepository = profilesRepository;
    }

    /**
     * {@code POST  /profiles} : Create a new profiles.
     *
     * @param profiles the profiles to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new profiles, or with status {@code 400 (Bad Request)} if the profiles has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/profiles")
    public Mono<ResponseEntity<Profiles>> createProfiles(@RequestBody Profiles profiles) throws URISyntaxException {
        log.debug("REST request to save Profiles : {}", profiles);
        if (profiles.getProfileId() != null) {
            throw new BadRequestAlertException("A new profiles cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return profilesService
            .save(profiles)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/profiles/" + result.getProfileId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getProfileId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /profiles/:profileId} : Updates an existing profiles.
     *
     * @param profileId the id of the profiles to save.
     * @param profiles the profiles to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated profiles,
     * or with status {@code 400 (Bad Request)} if the profiles is not valid,
     * or with status {@code 500 (Internal Server Error)} if the profiles couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/profiles/{profileId}")
    public Mono<ResponseEntity<Profiles>> updateProfiles(
        @PathVariable(value = "profileId", required = false) final Long profileId,
        @RequestBody Profiles profiles
    ) throws URISyntaxException {
        log.debug("REST request to update Profiles : {}, {}", profileId, profiles);
        if (profiles.getProfileId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(profileId, profiles.getProfileId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return profilesRepository
            .existsById(profileId)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return profilesService
                    .update(profiles)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(
                                HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getProfileId().toString())
                            )
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /profiles/:profileId} : Partial updates given fields of an existing profiles, field will ignore if it is null
     *
     * @param profileId the id of the profiles to save.
     * @param profiles the profiles to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated profiles,
     * or with status {@code 400 (Bad Request)} if the profiles is not valid,
     * or with status {@code 404 (Not Found)} if the profiles is not found,
     * or with status {@code 500 (Internal Server Error)} if the profiles couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/profiles/{profileId}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Profiles>> partialUpdateProfiles(
        @PathVariable(value = "profileId", required = false) final Long profileId,
        @RequestBody Profiles profiles
    ) throws URISyntaxException {
        log.debug("REST request to partial update Profiles partially : {}, {}", profileId, profiles);
        if (profiles.getProfileId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(profileId, profiles.getProfileId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return profilesRepository
            .existsById(profileId)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Profiles> result = profilesService.partialUpdate(profiles);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getProfileId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /profiles} : get all the profiles.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of profiles in body.
     */
    @GetMapping("/profiles")
    public Mono<ResponseEntity<List<Profiles>>> getAllProfiles(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of Profiles");
        return profilesService
            .countAll()
            .zipWith(profilesService.findAll(pageable).collectList())
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
     * {@code GET  /profiles/:id} : get the "id" profiles.
     *
     * @param id the id of the profiles to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the profiles, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/profiles/{id}")
    public Mono<ResponseEntity<Profiles>> getProfiles(@PathVariable Long id) {
        log.debug("REST request to get Profiles : {}", id);
        Mono<Profiles> profiles = profilesService.findOne(id);
        return ResponseUtil.wrapOrNotFound(profiles);
    }

    /**
     * {@code DELETE  /profiles/:id} : delete the "id" profiles.
     *
     * @param id the id of the profiles to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/profiles/{id}")
    public Mono<ResponseEntity<Void>> deleteProfiles(@PathVariable Long id) {
        log.debug("REST request to delete Profiles : {}", id);
        return profilesService
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
