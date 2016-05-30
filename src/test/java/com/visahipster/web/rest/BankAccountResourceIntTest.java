package com.visahipster.web.rest;

import com.visahipster.VisahipsterApp;
import com.visahipster.domain.BankAccount;
import com.visahipster.repository.BankAccountRepository;
import com.visahipster.service.BankAccountService;
import com.visahipster.repository.search.BankAccountSearchRepository;
import com.visahipster.web.rest.dto.BankAccountDTO;
import com.visahipster.web.rest.mapper.BankAccountMapper;

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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the BankAccountResource REST controller.
 *
 * @see BankAccountResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = VisahipsterApp.class)
@WebAppConfiguration
@IntegrationTest
public class BankAccountResourceIntTest {

    private static final String DEFAULT_NAME_ON_CARD = "AAAAA";
    private static final String UPDATED_NAME_ON_CARD = "BBBBB";
    private static final String DEFAULT_ADDRESS = "AAAAA";
    private static final String UPDATED_ADDRESS = "BBBBB";

    private static final Long DEFAULT_ACCOUNT_NUMBER = 1L;
    private static final Long UPDATED_ACCOUNT_NUMBER = 2L;

    private static final Boolean DEFAULT_IS_DEFAULT = false;
    private static final Boolean UPDATED_IS_DEFAULT = true;
    private static final String DEFAULT_SENDER_CITY = "AAAAA";
    private static final String UPDATED_SENDER_CITY = "BBBBB";
    private static final String DEFAULT_SENDER_COUNTRY_CODE = "AAAAA";
    private static final String UPDATED_SENDER_COUNTRY_CODE = "BBBBB";
    private static final String DEFAULT_SENDER_STATE_CODE = "AAAAA";
    private static final String UPDATED_SENDER_STATE_CODE = "BBBBB";

    @Inject
    private BankAccountRepository bankAccountRepository;

    @Inject
    private BankAccountMapper bankAccountMapper;

    @Inject
    private BankAccountService bankAccountService;

    @Inject
    private BankAccountSearchRepository bankAccountSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restBankAccountMockMvc;

    private BankAccount bankAccount;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        BankAccountResource bankAccountResource = new BankAccountResource();
        ReflectionTestUtils.setField(bankAccountResource, "bankAccountService", bankAccountService);
        ReflectionTestUtils.setField(bankAccountResource, "bankAccountMapper", bankAccountMapper);
        this.restBankAccountMockMvc = MockMvcBuilders.standaloneSetup(bankAccountResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        bankAccountSearchRepository.deleteAll();
        bankAccount = new BankAccount();
        bankAccount.setNameOnCard(DEFAULT_NAME_ON_CARD);
        bankAccount.setAddress(DEFAULT_ADDRESS);
        bankAccount.setAccountNumber(DEFAULT_ACCOUNT_NUMBER);
        bankAccount.setIsDefault(DEFAULT_IS_DEFAULT);
        bankAccount.setSenderCity(DEFAULT_SENDER_CITY);
        bankAccount.setSenderCountryCode(DEFAULT_SENDER_COUNTRY_CODE);
        bankAccount.setSenderStateCode(DEFAULT_SENDER_STATE_CODE);
    }

    @Test
    @Transactional
    public void createBankAccount() throws Exception {
        int databaseSizeBeforeCreate = bankAccountRepository.findAll().size();

        // Create the BankAccount
        BankAccountDTO bankAccountDTO = bankAccountMapper.bankAccountToBankAccountDTO(bankAccount);

        restBankAccountMockMvc.perform(post("/api/bank-accounts")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(bankAccountDTO)))
                .andExpect(status().isCreated());

        // Validate the BankAccount in the database
        List<BankAccount> bankAccounts = bankAccountRepository.findAll();
        assertThat(bankAccounts).hasSize(databaseSizeBeforeCreate + 1);
        BankAccount testBankAccount = bankAccounts.get(bankAccounts.size() - 1);
        assertThat(testBankAccount.getNameOnCard()).isEqualTo(DEFAULT_NAME_ON_CARD);
        assertThat(testBankAccount.getAddress()).isEqualTo(DEFAULT_ADDRESS);
        assertThat(testBankAccount.getAccountNumber()).isEqualTo(DEFAULT_ACCOUNT_NUMBER);
        assertThat(testBankAccount.isIsDefault()).isEqualTo(DEFAULT_IS_DEFAULT);
        assertThat(testBankAccount.getSenderCity()).isEqualTo(DEFAULT_SENDER_CITY);
        assertThat(testBankAccount.getSenderCountryCode()).isEqualTo(DEFAULT_SENDER_COUNTRY_CODE);
        assertThat(testBankAccount.getSenderStateCode()).isEqualTo(DEFAULT_SENDER_STATE_CODE);

