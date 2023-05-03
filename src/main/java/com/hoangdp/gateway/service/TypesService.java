package com.hoangdp.gateway.service;

import com.hoangdp.gateway.domain.Types;
import com.hoangdp.gateway.repository.TypesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Types}.
 */
@Service
@Transactional
public class TypesService {

    private final Logger log = LoggerFactory.getLogger(TypesService.class);

    private final TypesRepository typesRepository;

    public TypesService(TypesRepository typesRepository) {
        this.typesRepository = typesRepository;
    }

    /**
     * Save a types.
     *
     * @param types the entity to save.
     * @return the persisted entity.
     */
    public Mono<Types> save(Types types) {
        log.debug("Request to save Types : {}", types);
        return typesRepository.save(types);
    }

    /**
     * Update a types.
     *
     * @param types the entity to save.
     * @return the persisted entity.
     */
    public Mono<Types> update(Types types) {
        log.debug("Request to update Types : {}", types);
        return typesRepository.save(types);
    }

    /**
     * Partially update a types.
     *
     * @param types the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<Types> partialUpdate(Types types) {
        log.debug("Request to partially update Types : {}", types);

        return typesRepository
            .findById(types.getTypeId())
            .map(existingTypes -> {
                if (types.getName() != null) {
                    existingTypes.setName(types.getName());
                }
                if (types.getOwner() != null) {
                    existingTypes.setOwner(types.getOwner());
                }
                if (types.getCreatedBy() != null) {
                    existingTypes.setCreatedBy(types.getCreatedBy());
                }
                if (types.getCreatedOn() != null) {
                    existingTypes.setCreatedOn(types.getCreatedOn());
                }
                if (types.getModifiedBy() != null) {
                    existingTypes.setModifiedBy(types.getModifiedBy());
                }
                if (types.getModifiedOn() != null) {
                    existingTypes.setModifiedOn(types.getModifiedOn());
                }

                return existingTypes;
            })
            .flatMap(typesRepository::save);
    }

    /**
     * Get all the types.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Types> findAll(Pageable pageable) {
        log.debug("Request to get all Types");
        return typesRepository.findAllBy(pageable);
    }

    /**
     * Returns the number of types available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return typesRepository.count();
    }

    /**
     * Get one types by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<Types> findOne(Long id) {
        log.debug("Request to get Types : {}", id);
        return typesRepository.findById(id);
    }

    /**
     * Delete the types by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Types : {}", id);
        return typesRepository.deleteById(id);
    }
}
