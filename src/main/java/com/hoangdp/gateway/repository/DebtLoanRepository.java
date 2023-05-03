package com.hoangdp.gateway.repository;

import com.hoangdp.gateway.domain.DebtLoan;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the DebtLoan entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DebtLoanRepository extends ReactiveCrudRepository<DebtLoan, Long>, DebtLoanRepositoryInternal {
    Flux<DebtLoan> findAllBy(Pageable pageable);

    @Query("SELECT * FROM debt_loan entity WHERE entity.debts_profile_id = :id")
    Flux<DebtLoan> findByDebts(Long id);

    @Query("SELECT * FROM debt_loan entity WHERE entity.debts_profile_id IS NULL")
    Flux<DebtLoan> findAllWhereDebtsIsNull();

    @Query("SELECT * FROM debt_loan entity WHERE entity.loans_profile_id = :id")
    Flux<DebtLoan> findByLoans(Long id);

    @Query("SELECT * FROM debt_loan entity WHERE entity.loans_profile_id IS NULL")
    Flux<DebtLoan> findAllWhereLoansIsNull();

    @Override
    <S extends DebtLoan> Mono<S> save(S entity);

    @Override
    Flux<DebtLoan> findAll();

    @Override
    Mono<DebtLoan> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface DebtLoanRepositoryInternal {
    <S extends DebtLoan> Mono<S> save(S entity);

    Flux<DebtLoan> findAllBy(Pageable pageable);

    Flux<DebtLoan> findAll();

    Mono<DebtLoan> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<DebtLoan> findAllBy(Pageable pageable, Criteria criteria);

}
