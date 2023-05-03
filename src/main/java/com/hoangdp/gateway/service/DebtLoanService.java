package com.hoangdp.gateway.service;

import com.hoangdp.gateway.domain.DebtLoan;
import com.hoangdp.gateway.repository.DebtLoanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link DebtLoan}.
 */
@Service
@Transactional
public class DebtLoanService {

    private final Logger log = LoggerFactory.getLogger(DebtLoanService.class);

    private final DebtLoanRepository debtLoanRepository;

    public DebtLoanService(DebtLoanRepository debtLoanRepository) {
        this.debtLoanRepository = debtLoanRepository;
    }

    /**
     * Save a debtLoan.
     *
     * @param debtLoan the entity to save.
     * @return the persisted entity.
     */
    public Mono<DebtLoan> save(DebtLoan debtLoan) {
        log.debug("Request to save DebtLoan : {}", debtLoan);
        return debtLoanRepository.save(debtLoan);
    }

    /**
     * Update a debtLoan.
     *
     * @param debtLoan the entity to save.
     * @return the persisted entity.
     */
    public Mono<DebtLoan> update(DebtLoan debtLoan) {
        log.debug("Request to update DebtLoan : {}", debtLoan);
        return debtLoanRepository.save(debtLoan);
    }

    /**
     * Partially update a debtLoan.
     *
     * @param debtLoan the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<DebtLoan> partialUpdate(DebtLoan debtLoan) {
        log.debug("Request to partially update DebtLoan : {}", debtLoan);

        return debtLoanRepository
            .findById(debtLoan.getId())
            .map(existingDebtLoan -> {
                if (debtLoan.getLoanUserId() != null) {
                    existingDebtLoan.setLoanUserId(debtLoan.getLoanUserId());
                }
                if (debtLoan.getDebtUserId() != null) {
                    existingDebtLoan.setDebtUserId(debtLoan.getDebtUserId());
                }
                if (debtLoan.getCost() != null) {
                    existingDebtLoan.setCost(debtLoan.getCost());
                }
                if (debtLoan.getDeadline() != null) {
                    existingDebtLoan.setDeadline(debtLoan.getDeadline());
                }
                if (debtLoan.getDatOfPayment() != null) {
                    existingDebtLoan.setDatOfPayment(debtLoan.getDatOfPayment());
                }

                return existingDebtLoan;
            })
            .flatMap(debtLoanRepository::save);
    }

    /**
     * Get all the debtLoans.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<DebtLoan> findAll(Pageable pageable) {
        log.debug("Request to get all DebtLoans");
        return debtLoanRepository.findAllBy(pageable);
    }

    /**
     * Returns the number of debtLoans available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return debtLoanRepository.count();
    }

    /**
     * Get one debtLoan by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<DebtLoan> findOne(Long id) {
        log.debug("Request to get DebtLoan : {}", id);
        return debtLoanRepository.findById(id);
    }

    /**
     * Delete the debtLoan by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete DebtLoan : {}", id);
        return debtLoanRepository.deleteById(id);
    }
}
