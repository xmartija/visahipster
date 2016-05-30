package com.visahipster.service;

import com.visahipster.domain.BankAccount;
import com.visahipster.web.rest.dto.BankAccountDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.LinkedList;
import java.util.List;

/**
 * Service Interface for managing BankAccount.
 */
public interface BankAccountService {

    /**
     * Save a bankAccount.
     * 
     * @param bankAccountDTO the entity to save
     * @return the persisted entity
     */
    BankAccountDTO save(BankAccountDTO bankAccountDTO);

    /**
     *  Get all the bankAccounts.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<BankAccount> findAll(Pageable pageable);

    /**
     *  Get the "id" bankAccount.
     *  
     *  @param id the id of the entity
     *  @return the entity
     */
    BankAccountDTO findOne(Long id);

    /**
     *  Delete the "id" bankAccount.
     *  
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the bankAccount corresponding to the query.
     * 
     *  @param query the query of the search
     *  @return the list of entities
     */
    Page<BankAccount> search(String query, Pageable pageable);
}
