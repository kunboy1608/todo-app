package com.hoangdp.gateway.repository;

import com.hoangdp.gateway.domain.Conversations;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Conversations entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ConversationsRepository extends ReactiveCrudRepository<Conversations, Long>, ConversationsRepositoryInternal {
    Flux<Conversations> findAllBy(Pageable pageable);

    @Override
    <S extends Conversations> Mono<S> save(S entity);

    @Override
    Flux<Conversations> findAll();

    @Override
    Mono<Conversations> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ConversationsRepositoryInternal {
    <S extends Conversations> Mono<S> save(S entity);

    Flux<Conversations> findAllBy(Pageable pageable);

    Flux<Conversations> findAll();

    Mono<Conversations> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Conversations> findAllBy(Pageable pageable, Criteria criteria);

}
