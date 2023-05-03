package com.hoangdp.gateway.service;

import com.hoangdp.gateway.domain.Notes;
import com.hoangdp.gateway.repository.NotesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Notes}.
 */
@Service
@Transactional
public class NotesService {

    private final Logger log = LoggerFactory.getLogger(NotesService.class);

    private final NotesRepository notesRepository;

    public NotesService(NotesRepository notesRepository) {
        this.notesRepository = notesRepository;
    }

    /**
     * Save a notes.
     *
     * @param notes the entity to save.
     * @return the persisted entity.
     */
    public Mono<Notes> save(Notes notes) {
        log.debug("Request to save Notes : {}", notes);
        return notesRepository.save(notes);
    }

    /**
     * Update a notes.
     *
     * @param notes the entity to save.
     * @return the persisted entity.
     */
    public Mono<Notes> update(Notes notes) {
        log.debug("Request to update Notes : {}", notes);
        return notesRepository.save(notes);
    }

    /**
     * Partially update a notes.
     *
     * @param notes the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<Notes> partialUpdate(Notes notes) {
        log.debug("Request to partially update Notes : {}", notes);

        return notesRepository
            .findById(notes.getNoteId())
            .map(existingNotes -> {
                if (notes.getOwner() != null) {
                    existingNotes.setOwner(notes.getOwner());
                }
                if (notes.getContent() != null) {
                    existingNotes.setContent(notes.getContent());
                }

                return existingNotes;
            })
            .flatMap(notesRepository::save);
    }

    /**
     * Get all the notes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Notes> findAll(Pageable pageable) {
        log.debug("Request to get all Notes");
        return notesRepository.findAllBy(pageable);
    }

    /**
     * Returns the number of notes available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return notesRepository.count();
    }

    /**
     * Get one notes by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<Notes> findOne(Long id) {
        log.debug("Request to get Notes : {}", id);
        return notesRepository.findById(id);
    }

    /**
     * Delete the notes by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Notes : {}", id);
        return notesRepository.deleteById(id);
    }
}
