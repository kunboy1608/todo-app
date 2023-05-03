package com.hoangdp.gateway.repository;

import com.hoangdp.gateway.domain.Types;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Types entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TypesRepository extends ReactiveCrudRepository<Types, Long>, TypesRepositoryInternal {
    Flux<Types> findAllBy(Pageable pageable);

    @Query("SELECT * FROM types entity WHERE entity.profiles_profile_id = :id")
    Flux<Types> findByProfiles(Long id);

    @Query("SELECT * FROM types entity WHERE entity.profiles_profile_id IS NULL")
    Flux<Types> findAllWhereProfilesIsNull();

    @Override
    <S extends Types> Mono<S> save(S entity);

    @Override
    Flux<Types> findAll();

    @Override
    Mono<Types> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface TypesRepositoryInternal {
    <S extends Types> Mono<S> save(S entity);

    Flux<Types> findAllBy(Pageable pageable);

    Flux<Types> findAll();

    Mono<Types> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Types> findAllBy(Pageable pageable, Criteria criteria);

}
