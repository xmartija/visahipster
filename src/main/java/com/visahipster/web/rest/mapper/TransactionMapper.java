package com.visahipster.web.rest.mapper;

import com.visahipster.domain.*;
import com.visahipster.web.rest.dto.TransactionDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity Transaction and its DTO TransactionDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface TransactionMapper {

    @Mapping(source = "reciever.id", target = "recieverId")
    @Mapping(source = "sender.id", target = "senderId")
    TransactionDTO transactionToTransactionDTO(Transaction transaction);

    List<TransactionDTO> transactionsToTransactionDTOs(List<Transaction> transactions);

    @Mapping(source = "recieverId", target = "reciever")
    @Mapping(source = "senderId", target = "sender")
    Transaction transactionDTOToTransaction(TransactionDTO transactionDTO);

    List<Transaction> transactionDTOsToTransactions(List<TransactionDTO> transactionDTOs);

    default BankAccount bankAccountFromId(Long id) {
        if (id == null) {
            return null;
        }
        BankAccount bankAccount = new BankAccount();
        bankAccount.setId(id);
        return bankAccount;
    }
}
