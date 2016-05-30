package com.visahipster.web.rest.dto;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;


/**
 * A DTO for the Transaction entity.
 */
public class TransactionDTO implements Serializable {

    private Long id;

    @NotNull
    private BigDecimal ammount;

    @NotNull
    private String currency;


    private Long recieverId;
    
    private Long senderId;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public BigDecimal getAmmount() {
        return ammount;
    }

    public void setAmmount(BigDecimal ammount) {
        this.ammount = ammount;
    }
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Long getRecieverId() {
        return recieverId;
    }

    public void setRecieverId(Long bankAccountId) {
        this.recieverId = bankAccountId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long bankAccountId) {
        this.senderId = bankAccountId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TransactionDTO transactionDTO = (TransactionDTO) o;

        if ( ! Objects.equals(id, transactionDTO.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "TransactionDTO{" +
            "id=" + id +
            ", ammount='" + ammount + "'" +
            ", currency='" + currency + "'" +
            '}';
    }
}
