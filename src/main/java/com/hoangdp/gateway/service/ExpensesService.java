package com.hoangdp.gateway.service;

import com.hoangdp.gateway.domain.Expenses;
import com.hoangdp.gateway.repository.ExpensesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Expenses}.
 */
@Service
@Transactional
public class ExpensesService {

    private final Logger log = LoggerFactory.getLogger(ExpensesService.class);

    private final ExpensesRepository expensesRepository;

    public ExpensesService(ExpensesRepository expensesRepository) {
        this.expensesRepository = expensesRepository;
    }

    /**
     * Save a expenses.
     *
     * @param expenses the entity to save.
     * @return the persisted entity.
     */
    public Mono<Expenses> save(Expenses expenses) {
        log.debug("Request to save Expenses : {}", expenses);
        return expensesRepository.save(expenses);
    }

    /**
     * Update a expenses.
     *
     * @param expenses the entity to save.
     * @return the persisted entity.
     */
    public Mono<Expenses> update(Expenses expenses) {
        log.debug("Request to update Expenses : {}", expenses);
        return expensesRepository.save(expenses);
    }

    /**
     * Partially update a expenses.
     *
     * @param expenses the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<Expenses> partialUpdate(Expenses expenses) {
        log.debug("Request to partially update Expenses : {}", expenses);

        return expensesRepository
            .findById(expenses.getExpenseId())
            .map(existingExpenses -> {
                if (expenses.getOwner() != null) {
                    existingExpenses.setOwner(expenses.getOwner());
                }
                if (expenses.getContent() != null) {
                    existingExpenses.setContent(expenses.getContent());
                }
                if (expenses.getCost() != null) {
                    existingExpenses.setCost(expenses.getCost());
                }
                if (expenses.getTag() != null) {
                    existingExpenses.setTag(expenses.getTag());
                }
                if (expenses.getDay() != null) {
                    existingExpenses.setDay(expenses.getDay());
                }
                if (expenses.getCreatedBy() != null) {
                    existingExpenses.setCreatedBy(expenses.getCreatedBy());
                }
                if (expenses.getCreatedOn() != null) {
                    existingExpenses.setCreatedOn(expenses.getCreatedOn());
                }
                if (expenses.getModifiedBy() != null) {
                    existingExpenses.setModifiedBy(expenses.getModifiedBy());
                }
                if (expenses.getModifiedOn() != null) {
                    existingExpenses.setModifiedOn(expenses.getModifiedOn());
                }

                return existingExpenses;
            })
            .flatMap(expensesRepository::save);
    }

    /**
     * Get all the expenses.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Expenses> findAll(Pageable pageable) {
        log.debug("Request to get all Expenses");
        return expensesRepository.findAllBy(pageable);
    }

    /**
     * Returns the number of expenses available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return expensesRepository.count();
    }

    /**
     * Get one expenses by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<Expenses> findOne(Long id) {
        log.debug("Request to get Expenses : {}", id);
        return expensesRepository.findById(id);
    }

    /**
     * Delete the expenses by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Expenses : {}", id);
        return expensesRepository.deleteById(id);
    }
}
