package com.hoangdp.gateway.service;

import com.hoangdp.gateway.domain.Tags;
import com.hoangdp.gateway.repository.TagsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Tags}.
 */
@Service
@Transactional
public class TagsService {

    private final Logger log = LoggerFactory.getLogger(TagsService.class);

    private final TagsRepository tagsRepository;

    public TagsService(TagsRepository tagsRepository) {
        this.tagsRepository = tagsRepository;
    }

    /**
     * Save a tags.
     *
     * @param tags the entity to save.
     * @return the persisted entity.
     */
    public Mono<Tags> save(Tags tags) {
        log.debug("Request to save Tags : {}", tags);
        return tagsRepository.save(tags);
    }

    /**
     * Update a tags.
     *
     * @param tags the entity to save.
     * @return the persisted entity.
     */
    public Mono<Tags> update(Tags tags) {
        log.debug("Request to update Tags : {}", tags);
        return tagsRepository.save(tags);
    }

    /**
     * Partially update a tags.
     *
     * @param tags the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<Tags> partialUpdate(Tags tags) {
        log.debug("Request to partially update Tags : {}", tags);

        return tagsRepository
            .findById(tags.getTagId())
            .map(existingTags -> {
                if (tags.getOwner() != null) {
                    existingTags.setOwner(tags.getOwner());
                }
                if (tags.getName() != null) {
                    existingTags.setName(tags.getName());
                }

                return existingTags;
            })
            .flatMap(tagsRepository::save);
    }

    /**
     * Get all the tags.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Tags> findAll(Pageable pageable) {
        log.debug("Request to get all Tags");
        return tagsRepository.findAllBy(pageable);
    }

    /**
     * Returns the number of tags available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return tagsRepository.count();
    }

    /**
     * Get one tags by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<Tags> findOne(Long id) {
        log.debug("Request to get Tags : {}", id);
        return tagsRepository.findById(id);
    }

    /**
     * Delete the tags by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Tags : {}", id);
        return tagsRepository.deleteById(id);
    }
}
