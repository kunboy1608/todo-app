package com.hoangdp.gateway.repository;

import com.hoangdp.gateway.domain.Relationship;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Relationship entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RelationshipRepository extends ReactiveCrudRepository<Relationship, Long>, RelationshipRepositoryInternal {
    Flux<Relationship> findAllBy(Pageable pageable);

    @Override
    <S extends Relationship> Mono<S> save(S entity);

    @Override
    Flux<Relationship> findAll();

    @Override
    Mono<Relationship> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface RelationshipRepositoryInternal {
    <S extends Relationship> Mono<S> save(S entity);

    Flux<Relationship> findAllBy(Pageable pageable);

    Flux<Relationship> findAll();

    Mono<Relationship> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Relationship> findAllBy(Pageable pageable, Criteria criteria);

}
