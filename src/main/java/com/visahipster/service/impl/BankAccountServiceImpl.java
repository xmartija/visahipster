package com.visahipster.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.visahipster.domain.BankAccount;
import com.visahipster.repository.BankAccountRepository;
import com.visahipster.repository.search.BankAccountSearchRepository;
import com.visahipster.security.AuthoritiesConstants;
import com.visahipster.security.SecurityUtils;
import com.visahipster.service.BankAccountService;
import com.visahipster.web.rest.dto.BankAccountDTO;
import com.visahipster.web.rest.mapper.BankAccountMapper;

/**
 * Service Implementation for managing BankAccount.
 */
@Service
@Transactional
public class BankAccountServiceImpl implements BankAccountService{

    private final Logger log = LoggerFactory.getLogger(BankAccountServiceImpl.class);
    
    @Inject
    private BankAccountRepository bankAccountRepository;
    
    @Inject
    private BankAccountMapper bankAccountMapper;
    
    @Inject
    private BankAccountSearchRepository bankAccountSearchRepository;
    
    /**
     * Save a bankAccount.
     * 
     * @param bankAccountDTO the entity to save
     * @return the persisted entity
     */
    public BankAccountDTO save(BankAccountDTO bankAccountDTO) {
        log.debug("Request to save BankAccount : {}", bankAccountDTO);
        BankAccount bankAccount = bankAccountMapper.bankAccountDTOToBankAccount(bankAccountDTO);
        bankAccount = bankAccountRepository.save(bankAccount);
        BankAccountDTO result = bankAccountMapper.bankAccountToBankAccountDTO(bankAccount);
        bankAccountSearchRepository.save(bankAccount);
        return result;
    }

    /**
     *  Get all the bankAccounts.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<BankAccount> findAll(Pageable pageable) {
        log.debug("Request to get all BankAccounts");
        Page<BankAccount> result ;
        if(SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN)){
        	result=bankAccountRepository.findAll(pageable);
        }else{
        	result=bankAccountRepository.findByUserLogin( SecurityUtils.getCurrentUserLogin(),pageable); 
        }
        return result;
    }

    /**
     *  Get one bankAccount by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public BankAccountDTO findOne(Long id) {
        log.debug("Request to get BankAccount : {}", id);
        BankAccount bankAccount = bankAccountRepository.findOne(id);
        BankAccountDTO bankAccountDTO = bankAccountMapper.bankAccountToBankAccountDTO(bankAccount);
        return bankAccountDTO;
    }

    /**
     *  Delete the  bankAccount by id.
     *  
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete BankAccount : {}", id);
        bankAccountRepository.delete(id);
        bankAccountSearchRepository.delete(id);
    }

    /**
     * Search for the bankAccount corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<BankAccount> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of BankAccounts for query {}", query);
        return bankAccountSearchRepository.search(queryStringQuery(query), pageable);
    }


}
