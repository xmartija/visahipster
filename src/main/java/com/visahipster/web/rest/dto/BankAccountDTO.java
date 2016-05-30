package com.visahipster.web.rest.dto;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;


/**
 * A DTO for the BankAccount entity.
 */
public class BankAccountDTO implements Serializable {

    private Long id;

    @NotNull
    private String nameOnCard;

    @NotNull
    private String address;

    @NotNull
    private Long accountNumber;

    @NotNull
    private Boolean isDefault;

    private String senderCity;

    private String senderCountryCode;

    private String senderStateCode;


    private Long userId;
    

    private String userLogin;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getNameOnCard() {
        return nameOnCard;
    }

    public void setNameOnCard(String nameOnCard) {
        this.nameOnCard = nameOnCard;
    }
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    public Long getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(Long accountNumber) {
        this.accountNumber = accountNumber;
    }
    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }
    public String getSenderCity() {
        return senderCity;
    }

    public void setSenderCity(String senderCity) {
        this.senderCity = senderCity;
    }
    public String getSenderCountryCode() {
        return senderCountryCode;
    }

    public void setSenderCountryCode(String senderCountryCode) {
        this.senderCountryCode = senderCountryCode;
    }
    public String getSenderStateCode() {
        return senderStateCode;
    }

    public void setSenderStateCode(String senderStateCode) {
        this.senderStateCode = senderStateCode;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }


    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BankAccountDTO bankAccountDTO = (BankAccountDTO) o;

        if ( ! Objects.equals(id, bankAccountDTO.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "BankAccountDTO{" +
            "id=" + id +
            ", nameOnCard='" + nameOnCard + "'" +
            ", address='" + address + "'" +
            ", accountNumber='" + accountNumber + "'" +
            ", isDefault='" + isDefault + "'" +
            ", senderCity='" + senderCity + "'" +
            ", senderCountryCode='" + senderCountryCode + "'" +
            ", senderStateCode='" + senderStateCode + "'" +
            '}';
    }
}
