package com.hoangdp.gateway.repository;

import com.hoangdp.gateway.domain.Profiles;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Profiles entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProfilesRepository extends ReactiveCrudRepository<Profiles, Long>, ProfilesRepositoryInternal {
    Flux<Profiles> findAllBy(Pageable pageable);

    @Override
    <S extends Profiles> Mono<S> save(S entity);

    @Override
    Flux<Profiles> findAll();

    @Override
    Mono<Profiles> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ProfilesRepositoryInternal {
    <S extends Profiles> Mono<S> save(S entity);

    Flux<Profiles> findAllBy(Pageable pageable);

    Flux<Profiles> findAll();

    Mono<Profiles> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Profiles> findAllBy(Pageable pageable, Criteria criteria);

}
