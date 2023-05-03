package com.hoangdp.gateway.repository;

import com.hoangdp.gateway.domain.ConversationsDetails;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the ConversationsDetails entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ConversationsDetailsRepository
    extends ReactiveCrudRepository<ConversationsDetails, Long>, ConversationsDetailsRepositoryInternal {
    Flux<ConversationsDetails> findAllBy(Pageable pageable);

    @Query("SELECT * FROM conversations_details entity WHERE entity.conversations_conversation_id = :id")
    Flux<ConversationsDetails> findByConversations(Long id);

    @Query("SELECT * FROM conversations_details entity WHERE entity.conversations_conversation_id IS NULL")
    Flux<ConversationsDetails> findAllWhereConversationsIsNull();

    @Override
    <S extends ConversationsDetails> Mono<S> save(S entity);

    @Override
    Flux<ConversationsDetails> findAll();

    @Override
    Mono<ConversationsDetails> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ConversationsDetailsRepositoryInternal {
    <S extends ConversationsDetails> Mono<S> save(S entity);

    Flux<ConversationsDetails> findAllBy(Pageable pageable);

    Flux<ConversationsDetails> findAll();

    Mono<ConversationsDetails> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<ConversationsDetails> findAllBy(Pageable pageable, Criteria criteria);

}
