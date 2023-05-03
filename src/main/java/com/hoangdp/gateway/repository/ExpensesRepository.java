package com.hoangdp.gateway.repository;

import com.hoangdp.gateway.domain.Expenses;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Expenses entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ExpensesRepository extends ReactiveCrudRepository<Expenses, Long>, ExpensesRepositoryInternal {
    Flux<Expenses> findAllBy(Pageable pageable);

    @Query("SELECT * FROM expenses entity WHERE entity.types_type_id = :id")
    Flux<Expenses> findByTypes(Long id);

    @Query("SELECT * FROM expenses entity WHERE entity.types_type_id IS NULL")
    Flux<Expenses> findAllWhereTypesIsNull();

    @Query("SELECT * FROM expenses entity WHERE entity.profiles_profile_id = :id")
    Flux<Expenses> findByProfiles(Long id);

    @Query("SELECT * FROM expenses entity WHERE entity.profiles_profile_id IS NULL")
    Flux<Expenses> findAllWhereProfilesIsNull();

    @Override
    <S extends Expenses> Mono<S> save(S entity);

    @Override
    Flux<Expenses> findAll();

    @Override
    Mono<Expenses> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ExpensesRepositoryInternal {
    <S extends Expenses> Mono<S> save(S entity);

    Flux<Expenses> findAllBy(Pageable pageable);

    Flux<Expenses> findAll();

    Mono<Expenses> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Expenses> findAllBy(Pageable pageable, Criteria criteria);

}
