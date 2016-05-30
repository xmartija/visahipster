package com.visahipster.web.rest;

import com.visahipster.VisahipsterApp;
import com.visahipster.domain.Transaction;
import com.visahipster.repository.TransactionRepository;
import com.visahipster.service.TransactionService;
import com.visahipster.repository.search.TransactionSearchRepository;
import com.visahipster.web.rest.dto.TransactionDTO;
import com.visahipster.web.rest.mapper.TransactionMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the TransactionResource REST controller.
 *
 * @see TransactionResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = VisahipsterApp.class)
@WebAppConfiguration
@IntegrationTest
public class TransactionResourceIntTest {


    private static final BigDecimal DEFAULT_AMMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMMOUNT = new BigDecimal(2);
    private static final String DEFAULT_CURRENCY = "AAAAA";
    private static final String UPDATED_CURRENCY = "BBBBB";

    @Inject
    private TransactionRepository transactionRepository;

    @Inject
    private TransactionMapper transactionMapper;

    @Inject
    private TransactionService transactionService;

    @Inject
    private TransactionSearchRepository transactionSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restTransactionMockMvc;

    private Transaction transaction;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TransactionResource transactionResource = new TransactionResource();
        ReflectionTestUtils.setField(transactionResource, "transactionService", transactionService);
        ReflectionTestUtils.setField(transactionResource, "transactionMapper", transactionMapper);
        this.restTransactionMockMvc = MockMvcBuilders.standaloneSetup(transactionResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        transactionSearchRepository.deleteAll();
        transaction = new Transaction();
        transaction.setAmmount(DEFAULT_AMMOUNT);
        transaction.setCurrency(DEFAULT_CURRENCY);
    }

    @Test
    @Transactional
    public void createTransaction() throws Exception {
        int databaseSizeBeforeCreate = transactionRepository.findAll().size();

        // Create the Transaction
        TransactionDTO transactionDTO = transactionMapper.transactionToTransactionDTO(transaction);

        restTransactionMockMvc.perform(post("/api/transactions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(transactionDTO)))
                .andExpect(status().isCreated());

        // Validate the Transaction in the database
        List<Transaction> transactions = transactionRepository.findAll();
        assertThat(transactions).hasSize(databaseSizeBeforeCreate + 1);
        Transaction testTransaction = transactions.get(transactions.size() - 1);
        assertThat(testTransaction.getAmmount()).isEqualTo(DEFAULT_AMMOUNT);
        assertThat(testTransaction.getCurrency()).isEqualTo(DEFAULT_CURRENCY);

        // Validate the Transaction in ElasticSearch
        Transaction transactionEs = transactionSearchRepository.findOne(testTransaction.getId());
        assertThat(transactionEs).isEqualToComparingFieldByField(testTransaction);
    }

    @Test
    @Transactional
    public void checkAmmountIsRequired() throws Exception {
        int databaseSizeBeforeTest = transactionRepository.findAll().size();
        // set the field null
        transaction.setAmmount(null);

        // Create the Transaction, which fails.
        TransactionDTO transactionDTO = transactionMapper.transactionToTransactionDTO(transaction);

        restTransactionMockMvc.perform(post("/api/transactions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(transactionDTO)))
                .andExpect(status().isBadRequest());

        List<Transaction> transactions = transactionRepository.findAll();
        assertThat(transactions).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkCurrencyIsRequired() throws Exception {
        int databaseSizeBeforeTest = transactionRepository.findAll().size();
        // set the field null
        transaction.setCurrency(null);

        // Create the Transaction, which fails.
        TransactionDTO transactionDTO = transactionMapper.transactionToTransactionDTO(transaction);

        restTransactionMockMvc.perform(post("/api/transactions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(transactionDTO)))
                .andExpect(status().isBadRequest());

        List<Transaction> transactions = transactionRepository.findAll();
        assertThat(transactions).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllTransactions() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactions
        restTransactionMockMvc.perform(get("/api/transactions?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(transaction.getId().intValue())))
                .andExpect(jsonPath("$.[*].ammount").value(hasItem(DEFAULT_AMMOUNT.intValue())))
                .andExpect(jsonPath("$.[*].currency").value(hasItem(DEFAULT_CURRENCY.toString())));
    }

    @Test
    @Transactional
    public void getTransaction() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get the transaction
        restTransactionMockMvc.perform(get("/api/transactions/{id}", transaction.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(transaction.getId().intValue()))
            .andExpect(jsonPath("$.ammount").value(DEFAULT_AMMOUNT.intValue()))
            .andExpect(jsonPath("$.currency").value(DEFAULT_CURRENCY.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingTransaction() throws Exception {
        // Get the transaction
        restTransactionMockMvc.perform(get("/api/transactions/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTransaction() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);
        transactionSearchRepository.save(transaction);
        int databaseSizeBeforeUpdate = transactionRepository.findAll().size();

        // Update the transaction
        Transaction updatedTransaction = new Transaction();
        updatedTransaction.setId(transaction.getId());
        updatedTransaction.setAmmount(UPDATED_AMMOUNT);
        updatedTransaction.setCurrency(UPDATED_CURRENCY);
        TransactionDTO transactionDTO = transactionMapper.transactionToTransactionDTO(updatedTransaction);

        restTransactionMockMvc.perform(put("/api/transactions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(transactionDTO)))
                .andExpect(status().isOk());

        // Validate the Transaction in the database
        List<Transaction> transactions = transactionRepository.findAll();
        assertThat(transactions).hasSize(databaseSizeBeforeUpdate);
        Transaction testTransaction = transactions.get(transactions.size() - 1);
        assertThat(testTransaction.getAmmount()).isEqualTo(UPDATED_AMMOUNT);
        assertThat(testTransaction.getCurrency()).isEqualTo(UPDATED_CURRENCY);

        // Validate the Transaction in ElasticSearch
        Transaction transactionEs = transactionSearchRepository.findOne(testTransaction.getId());
        assertThat(transactionEs).isEqualToComparingFieldByField(testTransaction);
    }

    @Test
    @Transactional
    public void deleteTransaction() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);
        transactionSearchRepository.save(transaction);
        int databaseSizeBeforeDelete = transactionRepository.findAll().size();

        // Get the transaction
        restTransactionMockMvc.perform(delete("/api/transactions/{id}", transaction.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean transactionExistsInEs = transactionSearchRepository.exists(transaction.getId());
        assertThat(transactionExistsInEs).isFalse();

        // Validate the database is empty
        List<Transaction> transactions = transactionRepository.findAll();
        assertThat(transactions).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchTransaction() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);
        transactionSearchRepository.save(transaction);

        // Search the transaction
        restTransactionMockMvc.perform(get("/api/_search/transactions?query=id:" + transaction.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transaction.getId().intValue())))
            .andExpect(jsonPath("$.[*].ammount").value(hasItem(DEFAULT_AMMOUNT.intValue())))
            .andExpect(jsonPath("$.[*].currency").value(hasItem(DEFAULT_CURRENCY.toString())));
    }
}
