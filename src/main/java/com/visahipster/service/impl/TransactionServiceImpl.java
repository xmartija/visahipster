package com.visahipster.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.visahipster.domain.BankAccount;
import com.visahipster.domain.Transaction;
import com.visahipster.repository.TransactionRepository;
import com.visahipster.repository.search.TransactionSearchRepository;
import com.visahipster.service.BankAccountService;
import com.visahipster.service.TransactionService;
import com.visahipster.web.rest.dto.TransactionDTO;
import com.visahipster.web.rest.mapper.BankAccountMapper;
import com.visahipster.web.rest.mapper.TransactionMapper;

import visadirect.client.PushFundsClient;
import visadirect.model.Address;
import visadirect.model.CardAcceptor;
import visadirect.model.PointOfServiceData;
import visadirect.model.PushFundsBody;
import visadirect.model.PushFundsResponse;

/**
 * Service Implementation for managing Transaction.
 */
@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private final Logger log = LoggerFactory
            .getLogger(TransactionServiceImpl.class);

    @Autowired
    PushFundsClient aPushFundsClient;

    @Autowired
    BankAccountMapper aBankAccountMapper;

    @Autowired
    BankAccountService aBankAccountService;

    @Inject
    private TransactionRepository transactionRepository;

    @Inject
    private TransactionMapper transactionMapper;

    @Inject
    private TransactionSearchRepository transactionSearchRepository;

    /**
     * Save a transaction.
     * 
     * @param transactionDTO
     *            the entity to save
     * @return the persisted entity
     */
    public TransactionDTO save(TransactionDTO transactionDTO) {
        log.debug("Request to save Transaction : {}", transactionDTO);

        // Meat here
        PushFundsResponse aPushFundsResponse = performPushFunds(transactionDTO);

        Transaction transaction = transactionMapper
                .transactionDTOToTransaction(transactionDTO);
        transaction = transactionRepository.save(transaction);
        TransactionDTO result = transactionMapper
                .transactionToTransactionDTO(transaction);
        transactionSearchRepository.save(transaction);
        return result;
    }

    private PushFundsResponse performPushFunds(TransactionDTO transactionDTO) {

        BankAccount recieverBankAccount = aBankAccountMapper
                .bankAccountDTOToBankAccount(aBankAccountService
                        .findOne(transactionDTO.getRecieverId()));
        BankAccount senderBankAccount = aBankAccountMapper
                .bankAccountDTOToBankAccount(aBankAccountService
                        .findOne(transactionDTO.getSenderId()));

        PushFundsBody aPushFundsBody = new PushFundsBody();

        aPushFundsBody.initialize();

        aPushFundsBody
                .setAmount(transactionDTO.getAmmount().toEngineeringString());
        aPushFundsBody.setTransactionCurrencyCode(transactionDTO.getCurrency());

        aPushFundsBody
                .setSenderStateCode(senderBankAccount.getSenderStateCode());
        aPushFundsBody.setSenderAccountNumber(
                senderBankAccount.getAccountNumber().toString());
        aPushFundsBody.setSenderAddress(senderBankAccount.getAddress());
        aPushFundsBody.setSenderCity(senderBankAccount.getSenderCity());
        aPushFundsBody
                .setSenderCountryCode(senderBankAccount.getSenderCountryCode());
        aPushFundsBody.setSenderName(senderBankAccount.getNameOnCard());

        aPushFundsBody.setRecipientName(recieverBankAccount.getNameOnCard());
        aPushFundsBody.setRecipientPrimaryAccountNumber(
                recieverBankAccount.getAccountNumber().toString());

        // 2016-05-23T09:44:17 usually GMT
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));

        aPushFundsBody
                .setLocalTransactionDateTime(dateFormatGmt.format(new Date()));

        ResponseEntity<PushFundsResponse> response = null;

        try {

            response = aPushFundsClient.postRequest(aPushFundsBody);

            if ((response.getStatusCode().value() != 200)) {
                throw new RuntimeException("not successfull: " + response);
            }
        } catch (JsonParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return response.getBody();

    }

    /**
     * Get all the transactions.
     * 
     * @param pageable
     *            the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Transaction> findAll(Pageable pageable) {
        log.debug("Request to get all Transactions");
        Page<Transaction> result = transactionRepository.findAll(pageable);
        return result;
    }

    /**
     * Get one transaction by id.
     *
     * @param id
     *            the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public TransactionDTO findOne(Long id) {
        log.debug("Request to get Transaction : {}", id);
        Transaction transaction = transactionRepository.findOne(id);
        TransactionDTO transactionDTO = transactionMapper
                .transactionToTransactionDTO(transaction);
        return transactionDTO;
    }

    /**
     * Delete the transaction by id.
     * 
     * @param id
     *            the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Transaction : {}", id);
        transactionRepository.delete(id);
        transactionSearchRepository.delete(id);
    }

    /**
     * Search for the transaction corresponding to the query.
     *
     * @param query
     *            the query of the search
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Transaction> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Transactions for query {}",
                query);
        return transactionSearchRepository.search(queryStringQuery(query),
                pageable);
    }
}
