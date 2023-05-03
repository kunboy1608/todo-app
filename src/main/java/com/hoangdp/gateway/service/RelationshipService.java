package com.hoangdp.gateway.service;

import com.hoangdp.gateway.domain.Relationship;
import com.hoangdp.gateway.repository.RelationshipRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Relationship}.
 */
@Service
@Transactional
public class RelationshipService {

    private final Logger log = LoggerFactory.getLogger(RelationshipService.class);

    private final RelationshipRepository relationshipRepository;

    public RelationshipService(RelationshipRepository relationshipRepository) {
        this.relationshipRepository = relationshipRepository;
    }

    /**
     * Save a relationship.
     *
     * @param relationship the entity to save.
     * @return the persisted entity.
     */
    public Mono<Relationship> save(Relationship relationship) {
        log.debug("Request to save Relationship : {}", relationship);
        return relationshipRepository.save(relationship);
    }

    /**
     * Update a relationship.
     *
     * @param relationship the entity to save.
     * @return the persisted entity.
     */
    public Mono<Relationship> update(Relationship relationship) {
        log.debug("Request to update Relationship : {}", relationship);
        return relationshipRepository.save(relationship);
    }

    /**
     * Partially update a relationship.
     *
     * @param relationship the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<Relationship> partialUpdate(Relationship relationship) {
        log.debug("Request to partially update Relationship : {}", relationship);

        return relationshipRepository
            .findById(relationship.getRelationshipId())
            .map(existingRelationship -> {
                if (relationship.getOwner() != null) {
                    existingRelationship.setOwner(relationship.getOwner());
                }
                if (relationship.getPartner() != null) {
                    existingRelationship.setPartner(relationship.getPartner());
                }
                if (relationship.getStatus() != null) {
                    existingRelationship.setStatus(relationship.getStatus());
                }
                if (relationship.getCreatedBy() != null) {
                    existingRelationship.setCreatedBy(relationship.getCreatedBy());
                }
                if (relationship.getCreatedOn() != null) {
                    existingRelationship.setCreatedOn(relationship.getCreatedOn());
                }
                if (relationship.getModifiedBy() != null) {
                    existingRelationship.setModifiedBy(relationship.getModifiedBy());
                }
                if (relationship.getModifiedOn() != null) {
                    existingRelationship.setModifiedOn(relationship.getModifiedOn());
                }

                return existingRelationship;
            })
            .flatMap(relationshipRepository::save);
    }

    /**
     * Get all the relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Relationship> findAll(Pageable pageable) {
        log.debug("Request to get all Relationships");
        return relationshipRepository.findAllBy(pageable);
    }

    /**
     * Returns the number of relationships available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return relationshipRepository.count();
    }

    /**
     * Get one relationship by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<Relationship> findOne(Long id) {
        log.debug("Request to get Relationship : {}", id);
        return relationshipRepository.findById(id);
    }

    /**
     * Delete the relationship by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Relationship : {}", id);
        return relationshipRepository.deleteById(id);
    }
}