        // Validate the BankAccount in ElasticSearch
        BankAccount bankAccountEs = bankAccountSearchRepository.findOne(testBankAccount.getId());
        assertThat(bankAccountEs).isEqualToComparingFieldByField(testBankAccount);
    }

    @Test
    @Transactional
    public void checkNameOnCardIsRequired() throws Exception {
        int databaseSizeBeforeTest = bankAccountRepository.findAll().size();
        // set the field null
        bankAccount.setNameOnCard(null);

        // Create the BankAccount, which fails.
        BankAccountDTO bankAccountDTO = bankAccountMapper.bankAccountToBankAccountDTO(bankAccount);

        restBankAccountMockMvc.perform(post("/api/bank-accounts")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(bankAccountDTO)))
                .andExpect(status().isBadRequest());

        List<BankAccount> bankAccounts = bankAccountRepository.findAll();
        assertThat(bankAccounts).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkAddressIsRequired() throws Exception {
        int databaseSizeBeforeTest = bankAccountRepository.findAll().size();
        // set the field null
        bankAccount.setAddress(null);

        // Create the BankAccount, which fails.
        BankAccountDTO bankAccountDTO = bankAccountMapper.bankAccountToBankAccountDTO(bankAccount);

        restBankAccountMockMvc.perform(post("/api/bank-accounts")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(bankAccountDTO)))
                .andExpect(status().isBadRequest());

        List<BankAccount> bankAccounts = bankAccountRepository.findAll();
        assertThat(bankAccounts).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkAccountNumberIsRequired() throws Exception {
        int databaseSizeBeforeTest = bankAccountRepository.findAll().size();
        // set the field null
        bankAccount.setAccountNumber(null);

        // Create the BankAccount, which fails.
        BankAccountDTO bankAccountDTO = bankAccountMapper.bankAccountToBankAccountDTO(bankAccount);

        restBankAccountMockMvc.perform(post("/api/bank-accounts")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(bankAccountDTO)))
                .andExpect(status().isBadRequest());

        List<BankAccount> bankAccounts = bankAccountRepository.findAll();
        assertThat(bankAccounts).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkIsDefaultIsRequired() throws Exception {
        int databaseSizeBeforeTest = bankAccountRepository.findAll().size();
        // set the field null
        bankAccount.setIsDefault(null);

        // Create the BankAccount, which fails.
        BankAccountDTO bankAccountDTO = bankAccountMapper.bankAccountToBankAccountDTO(bankAccount);

        restBankAccountMockMvc.perform(post("/api/bank-accounts")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(bankAccountDTO)))
                .andExpect(status().isBadRequest());

        List<BankAccount> bankAccounts = bankAccountRepository.findAll();
        assertThat(bankAccounts).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllBankAccounts() throws Exception {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount);

        // Get all the bankAccounts
        restBankAccountMockMvc.perform(get("/api/bank-accounts?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(bankAccount.getId().intValue())))
                .andExpect(jsonPath("$.[*].nameOnCard").value(hasItem(DEFAULT_NAME_ON_CARD.toString())))
                .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS.toString())))
                .andExpect(jsonPath("$.[*].accountNumber").value(hasItem(DEFAULT_ACCOUNT_NUMBER.intValue())))
                .andExpect(jsonPath("$.[*].isDefault").value(hasItem(DEFAULT_IS_DEFAULT.booleanValue())))
                .andExpect(jsonPath("$.[*].senderCity").value(hasItem(DEFAULT_SENDER_CITY.toString())))
                .andExpect(jsonPath("$.[*].senderCountryCode").value(hasItem(DEFAULT_SENDER_COUNTRY_CODE.toString())))
                .andExpect(jsonPath("$.[*].senderStateCode").value(hasItem(DEFAULT_SENDER_STATE_CODE.toString())));
    }

    @Test
    @Transactional
    public void getBankAccount() throws Exception {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount);

        // Get the bankAccount
        restBankAccountMockMvc.perform(get("/api/bank-accounts/{id}", bankAccount.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(bankAccount.getId().intValue()))
            .andExpect(jsonPath("$.nameOnCard").value(DEFAULT_NAME_ON_CARD.toString()))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS.toString()))
            .andExpect(jsonPath("$.accountNumber").value(DEFAULT_ACCOUNT_NUMBER.intValue()))
            .andExpect(jsonPath("$.isDefault").value(DEFAULT_IS_DEFAULT.booleanValue()))
            .andExpect(jsonPath("$.senderCity").value(DEFAULT_SENDER_CITY.toString()))
            .andExpect(jsonPath("$.senderCountryCode").value(DEFAULT_SENDER_COUNTRY_CODE.toString()))
            .andExpect(jsonPath("$.senderStateCode").value(DEFAULT_SENDER_STATE_CODE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingBankAccount() throws Exception {
        // Get the bankAccount
        restBankAccountMockMvc.perform(get("/api/bank-accounts/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBankAccount() throws Exception {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount);
        bankAccountSearchRepository.save(bankAccount);
        int databaseSizeBeforeUpdate = bankAccountRepository.findAll().size();

        // Update the bankAccount
        BankAccount updatedBankAccount = new BankAccount();
        updatedBankAccount.setId(bankAccount.getId());
        updatedBankAccount.setNameOnCard(UPDATED_NAME_ON_CARD);
        updatedBankAccount.setAddress(UPDATED_ADDRESS);
        updatedBankAccount.setAccountNumber(UPDATED_ACCOUNT_NUMBER);
        updatedBankAccount.setIsDefault(UPDATED_IS_DEFAULT);
        updatedBankAccount.setSenderCity(UPDATED_SENDER_CITY);
        updatedBankAccount.setSenderCountryCode(UPDATED_SENDER_COUNTRY_CODE);
        updatedBankAccount.setSenderStateCode(UPDATED_SENDER_STATE_CODE);
        BankAccountDTO bankAccountDTO = bankAccountMapper.bankAccountToBankAccountDTO(updatedBankAccount);

        restBankAccountMockMvc.perform(put("/api/bank-accounts")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(bankAccountDTO)))
                .andExpect(status().isOk());

        // Validate the BankAccount in the database
        List<BankAccount> bankAccounts = bankAccountRepository.findAll();
        assertThat(bankAccounts).hasSize(databaseSizeBeforeUpdate);
        BankAccount testBankAccount = bankAccounts.get(bankAccounts.size() - 1);
        assertThat(testBankAccount.getNameOnCard()).isEqualTo(UPDATED_NAME_ON_CARD);
        assertThat(testBankAccount.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testBankAccount.getAccountNumber()).isEqualTo(UPDATED_ACCOUNT_NUMBER);
        assertThat(testBankAccount.isIsDefault()).isEqualTo(UPDATED_IS_DEFAULT);
        assertThat(testBankAccount.getSenderCity()).isEqualTo(UPDATED_SENDER_CITY);
        assertThat(testBankAccount.getSenderCountryCode()).isEqualTo(UPDATED_SENDER_COUNTRY_CODE);
        assertThat(testBankAccount.getSenderStateCode()).isEqualTo(UPDATED_SENDER_STATE_CODE);

        // Validate the BankAccount in ElasticSearch
        BankAccount bankAccountEs = bankAccountSearchRepository.findOne(testBankAccount.getId());
        assertThat(bankAccountEs).isEqualToComparingFieldByField(testBankAccount);
    }

    @Test
    @Transactional
    public void deleteBankAccount() throws Exception {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount);
        bankAccountSearchRepository.save(bankAccount);
        int databaseSizeBeforeDelete = bankAccountRepository.findAll().size();

        // Get the bankAccount
        restBankAccountMockMvc.perform(delete("/api/bank-accounts/{id}", bankAccount.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean bankAccountExistsInEs = bankAccountSearchRepository.exists(bankAccount.getId());
        assertThat(bankAccountExistsInEs).isFalse();

        // Validate the database is empty
        List<BankAccount> bankAccounts = bankAccountRepository.findAll();
        assertThat(bankAccounts).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchBankAccount() throws Exception {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount);
        bankAccountSearchRepository.save(bankAccount);

        // Search the bankAccount
        restBankAccountMockMvc.perform(get("/api/_search/bank-accounts?query=id:" + bankAccount.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bankAccount.getId().intValue())))
            .andExpect(jsonPath("$.[*].nameOnCard").value(hasItem(DEFAULT_NAME_ON_CARD.toString())))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS.toString())))
            .andExpect(jsonPath("$.[*].accountNumber").value(hasItem(DEFAULT_ACCOUNT_NUMBER.intValue())))
            .andExpect(jsonPath("$.[*].isDefault").value(hasItem(DEFAULT_IS_DEFAULT.booleanValue())))
            .andExpect(jsonPath("$.[*].senderCity").value(hasItem(DEFAULT_SENDER_CITY.toString())))
            .andExpect(jsonPath("$.[*].senderCountryCode").value(hasItem(DEFAULT_SENDER_COUNTRY_CODE.toString())))
            .andExpect(jsonPath("$.[*].senderStateCode").value(hasItem(DEFAULT_SENDER_STATE_CODE.toString())));
    }
}
