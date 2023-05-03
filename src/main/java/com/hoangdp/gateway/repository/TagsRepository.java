package com.hoangdp.gateway.repository;

import com.hoangdp.gateway.domain.Tags;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Tags entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TagsRepository extends ReactiveCrudRepository<Tags, Long>, TagsRepositoryInternal {
    Flux<Tags> findAllBy(Pageable pageable);

    @Query("SELECT * FROM tags entity WHERE entity.profiles_profile_id = :id")
    Flux<Tags> findByProfiles(Long id);

    @Query("SELECT * FROM tags entity WHERE entity.profiles_profile_id IS NULL")
    Flux<Tags> findAllWhereProfilesIsNull();

    @Override
    <S extends Tags> Mono<S> save(S entity);

    @Override
    Flux<Tags> findAll();

    @Override
    Mono<Tags> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface TagsRepositoryInternal {
    <S extends Tags> Mono<S> save(S entity);

    Flux<Tags> findAllBy(Pageable pageable);

    Flux<Tags> findAll();

    Mono<Tags> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Tags> findAllBy(Pageable pageable, Criteria criteria);

}
