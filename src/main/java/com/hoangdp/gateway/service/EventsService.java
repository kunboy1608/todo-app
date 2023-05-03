package com.hoangdp.gateway.service;

import com.hoangdp.gateway.domain.Events;
import com.hoangdp.gateway.repository.EventsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Events}.
 */
@Service
@Transactional
public class EventsService {

    private final Logger log = LoggerFactory.getLogger(EventsService.class);

    private final EventsRepository eventsRepository;

    public EventsService(EventsRepository eventsRepository) {
        this.eventsRepository = eventsRepository;
    }

    /**
     * Save a events.
     *
     * @param events the entity to save.
     * @return the persisted entity.
     */
    public Mono<Events> save(Events events) {
        log.debug("Request to save Events : {}", events);
        return eventsRepository.save(events);
    }

    /**
     * Update a events.
     *
     * @param events the entity to save.
     * @return the persisted entity.
     */
    public Mono<Events> update(Events events) {
        log.debug("Request to update Events : {}", events);
        return eventsRepository.save(events);
    }

    /**
     * Partially update a events.
     *
     * @param events the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<Events> partialUpdate(Events events) {
        log.debug("Request to partially update Events : {}", events);

        return eventsRepository
            .findById(events.getEventId())
            .map(existingEvents -> {
                if (events.getOwner() != null) {
                    existingEvents.setOwner(events.getOwner());
                }
                if (events.getKind() != null) {
                    existingEvents.setKind(events.getKind());
                }
                if (events.getDate() != null) {
                    existingEvents.setDate(events.getDate());
                }
                if (events.getIsLunar() != null) {
                    existingEvents.setIsLunar(events.getIsLunar());
                }

                return existingEvents;
            })
            .flatMap(eventsRepository::save);
    }

    /**
     * Get all the events.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Events> findAll(Pageable pageable) {
        log.debug("Request to get all Events");
        return eventsRepository.findAllBy(pageable);
    }

    /**
     * Returns the number of events available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return eventsRepository.count();
    }

    /**
     * Get one events by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<Events> findOne(Long id) {
        log.debug("Request to get Events : {}", id);
        return eventsRepository.findById(id);
    }

    /**
     * Delete the events by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Events : {}", id);
        return eventsRepository.deleteById(id);
    }
}
