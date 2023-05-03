package com.hoangdp.gateway.service;

import com.hoangdp.gateway.domain.Profiles;
import com.hoangdp.gateway.repository.ProfilesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Profiles}.
 */
@Service
@Transactional
public class ProfilesService {

    private final Logger log = LoggerFactory.getLogger(ProfilesService.class);

    private final ProfilesRepository profilesRepository;

    public ProfilesService(ProfilesRepository profilesRepository) {
        this.profilesRepository = profilesRepository;
    }

    /**
     * Save a profiles.
     *
     * @param profiles the entity to save.
     * @return the persisted entity.
     */
    public Mono<Profiles> save(Profiles profiles) {
        log.debug("Request to save Profiles : {}", profiles);
        return profilesRepository.save(profiles);
    }

    /**
     * Update a profiles.
     *
     * @param profiles the entity to save.
     * @return the persisted entity.
     */
    public Mono<Profiles> update(Profiles profiles) {
        log.debug("Request to update Profiles : {}", profiles);
        return profilesRepository.save(profiles);
    }

    /**
     * Partially update a profiles.
     *
     * @param profiles the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<Profiles> partialUpdate(Profiles profiles) {
        log.debug("Request to partially update Profiles : {}", profiles);

        return profilesRepository
            .findById(profiles.getProfileId())
            .map(existingProfiles -> {
                if (profiles.getUsername() != null) {
                    existingProfiles.setUsername(profiles.getUsername());
                }
                if (profiles.getNickname() != null) {
                    existingProfiles.setNickname(profiles.getNickname());
                }
                if (profiles.getBirthday() != null) {
                    existingProfiles.setBirthday(profiles.getBirthday());
                }
                if (profiles.getBio() != null) {
                    existingProfiles.setBio(profiles.getBio());
                }
                if (profiles.getCreatedBy() != null) {
                    existingProfiles.setCreatedBy(profiles.getCreatedBy());
                }
                if (profiles.getCreatedOn() != null) {
                    existingProfiles.setCreatedOn(profiles.getCreatedOn());
                }
                if (profiles.getModifiedBy() != null) {
                    existingProfiles.setModifiedBy(profiles.getModifiedBy());
                }
                if (profiles.getModifiedOn() != null) {
                    existingProfiles.setModifiedOn(profiles.getModifiedOn());
                }

                return existingProfiles;
            })
            .flatMap(profilesRepository::save);
    }

    /**
     * Get all the profiles.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Profiles> findAll(Pageable pageable) {
        log.debug("Request to get all Profiles");
        return profilesRepository.findAllBy(pageable);
    }

    /**
     * Returns the number of profiles available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return profilesRepository.count();
    }

    /**
     * Get one profiles by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<Profiles> findOne(Long id) {
        log.debug("Request to get Profiles : {}", id);
        return profilesRepository.findById(id);
    }

    /**
     * Delete the profiles by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Profiles : {}", id);
        return profilesRepository.deleteById(id);
    }
}
