package com.hoangdp.gateway.service;

import com.hoangdp.gateway.domain.ConversationsDetails;
import com.hoangdp.gateway.repository.ConversationsDetailsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ConversationsDetails}.
 */
@Service
@Transactional
public class ConversationsDetailsService {

    private final Logger log = LoggerFactory.getLogger(ConversationsDetailsService.class);

    private final ConversationsDetailsRepository conversationsDetailsRepository;

    public ConversationsDetailsService(ConversationsDetailsRepository conversationsDetailsRepository) {
        this.conversationsDetailsRepository = conversationsDetailsRepository;
    }

    /**
     * Save a conversationsDetails.
     *
     * @param conversationsDetails the entity to save.
     * @return the persisted entity.
     */
    public Mono<ConversationsDetails> save(ConversationsDetails conversationsDetails) {
        log.debug("Request to save ConversationsDetails : {}", conversationsDetails);
        return conversationsDetailsRepository.save(conversationsDetails);
    }

    /**
     * Update a conversationsDetails.
     *
     * @param conversationsDetails the entity to save.
     * @return the persisted entity.
     */
    public Mono<ConversationsDetails> update(ConversationsDetails conversationsDetails) {
        log.debug("Request to update ConversationsDetails : {}", conversationsDetails);
        return conversationsDetailsRepository.save(conversationsDetails);
    }

    /**
     * Partially update a conversationsDetails.
     *
     * @param conversationsDetails the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<ConversationsDetails> partialUpdate(ConversationsDetails conversationsDetails) {
        log.debug("Request to partially update ConversationsDetails : {}", conversationsDetails);

        return conversationsDetailsRepository
            .findById(conversationsDetails.getId())
            .map(existingConversationsDetails -> {
                if (conversationsDetails.getName() != null) {
                    existingConversationsDetails.setName(conversationsDetails.getName());
                }
                if (conversationsDetails.getIsGroup() != null) {
                    existingConversationsDetails.setIsGroup(conversationsDetails.getIsGroup());
                }
                if (conversationsDetails.getCreatedBy() != null) {
                    existingConversationsDetails.setCreatedBy(conversationsDetails.getCreatedBy());
                }
                if (conversationsDetails.getCreatedOn() != null) {
                    existingConversationsDetails.setCreatedOn(conversationsDetails.getCreatedOn());
                }
                if (conversationsDetails.getModifiedBy() != null) {
                    existingConversationsDetails.setModifiedBy(conversationsDetails.getModifiedBy());
                }
                if (conversationsDetails.getModifiedOn() != null) {
                    existingConversationsDetails.setModifiedOn(conversationsDetails.getModifiedOn());
                }

                return existingConversationsDetails;
            })
            .flatMap(conversationsDetailsRepository::save);
    }

    /**
     * Get all the conversationsDetails.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ConversationsDetails> findAll(Pageable pageable) {
        log.debug("Request to get all ConversationsDetails");
        return conversationsDetailsRepository.findAllBy(pageable);
    }

    /**
     * Returns the number of conversationsDetails available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return conversationsDetailsRepository.count();
    }

    /**
     * Get one conversationsDetails by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<ConversationsDetails> findOne(Long id) {
        log.debug("Request to get ConversationsDetails : {}", id);
        return conversationsDetailsRepository.findById(id);
    }

    /**
     * Delete the conversationsDetails by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete ConversationsDetails : {}", id);
        return conversationsDetailsRepository.deleteById(id);
    }
}
