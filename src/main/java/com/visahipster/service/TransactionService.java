package com.visahipster.service;

import com.visahipster.domain.Transaction;
import com.visahipster.web.rest.dto.TransactionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.LinkedList;
import java.util.List;

/**
 * Service Interface for managing Transaction.
 */
public interface TransactionService {

    /**
     * Save a transaction.
     * 
     * @param transactionDTO the entity to save
     * @return the persisted entity
     */
    TransactionDTO save(TransactionDTO transactionDTO);

    /**
     *  Get all the transactions.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<Transaction> findAll(Pageable pageable);

    /**
     *  Get the "id" transaction.
     *  
     *  @param id the id of the entity
     *  @return the entity
     */
    TransactionDTO findOne(Long id);

    /**
     *  Delete the "id" transaction.
     *  
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the transaction corresponding to the query.
     * 
     *  @param query the query of the search
     *  @return the list of entities
     */
    Page<Transaction> search(String query, Pageable pageable);
}
