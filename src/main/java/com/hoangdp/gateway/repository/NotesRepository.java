package com.hoangdp.gateway.repository;

import com.hoangdp.gateway.domain.Notes;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Notes entity.
 */
@SuppressWarnings("unused")
@Repository
public interface NotesRepository extends ReactiveCrudRepository<Notes, Long>, NotesRepositoryInternal {
    Flux<Notes> findAllBy(Pageable pageable);

    @Query("SELECT * FROM notes entity WHERE entity.profiles_profile_id = :id")
    Flux<Notes> findByProfiles(Long id);

    @Query("SELECT * FROM notes entity WHERE entity.profiles_profile_id IS NULL")
    Flux<Notes> findAllWhereProfilesIsNull();

    @Override
    <S extends Notes> Mono<S> save(S entity);

    @Override
    Flux<Notes> findAll();

    @Override
    Mono<Notes> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface NotesRepositoryInternal {
    <S extends Notes> Mono<S> save(S entity);

    Flux<Notes> findAllBy(Pageable pageable);

    Flux<Notes> findAll();

    Mono<Notes> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Notes> findAllBy(Pageable pageable, Criteria criteria);

}
